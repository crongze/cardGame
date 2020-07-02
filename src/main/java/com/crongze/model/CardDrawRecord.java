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
 * 抽卡记录表
 *
 */
@TableName("tb_card_draw_record")
@Data
public class CardDrawRecord implements Serializable {

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	/** 主键id */
	@TableId(type = IdType.AUTO)
	private Long id;

	/** 抽取人QQ号 */
	private Long drawQQ;

	/** 被抽取卡片id */
	private Long cardId;

	/** 被抽取卡片名称 */
	private String cardName;

	/** 创建时间（抽取时间） */
	private LocalDateTime createTime;

}
