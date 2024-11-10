package com.example.entity;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@Table(name = "advertiser_info")
public class AdvertiserInfo   implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "name")
	private String name;//公告名
	@Column(name = "content")
	private String content;//公告内容
//	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "time")
	private LocalDateTime time;//公告发布时间


}
