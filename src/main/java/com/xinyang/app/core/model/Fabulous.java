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
@Table(name = "xy_fabulous")
@EntityListeners(AuditingEntityListener.class)
public class Fabulous {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long articleId;

    private String username;


}
