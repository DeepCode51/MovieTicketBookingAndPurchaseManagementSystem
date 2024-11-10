package com.example.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 电影信息实体类
 */
@Data
@Table(name = "goods_info")
public class GoodsInfo  implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品名称 （电影名称）
     */
    private String name;

    /**
     * 商品描述（电影描述）
     */
    private String description;

    /**
     * 商品图片id（电影图片）
     */
    @Column(name = "fileIds")
    private String fileIds;

    /**
     * 商品价格（电影价格）
     */
    private Double price;

    /**
     * 商品销量（电影销量）
     */
    private Integer sales;

    /**
     * 商品点赞数
     */
    private Integer hot;

    /**
     * 参演电影的演员信息
     */
    private String actor;

    /**
     * 电影放映开始时间
     */
    @Column(name = "beginTime")
//    @JsonFormat(pattern = "yyyy:MM:dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 电影放映时长
     */
    private String time;

    /**
     * 所属类别
     */
    @Column(name = "typeId")
    private Long typeId;
    /**
     * 所属用户
     */
    @Column(name = "userId")
    private Long userId;

    @Column(name = "level")
    private Integer level;

    @Column(name = "isShow")
    private Integer isShow;//是否上架

    /**
     * 折扣
     */
    private Double discount;
    /**
     * 是否推荐
     */
    @Column(name = "recommend")
    private Integer recommend;

    @Transient
    private String typeName;

    @Transient
    private String userName;

    @Transient
    private List<Long> fileList;

    /**
     * 商品评价状态
     */
    @Transient
    private String commentStatus;

    @Transient
    private Integer num;


}
