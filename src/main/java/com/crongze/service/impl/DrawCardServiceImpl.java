package com.crongze.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.crongze.model.Card;
import com.crongze.service.ICardDrawRecordService;
import com.crongze.service.ICardService;
import com.crongze.service.IDrawCardService;
import com.sobte.cqp.jcq.entity.CoolQ;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DrawCardServiceImpl implements IDrawCardService {

    private final ICardService cardService;
    private final ICardDrawRecordService cardDrawRecordService;

    /**
     *          制卡c 示例：c {name:"",description:"",linkUrl:""}
     *          名称必填，唯一，长度最大20汉字
     *          描述必填，长度最大512汉字
     *          相关链接必填，长度最大1024字符
     *          卡片数据不作隔离，相当于只有世界服
     * @param subType
     * @param msgId
     * @param fromQQ
     * @param msg
     * @param font
     */
    @Override
    public void createCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {
        if(!msg.startsWith("c ")){
            return;
        }
        StringBuilder msgBuilder = new StringBuilder(msg);
        msgBuilder.delete(0, 2);
        Card card = JSONObject.parseObject(msgBuilder.toString(), Card.class);
        card.setCreateTime(LocalDateTime.now());
        card.setFromQQ(fromQQ);
        cardService.insert(card);
        CQ.sendPrivateMsg(fromQQ, "您成功制作了一张新卡片");
    }

    @Override
    public void viewCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    @Override
    public void viewCardDetail(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    @Override
    public void drawCard(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {

    }
}
