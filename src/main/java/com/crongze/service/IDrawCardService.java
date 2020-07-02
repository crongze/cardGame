package com.crongze.service;

import com.sobte.cqp.jcq.entity.CoolQ;

public interface IDrawCardService {

    void createCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font);

    void viewCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font);

    void viewCardDetail(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font);

    void drawCard(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font);
}
