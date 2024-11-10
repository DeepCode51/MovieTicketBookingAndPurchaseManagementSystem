package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户实体类
 */
@Data
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//主键 id
    @Column(name = "name")
    private String name; //姓名(登录用户名)

    @Column(name = "email")
    @Transient
    private String email;//邮箱
    @Column(name = "password")
    private String password;//密码
    @Column(name = "level")
    private Integer level;//权限等级
    @Column(name = "sex")
    private Integer sex;//性别
    @Transient
    private String newPassword;//新密码
    @Transient
    private String address;//地址
    @Column(name = "nickName")
    private String nickName;//昵称
    @Transient
    private String phone;//手机号
    @Transient
    private Double account;//账户余额


}
