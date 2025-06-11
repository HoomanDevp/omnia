package ir.stts.bajet.core.resilience.constant;

import ir.stts.bajet.core.resilience.model.ErrorSpec;
import org.springframework.http.HttpStatus;

public interface IErrorCode {

    // GLOBAL EXCEPTION HANDLING
    ErrorSpec _INFR_10001 = new ErrorSpec("INFR-10001", HttpStatus.UNAUTHORIZED, true, false, false, "UnauthorizedException");
    ErrorSpec _INFR_10002 = new ErrorSpec("INFR-10002", HttpStatus.NOT_FOUND, true, false, false, "NotFoundException");
    ErrorSpec _INFR_10003 = new ErrorSpec("INFR-10003", HttpStatus.INTERNAL_SERVER_ERROR, true, false, false, "MappingException");
    ErrorSpec _INFR_10004 = new ErrorSpec("INFR-10004", HttpStatus.INTERNAL_SERVER_ERROR, true, false, false, "HeaderInfoException");
    ErrorSpec _INFR_10005 = new ErrorSpec("INFR-10005", HttpStatus.UNAUTHORIZED, false, false, false, "GWUnauthorizedException");
    ErrorSpec _INFR_10006 = new ErrorSpec("INFR-10006", HttpStatus.GATEWAY_TIMEOUT, false, true, false, "GWTimeoutException");
    ErrorSpec _INFR_10007 = new ErrorSpec("INFR-10007", HttpStatus.NOT_FOUND, false, false, false, "GWNotFoundException");
    ErrorSpec _INFR_10008 = new ErrorSpec("INFR-10008", HttpStatus.SERVICE_UNAVAILABLE, false, true, false, "GWNotAvailableException");
    ErrorSpec _INFR_10009 = new ErrorSpec("INFR-10009", HttpStatus.FORBIDDEN, true, false, false, "FileIllegalTypeException");
    ErrorSpec _INFR_10010 = new ErrorSpec("INFR-10010", HttpStatus.FORBIDDEN, true, false, false, "FileIllegalSizeException");
    ErrorSpec _INFR_10011 = new ErrorSpec("INFR-10011", HttpStatus.BAD_REQUEST, true, false, false, "MethodArgumentNotValidException");
    ErrorSpec _INFR_10012 = new ErrorSpec("INFR-10012", HttpStatus.BAD_REQUEST, true, false, false, "MethodValidationException");
    ErrorSpec _INFR_10013 = new ErrorSpec("INFR-10013", HttpStatus.REQUEST_TIMEOUT, true, true, false, "AsyncRequestTimeoutException");
    ErrorSpec _INFR_10014 = new ErrorSpec("INFR-10014", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "ErrorResponseException");
    ErrorSpec _INFR_10015 = new ErrorSpec("INFR-10015", HttpStatus.BAD_REQUEST, true, false, false, "MaxUploadSizeExceededException");
    ErrorSpec _INFR_10016 = new ErrorSpec("INFR-10016", HttpStatus.BAD_REQUEST, true, false, false, "ConversionNotSupported");
    ErrorSpec _INFR_10017 = new ErrorSpec("INFR-10017", HttpStatus.BAD_REQUEST, true, false, false, "TypeMismatch");
    ErrorSpec _INFR_10018 = new ErrorSpec("INFR-10018", HttpStatus.INTERNAL_SERVER_ERROR, true, false, false, "ConstraintViolation");
    ErrorSpec _INFR_10019 = new ErrorSpec("INFR-10019", HttpStatus.INTERNAL_SERVER_ERROR, true, false, false, "DataIntegrityViolation");
    ErrorSpec _INFR_10020 = new ErrorSpec("INFR-10020", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "Multipart");
    ErrorSpec _INFR_10021 = new ErrorSpec("INFR-10021", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "ObjectOptimisticLockingFailure");

    // REDIS - OTP
    ErrorSpec _INFR_10022 = new ErrorSpec("INFR-10022", HttpStatus.BAD_REQUEST, true, false, false, "OtpMethodsArgumentNotValidException");

    // STORAGE
    ErrorSpec _INFR_10023 = new ErrorSpec("INFR-10023", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-Upload");
    ErrorSpec _INFR_10024 = new ErrorSpec("INFR-10024", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-Download");
    ErrorSpec _INFR_10025 = new ErrorSpec("INFR-10025", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-Delete");
    ErrorSpec _INFR_10026 = new ErrorSpec("INFR-10026", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-GetPreSignedUrl2Upload");
    ErrorSpec _INFR_10027 = new ErrorSpec("INFR-10027", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-GetPreSignedUrl2Download");
    ErrorSpec _INFR_10028 = new ErrorSpec("INFR-10028", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-GetMetadata");
    ErrorSpec _INFR_10029 = new ErrorSpec("INFR-10029", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-Buckets");
    ErrorSpec _INFR_10030 = new ErrorSpec("INFR-10030", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-CreateBucket");
    ErrorSpec _INFR_10031 = new ErrorSpec("INFR-10031", HttpStatus.INTERNAL_SERVER_ERROR, true, true, false, "MinioService-Restore");

}