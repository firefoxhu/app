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
@Table(name = "xy_article")
@EntityListeners(AuditingEntityListener.class)
public class Article {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(length = 256)
    private String content;

    @Column(length = 512)
    private String pictures;

    /**
     * 0 微信名称
     * 1 匿名名称
     */
    @Column(length = 1)
    private String anonymous;

    @Column(length = 1)
    @Builder.Default
    private String  top = "0";

    @Column(length = 1)
    @Builder.Default
    private String status = "0";

    @Builder.Default
    private Integer views = 0;

    @Builder.Default
    private Integer fabulous = 0;

    @Builder.Default
    private Integer  commentsNumber = 0;

    @Column(length = 32)
    private String location;

    @Column(length = 32)
    private String ip;

    @Column
    @CreatedDate
    private LocalDateTime createTime;

    /** Object Mapping Object **/

    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false)
    @JoinColumn(name="user_id")
    private  User user;

}
