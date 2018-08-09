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
@Table(name = "xy_comment")
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private Long articleId;

    @Column(length = 128)
    private String content;

    @Builder.Default
    private Integer fabulous = 0;

    @Column(length = 32)
    private String location;

    @Column(length = 1)
    @Builder.Default
    private String status = "0";

    @Column(length = 32)
    private String ip;

    /**
     * 0正常
     * 1匿名
     */
    @Column(length = 1)
    private String anonymous;


    @Column
    @CreatedDate
    private LocalDateTime createTime;


    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false)
    @JoinColumn(name="user_id")
    private  User user;
}
