package ir.stts.bajet.core.resilience.entity;

import ir.stts.bajet.core.entity.Auditable;
import ir.stts.bajet.core.resilience.constant.ErrorSeverity;
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
@Table(name = "tbl_error", indexes = {
        @Index(name = "idx_error_error_code", columnList = "error_code", unique = true)
})
public class Error extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_tbl_error")
    @SequenceGenerator(name = "seq_tbl_error", sequenceName = "seq_tbl_error", allocationSize = 1)
    private Long id;

    @Column(name = "error_code", nullable = false)
    private String errorCode;

    @Column(name = "error_message", length = 500, nullable = false)
    private String errorMessage;

    @Column(name = "tech_error_message", length = 1000)
    private String techErrorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private ErrorSeverity severity;

    @Builder.Default
    @Column(name = "threshold", nullable = false, columnDefinition = "number default 0")
    private int threshold = 0;

    @Builder.Default
    @Column(name = "retryable", nullable = false, columnDefinition = "number(1) default 0")
    private boolean retryable = false;

    @Builder.Default
    @Column(name = "time_box_in_minutes", nullable = false, columnDefinition = "number default 0")
    private int timeBoxInMinutes = 0;
}