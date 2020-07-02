package com.crongze.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * 抽卡记录表
 *
 */
@Data
public class CardDrawRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 主键id */
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
