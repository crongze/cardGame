package com.crongze.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * 抽卡记录表
 *
 */
@Data
public class CardDrawRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 被抽取卡片的名称 */
	private String cardName;

	/** 抽取数量 */
	private BigInteger drawNum;

}
