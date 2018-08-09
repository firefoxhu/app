package com.xinyang.app.core.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xy_mchnt")
@EntityListeners(AuditingEntityListener.class)
public class Mchnt {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long userId;

    @Column(length = 16)
    private String name;

    @Column(length = 11)
    private String phone;

    @Column(length = 128)
    private String picture;

    @Column(length = 20)
    private String identify;

    @Column(length = 1)
    @Builder.Default
    private String status = "0";

    @Column
    @CreatedDate
    private LocalDateTime createTime;

}
