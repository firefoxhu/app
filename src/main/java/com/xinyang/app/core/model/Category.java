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
@Table(name = "xy_category")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    /**
     * 类别的类别码
     */
    @Column(length = 16)
    private String code;

    /**
     * 类别名称
     */
    @Column(length = 16)
    private String name;

    @Column(length = 128)
    private String picture;

    /**
     * 排序编号
     */
    @Builder.Default
    private Integer sort = 1;

    @Column
    @CreatedDate
    private LocalDateTime createTime;

}
