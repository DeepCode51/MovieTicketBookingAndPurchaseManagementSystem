package com.example.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户实体类
 */
@Data
@Table(name = "user_info")
public class UserInfo extends Account  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "name")
	private String name;//登录用（用户名）
	@Column(name = "password")
	private String password;//密码
	@Column(name = "nickName")
	private String nickName;//昵称
	@Column(name = "sex")
	private Integer sex;//性别
	@Column(name = "age")
	private Integer age;//年龄
//	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
	@Column(name = "birthday")
	private LocalDate birthday;//出生日期
	@Column(name = "phone")
	private String phone;//联系方式
	@Column(name = "address")
	private String address;//地址
	@Column(name = "email")
	private String email;
	@Column(name = "cardId")
	private String cardId;//关联订单id
	@Column(name = "level")
	private Integer level;//用户等级
	@Column(name = "account")
	private Double account;//余额

	@Column(name="confirmCode")
	private String confirmCode;//确认码

	@Column(name="activationTime")
	private LocalDateTime activationTime;//失效时间

	@Column(name="isValid")
	private Integer isValid;//是否激活

	@Column(name="isBan")
	private Integer isBan;//是否禁用

}
