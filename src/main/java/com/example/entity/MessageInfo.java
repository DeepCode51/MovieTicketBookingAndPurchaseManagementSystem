package com.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 影评实体类
 */
@Data
@Table(name = "message_info")
public class MessageInfo  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "name")
	private String name;//发表的影评用户名
	@Column(name = "content")
	private String content;//影评内容
	@Column(name = "time")
	private LocalDateTime time;//发表评价时间
	@Column(name = "parentId")
	private Long parentId;//父级id

}
