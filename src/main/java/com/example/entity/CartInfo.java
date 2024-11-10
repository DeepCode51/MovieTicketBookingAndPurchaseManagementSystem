package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@Table(name = "cart_info")
public class CartInfo  implements Serializable {
    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 所属用户
     */
    @Column(name = "userId")
    private Long userId;

    /**
     * 所属商品
     */
    @Column(name = "goodsId")
    private Long goodsId;

    /**
     * 创建时间
     */
    @Column(name = "createTime")
    private LocalDateTime createTime;

    @Column(name = "level")
    private Integer level;//权限等级

    @Transient
    private String userName; //用户名
    @Transient
    private String goodsName;//商品名


}
