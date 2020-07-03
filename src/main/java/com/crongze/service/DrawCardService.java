package com.crongze.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crongze.model.Card;
import com.sobte.cqp.jcq.entity.CoolQ;

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
            if(StringUtil)
        }catch (Exception e){
            coolQ.sendPrivateMsg(fromQQ, "卡片格式错误，请仔细检查。可参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");

        }



        card.setCreateTime(LocalDateTime.now());
        card.setFromQQ(fromQQ);

        // 获取应用目录 appDirectory：Z:\home\user\coolq\data\app\com.sobte.cqp.jcq\app\com.crongze.draw-card\
        //String appDirectoryUrl = coolQ.getAppDirectory();
        //File appDirectory = new File(appDirectoryUrl);
        //if(!appDirectory.exists()){
        //    appDirectory.createNewFile();
        //}

        // 获取卡片数据文件
        String cardsDBFileUrl = coolQ.getAppDirectory() + "data\\cards.db";
        File cardsDBFile = new File(cardsDBFileUrl);
        if(!cardsDBFile.exists()){
            try {
                cardsDBFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO 卡片数据文件转JsonArray
        BufferedReader bufferedReader = new BufferedReader(new FileReader(cardsDBFile));
        StringBuilder stringBuilder = new StringBuilder();
        String content;
        while((content = bufferedReader.readLine() ) != null){
            stringBuilder.append(content);
        }
        List<Card> cards = JSONArray.parseArray(stringBuilder.toString(), Card.class);

        // TODO 校验卡片名重复

        // 新增卡片数据
        cards.add(card);

        // 写出卡片数据文件


        coolQ.sendPrivateMsg(fromQQ, "您成功制作了一张新卡片："+cardsDBFileUrl);
    }

    public void viewCard(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    public void viewCardDetail(CoolQ CQ, int subType, int msgId, long fromQQ, String msg, int font) {

    }

    public void drawCard(CoolQ CQ, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {

    }
}
