package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Table(name = "order_info")
public class OrderInfo  implements Serializable {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单id
     */
    @Column(name = "orderId")
    private String orderId;

    /**
     * 订单总价格
     */
    @Column(name = "totalPrice")
    private Double totalPrice;

    /**
     * 所属用户
     */
    @Column(name = "userId")
    private Long userId;

    @Column(name = "level")
    private Integer level;

    /**
     * 联系地址
     */
    @Column(name = "linkAddress")
    private String linkAddress;

    /**
     * 联系电话
     */
    @Column(name = "linkPhone")
    private String linkPhone;

    /**
     * 联系人
     */
    @Column(name = "linkMan")
    private String linkMan;

    /**
     * 创建时间
     */
    @Column(name = "createTime")
    private LocalDateTime createTime;

    /**
     * 订单状态
     * 取消0 完成3 待观看2 代付款1
     */
    private Integer status;

    /**
     * 关联的用户信息
     */
    @Transient
    private Account userInfo;


    @Transient
    private List<GoodsInfo> goodsList;

    //电影id
    @Transient
    private Long goodsId;
    @Transient
    private Integer total;
    @Transient
    private String position;


}
