package com.xinyang.app.core.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Table(name = "xy_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(length = 32)
    private String openId;

    @Column(length = 64)
    private String username;

    @Column(length = 128)
    private String password;

    @Column(length = 2)
    private String gender;

    @Column(length = 11)
    private String mobile;

    @Column(length = 64)
    private String nickname;

    @Column(length = 256)
    private String avatar;

    @Column(length = 64)
    private String anonymousName;

    @Column(length = 128)
    private String anonymousAvatar;

    /**
     * 0 正常
     * 1 仅仅是使用过openid 属于匿名用户
     */
    @Column(length = 1)
    private String status;

    @Column(length = 19)
    @CreatedDate
    private LocalDateTime createTime;



}
