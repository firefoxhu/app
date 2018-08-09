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
@Table(name = "xy_shop")
@EntityListeners(AuditingEntityListener.class)
public class Shop {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long mchntId;

    @Column(length = 32)
    private String name;

    @Column(length = 64)
    private String businessLicense;

    @Column(length = 60)
    private String latitude;

    @Column(length = 60)
    private String longitude;

    @Column(length = 1)
    @Builder.Default
    private String status = "0";

    @Column(length = 16)
    private String province;

    @Column(length = 16)
    private String city;

    @Column(length = 16)
    private String area;

    @Column(length = 256)
    private String street;

    @Column
    @CreatedDate
    private LocalDateTime createTime;
}
