package ir.stts.bajet.core.setting.entity;

import com.fasterxml.jackson.databind.JsonNode;
import ir.stts.bajet.core.jpa.converter.JsonNode2String;
import ir.stts.bajet.core.entity.Auditable;
import ir.stts.bajet.core.setting.constant.SettingDataType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Entity
@Table(name = "tbl_setting", indexes = {
        @Index(name = "idx_setting_key", columnList = "key", unique = true)
})
public class Setting extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_tbl_setting")
    @SequenceGenerator(name = "seq_tbl_setting", sequenceName = "seq_tbl_setting", allocationSize = 1)
    private Long id;

    @Column(name = "key", nullable = false)
    private String key;

    @Lob
    @Column(name = "value", nullable = false)
    @Convert(converter = JsonNode2String.class)
    private JsonNode value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SettingDataType type;

    @Column(name = "validation")
    private String validation;

    @Column(name = "allowed_values")
    private String allowedValues;
}