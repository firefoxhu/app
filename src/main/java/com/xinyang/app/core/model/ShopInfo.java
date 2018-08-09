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
@Table(name = "xy_shop_info")
@EntityListeners(AuditingEntityListener.class)
public class ShopInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long shopId;

    @Column(length = 16)
    private String categoryCode;

    @Column(length = 256)
    private String skillDescription;

    @Column(length = 11)
    private String phone;

    @Column(length = 256)
    private String bannerPicture;

    @Column(length = 512)
    private String galleryPicture;

    @Column(length = 64)
    private String wxCode;

    @Column(length = 512)
    private String description;

    @Builder.Default
    private Integer views = 0;

    @Column(length = 1)
    @Builder.Default
    private String topPlacement = "0";

    @Column(length = 1)
    @Builder.Default
    private String recommend = "0";

    @Column
    @CreatedDate
    private LocalDateTime createTime;
}
