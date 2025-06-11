package ir.stts.bajet.core.date.config;

import ir.stts.bajet.core.constant.BajetConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(prefix = BajetConstants.BAJET_BASE_PACKAGE + ".core.jackson")
public class PersianDateSerializerProperties {
    private String persianDatePattern;
    private String persianDateTimePattern;
}