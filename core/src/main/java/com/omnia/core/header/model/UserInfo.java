package com.omnia.core.header.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class UserInfo {
    private String firstName;
    private String firstNameEn;
    private String lastName;
    private String lastNameEn;
    private String userId;
    private String clientId;
    private String username;
    private String nationalCode;
    private String phoneNumber;
    private List<Map<String, Object>> authorizations = new ArrayList<>();
    private List<String> organs = new ArrayList<>();
}
