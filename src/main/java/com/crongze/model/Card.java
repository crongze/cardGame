package com.crongze.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * 卡片表
 *
 */
@Data
public class Card implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 卡片名称 */
	private String name;

	/** 卡片描述 */
	private String description;

	/** 相关链接 */
	private String linkUrl;

	/** 制卡人QQ号 */
	private Long fromQQ;

    /** 制卡人QQ昵称 */
    private String fromQQNick;

	/** 创建时间 */
	private LocalDateTime createTime;

}
