package com.omnia.core.message.entity;

import com.omnia.core.entity.Auditable;
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
@Table(name = "tbl_message", indexes = {
        @Index(name = "idx_message_key", columnList = "key", unique = true)
})
public class Message extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_tbl_message")
    @SequenceGenerator(name = "seq_tbl_message", sequenceName = "seq_tbl_message", allocationSize = 1)
    private Long id;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value", nullable = false, length = 1000)
    private String value;
}