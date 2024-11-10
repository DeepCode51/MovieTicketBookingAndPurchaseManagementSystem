package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@Table(name = "comment_info")
public class CommentInfo  implements Serializable {
    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 所属电影
     */
    @Column(name = "goodsId")
    private Long goodsId;

    /**
     * 创建时间
     */
    @Column(name = "createTime")
    private LocalDateTime createTime;
    //评论者（当前用户）id
    @Column(name = "userId")
    private Long userId;

    //权限等级
    @Column(name = "level")
    private Integer level;

    //电影名
    @Transient
    private String goodsName;

    //用户昵称
    @Transient
    private String userName;


}
