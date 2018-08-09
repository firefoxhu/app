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
@Table(name = "xy_message")
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    /**
     * 消息来源
     */
    private Long fromUser;

    /**
     * 发送给谁
     */
    private Long toUser;

    /**
     * 消息内容
     */
    @Column(length = 256)
    private String content;

    /**
     * 1 未读
     * 0 已读
     */
    @Column(length = 1)
    @Builder.Default
    private String  isRead = "1";

    /**
     * 1 删除
     * 0 正常
     */
    @Column(length = 1)
    @Builder.Default
    private String status = "0";

    @Column
    @CreatedDate
    private LocalDateTime createTime;





}
