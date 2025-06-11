package ir.stts.bajet.core.header.config;

import ir.stts.bajet.core.header.constant.ClientType;
import ir.stts.bajet.core.header.constant.GatewayType;
import ir.stts.bajet.core.header.constant.HeaderValidationStatus;
import ir.stts.bajet.core.header.model.HeaderSpec;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

public class HeaderValidation {

    public HeaderValidationStatus validate(HeaderSpec headerSpec, HeaderActionProperties headerActionProperties) {

        if (!headerActionProperties.isEnabled())
            return HeaderValidationStatus.IGNORED;

        String acceptLanguage = headerActionProperties.getAcceptLanguage();
        if (StringUtils.hasText(acceptLanguage) && !acceptLanguage.equalsIgnoreCase(headerSpec.getLang()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceClientIP() && !StringUtils.hasText(headerSpec.getClientInfo().getClientIp()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceClientVersion() && !StringUtils.hasText(headerSpec.getClientInfo().getClientVersion()))
            return HeaderValidationStatus.INVALID;

        ClientType[] allowedClientType = headerActionProperties.getAllowedClientType();
        if (allowedClientType != null
                && allowedClientType.length > 0
                && !ArrayUtils.contains(allowedClientType, headerSpec.getClientInfo().getClientType()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceDeviceId() && !StringUtils.hasText(headerSpec.getClientInfo().getDeviceId()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceAppVersion() && !StringUtils.hasText(headerSpec.getClientInfo().getAppVersion()))
            return HeaderValidationStatus.INVALID;

        GatewayType[] allowedGatewayType = headerActionProperties.getAllowedGatewayType();
        if (allowedGatewayType != null
                && allowedGatewayType.length > 0
                && !ArrayUtils.contains(allowedGatewayType, headerSpec.getClientInfo().getGatewayType()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceUserId() && !StringUtils.hasText(headerSpec.getUserInfo().getUserId()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceClientId() && !StringUtils.hasText(headerSpec.getUserInfo().getClientId()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceUsername() && !StringUtils.hasText(headerSpec.getUserInfo().getUsername()))
            return HeaderValidationStatus.INVALID;

        if (headerActionProperties.isForceNationalCode() && !StringUtils.hasText(headerSpec.getUserInfo().getNationalCode()))
            return HeaderValidationStatus.INVALID;

        return HeaderValidationStatus.VALID;
    }
}