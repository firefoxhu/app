package com.xinyang.app.core.model;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "xy_shop_info_type_r")
@EntityListeners(AuditingEntityListener.class)
public class ShopInfoType {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long shopInfoId;

    private Long typeId;

}
