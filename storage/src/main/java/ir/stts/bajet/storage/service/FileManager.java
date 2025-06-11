package ir.stts.bajet.storage.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.*;
import ir.stts.bajet.core.constant.BajetConstants;
import ir.stts.bajet.core.resilience.constant.IErrorCode;
import ir.stts.bajet.core.resilience.exception.ForbiddenException;
import ir.stts.bajet.core.security.LegacyUserData;
import ir.stts.bajet.core.security.UserDataHolder;

import ir.stts.bajet.storage.config.MinioProperties;
import ir.stts.bajet.storage.dto.MinioTagDto;
import ir.stts.bajet.storage.dto.constant.MinioTagKey;
import ir.stts.bajet.storage.dto.req.MinioPresignedUrlReqDto;
import ir.stts.bajet.storage.dto.req.MinioUploadReqDto;
import ir.stts.bajet.storage.dto.resp.MinioBucketRespDto;
import ir.stts.bajet.storage.dto.resp.MinioDownloadRespDto;
import ir.stts.bajet.storage.dto.resp.MinioMetadataRespDto;
import ir.stts.bajet.storage.exception.FmsException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = BajetConstants.BAJET_BASE_PACKAGE + ".minio",
        name = "enabled",
        havingValue = "true"
)
public class FileManager {
    private final MinioClient minioClient;
    private final MinioProperties properties;
    private ServerSideEncryptionCustomerKey sse;

    @PostConstruct
    private void init() throws NoSuchAlgorithmException, InvalidKeyException {
        if (properties.isSecure()) {
            String key = properties.getEncryptionKey();

            byte[] bytes = Base64.getDecoder().decode(key);
            if (bytes.length != 32)
                throw new IllegalArgumentException("Encryption key must be exactly 32 bytes (256 bits) in length. Please provide a valid AES-256 key.");

            sse = new ServerSideEncryptionCustomerKey(new SecretKeySpec(bytes, "AES"));
        }
    }

