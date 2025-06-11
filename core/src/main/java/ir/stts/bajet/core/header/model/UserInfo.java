package ir.stts.bajet.core.header.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class UserInfo {

    private String userId;
    private String clientId;
    private String username;
    private String nationalCode;
    private String phoneNumber;
    private List<String> organs = new ArrayList<>();
}
