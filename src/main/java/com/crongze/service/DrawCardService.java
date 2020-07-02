package com.crongze.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crongze.model.Card;
import com.sobte.cqp.jcq.entity.CoolQ;

import java.time.LocalDateTime;

public class DrawCardService {

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
    public void createCard(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font) {
        if(!msg.startsWith("c ")){
            return;
        }
        //StringBuilder msgBuilder = new StringBuilder(msg);
        //msgBuilder.delete(0, 2);
        //Card card = JSONObject.parseObject(msgBuilder.toString(), Card.class);
        //card.setCreateTime(LocalDateTime.now());
        //card.setFromQQ(fromQQ);
        // 读取卡片池数据
        String appDirectory = coolQ.getAppDirectory();
        // 获取应用数据目录(无需储存数据时，请将此行注释)
        // 返回如：D:\CoolQ\app\com.sobte.cqp.jcq\app\com.example.demo\
        // 应用的所有数据、配置【必须】存放于此目录，避免给用户带来困扰。
        //JSONArray.
        coolQ.sendPrivateMsg(fromQQ, "您成功制作了一张新卡片appDirectory："+appDirectory);
    }

    public void viewCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    public void viewCardDetail(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    public void drawCard(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {

    }
}
