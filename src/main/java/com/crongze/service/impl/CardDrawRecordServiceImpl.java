package com.crongze.service.impl;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.crongze.mapper.CardDrawRecordMapper;
import com.crongze.model.CardDrawRecord;
import com.crongze.service.ICardDrawRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * CardDrawRecord 表数据服务层接口实现类
 *
 */
@Service
@RequiredArgsConstructor
public class CardDrawRecordServiceImpl extends SuperServiceImpl<CardDrawRecordMapper, CardDrawRecord> implements ICardDrawRecordService {
    private final CardDrawRecordMapper cardDrawRecordMapper;

}