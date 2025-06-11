package com.omnia.core.jpa;

import com.omnia.core.security.LegacyUserData;
import com.omnia.core.security.UserDataHolder;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class JpaAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        LegacyUserData legacyUserData = UserDataHolder.get();
        if (legacyUserData != null && legacyUserData.getUserInfo() != null && StringUtils.hasText(legacyUserData.getUserInfo().getUserId()))
            return Optional.of(legacyUserData.getUserInfo().getUserId());

        return Optional.of("anonymous user");
    }
}