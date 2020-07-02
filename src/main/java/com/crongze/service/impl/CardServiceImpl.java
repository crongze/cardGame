package com.crongze.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.crongze.mapper.CardMapper;
import com.crongze.model.Card;
import com.crongze.service.ICardService;
import com.baomidou.framework.service.impl.SuperServiceImpl;

/**
 *
 * Card 表数据服务层接口实现类
 *
 */
@Service
@RequiredArgsConstructor
public class CardServiceImpl extends SuperServiceImpl<CardMapper, Card> implements ICardService {
    private final CardMapper cardMapper;

}