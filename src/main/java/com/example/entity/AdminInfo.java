package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Table(name = "admin_info")
public class AdminInfo extends Account  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "name")
	private String name; //用户名
	@Column(name = "password")
	private String password;//密码
	@Column(name = "nickName")
	private String nickName;//昵称
	@Column(name = "sex")
	private Integer sex;//性别
	@Column(name = "age")
	private Integer age;//年龄
	@Column(name = "birthday")
//	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDate birthday;//出生日期
	@Column(name = "phone")
	private String phone;//手机号
	@Column(name = "address")
	private String address;//地址
	@Column(name = "code")
	private String code;//编号
	@Column(name = "email")
	private String email;//邮箱
	@Column(name = "cardId")
	private String cardId;//身份证
	@Column(name = "level")
	private Integer level;//权限等级
	@Column(name = "account")
	private Double account;//账户余额

	@Column(name="confirmCode")
	private String confirmCode;

	@Column(name="activationTime")
	private LocalDateTime activationTime;

	@Column(name="isValid")
	private Integer isValid;



}