    public String upload(@Valid MinioUploadReqDto data) throws FmsException {

        try {
            InputStream inputStream = new ByteArrayInputStream(data.getFile());

            PutObjectArgs.Builder argBuilder = PutObjectArgs
                    .builder()
                    .bucket(data.getBucketName())
                    .object(data.getName())
                    .tags(data.getTags().getTagMap())
                    .stream(
                            inputStream,
                            inputStream.available(),
                            -1);

            if (properties.isSecure()) {
                argBuilder.sse(sse);
            }

            ObjectWriteResponse response = minioClient.putObject(argBuilder.build());
            Duration protectionTime = data.getProtectionTime();
            ObjectLockConfiguration config = minioClient.getObjectLockConfiguration(
                    GetObjectLockConfigurationArgs.builder()
                            .bucket(data.getBucketName())
                            .build());

            if (config != null && protectionTime != null && protectionTime.isPositive()) {

                Retention retention = new Retention(RetentionMode.GOVERNANCE, ZonedDateTime.now().plus(protectionTime));
                minioClient.setObjectRetention(SetObjectRetentionArgs.builder()
                        .bucket(data.getBucketName())
                        .object(data.getName())
                        .config(retention)
                        .versionId(response.versionId())
                        .bypassGovernanceMode(data.isBypassGovernanceMode())
                        .build());
            }

            return response.versionId();
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10023, e);
        }
    }

    public MinioDownloadRespDto download(@NotBlank String bucketName, @NotBlank String name, String versionId) throws FmsException {

        try {

            GetObjectArgs.Builder argBuilder = GetObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .versionId(versionId);

            if (properties.isSecure()) {
                argBuilder.ssec(sse);
            }

            final InputStream inputStream = minioClient.getObject(argBuilder.build());

            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();

            Tags tags = minioClient.getObjectTags(GetObjectTagsArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .versionId(versionId)
                    .build());

            return new MinioDownloadRespDto()
                    .setFile(fileBytes)
                    .setDeleted(Boolean.parseBoolean(tags.get().get(MinioTagKey.DELETED.name())));
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10024, e);
        }
    }

    public void restore(@NotBlank String bucketName, @NotBlank String name, String versionId) throws FmsException {

        try {

            Tags tags = minioClient.getObjectTags(GetObjectTagsArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .versionId(versionId)
                    .build());

            Map<String, String> tagMap = new HashMap<>(tags.get());
            tagMap.put(MinioTagKey.DELETED.name(), "false");
            minioClient.setObjectTags(SetObjectTagsArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .versionId(versionId)
                    .tags(Tags.newObjectTags(tagMap))
                    .build());
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10031, e);
        }
    }

    public void delete(@NotBlank String bucketName, @NotBlank String name, String versionId, boolean permanently) throws FmsException {
        try {
            if (permanently)
                minioClient.removeObject(RemoveObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(name)
                        .versionId(versionId)
                        .build());
            else {
                Tags tags = minioClient.getObjectTags(GetObjectTagsArgs
                        .builder()
                        .bucket(bucketName)
                        .object(name)
                        .versionId(versionId)
                        .build());

                Map<String, String> tagMap = new HashMap<>(tags.get());
                tagMap.put(MinioTagKey.DELETED.name(), "true");
                minioClient.setObjectTags(SetObjectTagsArgs
                        .builder()
                        .bucket(bucketName)
                        .object(name)
                        .versionId(versionId)
                        .tags(Tags.newObjectTags(tagMap))
                        .build());
            }
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10025, e);
        }
    }

    public String preSignedUrl2Upload(@Valid MinioPresignedUrlReqDto data) throws FmsException {
        try {
            LegacyUserData legacyUserData = UserDataHolder.get();
            HashMap<String, String> headers = new HashMap<>();

            if (legacyUserData != null) {
                headers.put("device_id", legacyUserData.getClientInfo().getDeviceId());
                headers.put("user_id", legacyUserData.getUserInfo().getUserId());
                headers.put("national_code", legacyUserData.getUserInfo().getNationalCode());
                headers.put("username", legacyUserData.getUserInfo().getUsername());
            }

            Map<String, String> params = new HashMap<>() {{
                put("response-content-type", properties.getPreSignedUrlContentType());
            }};

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.PUT)
                    .bucket(data.getBucketName())
                    .object(data.getName())
                    .versionId(data.getVersionId())
                    .extraHeaders(headers)
                    .extraQueryParams(params)
                    .expiry(properties.getPreSignedUrlUploadExpirationInMinutes(), TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10026, e);
        }
    }

    public String preSignedUrl2Download(@Valid MinioPresignedUrlReqDto data) throws FmsException {
        try {
            LegacyUserData legacyUserData = UserDataHolder.get();
            HashMap<String, String> headers = new HashMap<>();
            if (legacyUserData != null) {
                headers.put("device_id", legacyUserData.getClientInfo().getDeviceId());
                headers.put("user_id", legacyUserData.getUserInfo().getUserId());
                headers.put("national_code", legacyUserData.getUserInfo().getNationalCode());
                headers.put("username", legacyUserData.getUserInfo().getUsername());
            }

            Map<String, String> params = new HashMap<>() {{
                put("response-content-type", properties.getPreSignedUrlContentType());
            }};

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                    .builder()
                    .method(Method.GET)
                    .bucket(data.getBucketName())
                    .object(data.getName())
                    .versionId(data.getVersionId())
                    .expiry(properties.getPreSignedUrlDownloadExpirationInMinutes(), TimeUnit.MINUTES)
                    .extraQueryParams(params)
                    .extraHeaders(headers)
                    .build());
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10027, e);
        }
    }

    public MinioMetadataRespDto metadata(@NotBlank String bucketName, @NotBlank String name, String versionId) throws FmsException {

        try {

            StatObjectArgs.Builder argBuilder = StatObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .versionId(versionId);

            if (properties.isSecure()) {
                argBuilder.ssec(sse);
            }

            StatObjectResponse statObjectResponse = minioClient.statObject(argBuilder.build());

            MinioMetadataRespDto minioMetadataRespDto = new MinioMetadataRespDto()
                    .setSize(statObjectResponse.size())
                    .setEtag(statObjectResponse.etag())
                    .setDeleteMarker(statObjectResponse.deleteMarker())
                    .setLegalHold(statObjectResponse.legalHold().status())
                    .setRetentionMode(statObjectResponse.retentionMode() == null ? null : statObjectResponse.retentionMode().name())
                    .setRetentionRetainUntilDate(statObjectResponse.retentionRetainUntilDate())
                    .setLastModified(statObjectResponse.lastModified());

            Tags tags = minioClient.getObjectTags(GetObjectTagsArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .versionId(versionId)
                    .build());
            Map<String, String> tagMap = tags.get();
            MinioTagDto minioTagDto = new MinioTagDto()
                    .setUploadedBy(tagMap.get(MinioTagKey.UPLOADED_BY.name()))
                    .setOriginalName(tagMap.get(MinioTagKey.ORIGINAL_NAME.name()))
                    .setIdentifier(tagMap.get(MinioTagKey.IDENTIFIER.name()))
                    .setDataIdentifier(tagMap.get(MinioTagKey.DATA_IDENTIFIER.name()))
                    .setDocumentIdentifier(tagMap.get(MinioTagKey.DOCUMENT_IDENTIFIER.name()))
                    .setDeleted(Boolean.parseBoolean(tagMap.get(MinioTagKey.DELETED.name())));

            Map<String, String> extras = tagMap
                    .entrySet()
                    .stream()
                    .filter(e -> Arrays
                            .stream(MinioTagKey.values())
                            .noneMatch(k -> k.name().equalsIgnoreCase(e.getKey())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            minioTagDto.setExtras(extras);
            minioMetadataRespDto.setTags(minioTagDto);

            return minioMetadataRespDto;
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10028, e);
        }
    }

    public List<MinioBucketRespDto> buckets() throws FmsException {

        try {

            List<Bucket> buckets = minioClient.listBuckets();
            List<MinioBucketRespDto> minioBucketRespDtoList = new ArrayList<>(buckets.size());
            for (Bucket bucket : buckets) {

                MinioBucketRespDto minioBucketRespDto = new MinioBucketRespDto()
                        .setName(bucket.name())
                        .setCreationDate(bucket.creationDate());

                minioBucketRespDtoList.add(minioBucketRespDto);
            }

            return minioBucketRespDtoList;
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10029, e);
        }
    }

    public boolean createBucket(@NotBlank String bucketName, boolean isPublic, boolean allowVersioning, boolean activeObjectLock) throws FmsException {

        try {

            boolean isExist = minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(bucketName)
                    .build());
            if (isExist)
                return false;

            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .objectLock(activeObjectLock)
                    .build());

            if (allowVersioning) {

                VersioningConfiguration versioningConfig = new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, false);
                minioClient.setBucketVersioning(SetBucketVersioningArgs
                        .builder()
                        .bucket(bucketName)
                        .config(versioningConfig)
                        .build());
            }

            String policy;
            if (isPublic)
                policy = "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Action\": [\n" +
                        "        \"s3:GetObject\"\n" +
                        "      ],\n" +
                        "      \"Resource\": [\n" +
                        "        \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
            else
                policy = "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": {\n" +
                        "        \"AWS\": \"arn:aws:iam::minio-instance:user/" + properties.getAccessKey() + "\"\n" +
                        "      },\n" +
                        "      \"Action\": [\n" +
                        "        \"s3:*\"\n" +
                        "      ],\n" +
                        "      \"Resource\": [\n" +
                        "        \"arn:aws:s3:::" + bucketName + "\",\n" +
                        "        \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Action\": [\n" +
                        "        \"s3:ListBucket\"\n" +
                        "      ],\n" +
                        "      \"Resource\": [\n" +
                        "        \"arn:aws:s3:::" + bucketName + "\"\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n";
            minioClient.setBucketPolicy(SetBucketPolicyArgs
                    .builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build());


            return true;
        } catch (Exception e) {
            throw new FmsException(IErrorCode._INFR_10030, e);
        }
    }

    public boolean isExist(@NotBlank String objectPath, @NotBlank String bucketName) throws FmsException {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectPath)
                .build()
            );

            return true;
        }
        catch (MinioException ex) {
            return false;
        }
        catch (Exception ex) {
            throw new FmsException(IErrorCode._INFR_10023, ex);
        }
    }
}