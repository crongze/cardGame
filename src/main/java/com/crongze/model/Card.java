package com.crongze.model;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * 卡片表
 *
 */
@TableName("tb_card")
@Data
public class Card implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	/** 主键id */
	@TableId(type = IdType.AUTO)
	private Long id;

	/** 卡片名称 */
	private String name;

	/** 卡片描述 */
	private String description;

	/** 相关链接 */
	private String linkUrl;

	/** 制卡人QQ号 */
	private Long fromQQ;

	/** 创建时间 */
	private LocalDateTime createTime;

}
