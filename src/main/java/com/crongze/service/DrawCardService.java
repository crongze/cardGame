package com.crongze.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crongze.model.Card;
import com.sobte.cqp.jcq.entity.CoolQ;
import org.springframework.util.StringUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

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
    public void createCard(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font) throws Exception{
        if(!msg.startsWith("c ")){
            return;
        }

        StringBuilder msgBuilder = new StringBuilder(msg);
        msgBuilder.delete(0, 2);

        // 校验参数
        Card newCard = null;
        try {
            newCard = JSONObject.parseObject(msgBuilder.toString(), Card.class);
            if(!StringUtils.hasText(newCard.getName())){
                coolQ.sendPrivateMsg(fromQQ, "name不得为空");
                return;
            }
            if(!StringUtils.hasText(newCard.getDescription())){
                coolQ.sendPrivateMsg(fromQQ, "description不得为空");
                return;
            }
            if(!StringUtils.hasText(newCard.getLinkUrl())){
                coolQ.sendPrivateMsg(fromQQ, "linkUrl不得为空");
                return;
            }
        }catch (Exception e){
            coolQ.sendPrivateMsg(fromQQ, "卡片格式错误，请仔细检查。可参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
            return;
        }

        newCard.setCreateTime(LocalDateTime.now());
        newCard.setFromQQ(fromQQ);

        // 校验卡名是否重复
        // appDirectory：Z:\home\user\coolq\data\app\com.sobte.cqp.jcq\app\com.crongze.draw-card\
        String cardFileUrl = coolQ.getAppDirectory() + "data\\cards\\" + newCard.getName().trim() + ".db";
        File cardFile = new File(cardFileUrl);
        if(cardFile.exists()){
            coolQ.sendPrivateMsg(fromQQ, "制作失败，已有相同名称的卡片");
            return;
        }

        cardFile.createNewFile();

        // 新增卡片数据文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(cardFile));
        StringBuilder stringBuilder = new StringBuilder();
        String content;
        while((content = bufferedReader.readLine() ) != null){
            stringBuilder.append(content);
        }

        writer.write();

        coolQ.sendPrivateMsg(fromQQ, "您成功制作了一张新卡片：" + newCard.getName().trim());
    }

    public void viewCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    public void viewCardDetail(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    public void drawCard(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {

    }
}
