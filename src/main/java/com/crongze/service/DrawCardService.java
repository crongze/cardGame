package com.crongze.service;

import com.alibaba.fastjson.JSONObject;
import com.crongze.model.Card;
import com.crongze.model.CardDrawRecord;
import com.sobte.cqp.jcq.entity.CoolQ;
import com.sobte.cqp.jcq.message.CQCode;
import org.springframework.util.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Random;

public class DrawCardService {

    public void createCard(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font, String lowerMsg) throws Exception{
        if(lowerMsg.equals("c") || lowerMsg.equals("制卡")){
            coolQ.sendPrivateMsg(fromQQ, "您没有指定卡片创建数据。\n请参考：【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");
            return;
        }
        if(!lowerMsg.startsWith("c ") && !lowerMsg.startsWith("制卡 ")){
            return;
        }

        StringBuilder msgBuilder = new StringBuilder(msg);
        msgBuilder.delete(0, 2);

        // 校验参数
        Card newCard;
        try {
            newCard = JSONObject.parseObject(msgBuilder.toString(), Card.class);
            if(!StringUtils.hasText(newCard.getName())){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不得为空。\n请参考：【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");
                return;
            }
            if(newCard.getName().length() > 20){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name超出最大长度20字符");
                return;
            }
            try {
                Integer.valueOf(newCard.getName().trim());
                Double.valueOf(newCard.getName().trim());
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不允许是纯数字");
                return;
            }catch (Exception e){

            }
            if(newCard.getName().contains(".record")){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不允许包含保留字符串.record");
                return;
            }
            if(newCard.getName().contains(".card")){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不允许包含保留字符串.card");
                return;
            }
            if(newCard.getName().contains("c ")){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不允许包含保留字符串c ");
                return;
            }
            if(newCard.getName().contains("vd ")){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不允许包含保留字符串vd ");
                return;
            }
            if(!StringUtils.hasText(newCard.getDescription())){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，description不得为空。\n请参考：【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");
                return;
            }
            if(newCard.getDescription().length() > 512){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，description超出最大长度512字符");
                return;
            }
            if(!StringUtils.hasText(newCard.getLinkUrl())){
                newCard.setLinkUrl("暂无");
            }
            if(newCard.getLinkUrl().length() > 1024){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，linkUrl超出最大长度1024字符");
                return;
            }
        }catch (Exception e){
            coolQ.sendPrivateMsg(fromQQ, "卡片数据格式错误，非json格式，请仔细检查。\n请参考：【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");
            return;
        }

        newCard.setCreateTime(LocalDateTime.now());
        newCard.setFromQQ(fromQQ);

        // 校验卡名是否重复
        String cardFileUrl = coolQ.getAppDirectory() + "data\\cards\\" + newCard.getName().trim() + ".card";
        File cardFile = new File(cardFileUrl);
        if(cardFile.exists()){
            coolQ.sendPrivateMsg(fromQQ, "制作失败，已存在相同名称的卡片。请换个name试试");
            return;
        }

        // 去除多余的空格
        newCard.setName(newCard.getName().trim());
        newCard.setDescription(newCard.getDescription().trim());
        newCard.setLinkUrl(newCard.getLinkUrl().trim());

        // 校验并生成父级目录
        if(!cardFile.getParentFile().exists()){
            cardFile.getParentFile().mkdirs();
        }

        // 新增卡片数据文件
        cardFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(cardFile));
        writer.write(JSONObject.toJSONString(newCard));
        writer.flush();
        writer.close();
        coolQ.sendPrivateMsg(fromQQ, "恭喜！您成功制作了一张新卡片：【" + newCard.getName() + "】");
    }

    public void viewCard(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font, String lowerMsg) throws Exception{
        if(!lowerMsg.equals("v") && !lowerMsg.equals("卡列表")){
            return;
        }
        String recordDirectoryUrl = coolQ.getAppDirectory() + "data\\draw_record\\" + fromQQ;
        File recordDirectoryFile = new File(recordDirectoryUrl);
        if(!recordDirectoryFile.exists() || recordDirectoryFile.list().length == 0){
            coolQ.sendPrivateMsg(fromQQ, "您一张卡片都没有...");
            return;
        }
        File[] recordFiles = recordDirectoryFile.listFiles();
        // 返回卡片列表信息
        StringBuilder cardList = new StringBuilder();
        cardList.append("以下是您已抽取到的卡片：\n\n");
        for (int i = 0; i < recordFiles.length; i++) {
            BufferedReader recordFileReader = new BufferedReader(new FileReader(recordFiles[i]));
            StringBuilder recordFileBuilder = new StringBuilder();
            String recordContent;
            while ((recordContent = recordFileReader.readLine()) != null){
                recordFileBuilder.append(recordContent);
            }
            CardDrawRecord cardDrawRecord = JSONObject.parseObject(recordFileBuilder.toString(), CardDrawRecord.class);
            cardList.append((i + 1) + ". 【" + cardDrawRecord.getCardName() + "】 * " + cardDrawRecord.getDrawNum() + "\n");
        }
        cardList.append("\ntips：\n");
        cardList.append("您可以通过指令 vd 列表序号/卡片名 来查看某张卡片的详细信息哦！比如：vd 1 or vd 卡片名");
        coolQ.sendPrivateMsg(fromQQ, cardList.toString());
    }

    public void viewCardDetail(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font, String lowerMsg) throws Exception{
        if(lowerMsg.equals("vd") || lowerMsg.equals("卡详情")){
            coolQ.sendPrivateMsg(fromQQ, "您没有指定查看的卡片\ntips：\n您可以通过指令 vd 列表序号/卡片名 来查看某张卡片的详细信息哦！比如：vd 1 or vd 卡片名");
            return;
        }
        if(!lowerMsg.startsWith("vd ") && !lowerMsg.startsWith("卡详情 ")){
            return;
        }

        String recordDirectoryUrl = coolQ.getAppDirectory() + "data\\draw_record\\" + fromQQ;
        File recordDirectoryFile = new File(recordDirectoryUrl);
        if(!recordDirectoryFile.exists() || recordDirectoryFile.list().length == 0){
            coolQ.sendPrivateMsg(fromQQ, "您一张卡片都没有...");
            return;
        }
        if(lowerMsg.startsWith("vd ")){
            msg = msg.substring("vd ".length());
        }
        if(lowerMsg.startsWith("卡详情 ")){
            msg = msg.substring("卡详情 ".length());
        }
        String cardName;
        try {
            Integer listNo = Integer.valueOf(msg);
            // 列表序号方式查询
            File[] recordFiles = recordDirectoryFile.listFiles();
            if(listNo < 1 || listNo > recordFiles.length){
                coolQ.sendPrivateMsg(fromQQ, "您查询的列表序号超出范围。您的列表序号范围：1-" + recordFiles.length);
            }
            cardName = recordFiles[listNo - 1].getName().replace(".record", "");
        }catch (Exception e){
            // 卡片名方式查询
            String recordFileUrl = coolQ.getAppDirectory() + "data\\draw_record\\" + fromQQ + "\\" + msg + ".record";
            File recordFile = new File(recordFileUrl);
            if(!recordFile.exists()){
                coolQ.sendPrivateMsg(fromQQ, "抱歉！您还没有获得该卡片：【" + msg + "】\n您可以试试通过抽取得到哦~");
                return;
            }
            cardName = msg;
        }

        File cardFile = new File(coolQ.getAppDirectory() + "data\\cards\\" + cardName + ".card");
        BufferedReader reader = new BufferedReader(new FileReader(cardFile));
        StringBuilder builder = new StringBuilder();
        String content;
        while ((content = reader.readLine()) != null){
            builder.append(content);
        }
        Card card = JSONObject.parseObject(builder.toString(), Card.class);
        StringBuilder cardMessage = new StringBuilder();
        cardMessage.append("【卡片名称】：" + card.getName() + "\n");
        cardMessage.append("【卡片描述】：" + card.getDescription() + "\n");
        cardMessage.append("【相关链接】：" + card.getLinkUrl() + "\n");
        cardMessage.append("【制卡人】：" + card.getFromQQ());
        coolQ.sendPrivateMsg(fromQQ, cardMessage.toString());
    }

    public void drawCard(CoolQ coolQ, CQCode cQCode, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font, String lowerMsg) throws Exception{
        if(!lowerMsg.equals("m") && !lowerMsg.equals("抽卡")){
            return;
        }

        //String BASE_URL = "E:\\";

        // 获取卡片数据库
        String cardDBUrl = coolQ.getAppDirectory() + "data\\cards";
        //String cardDBUrl = BASE_URL + "data\\cards";
        File cardDB = new File(cardDBUrl);

        // 校验是否有卡片
        if(!cardDB.exists() || cardDB.list().length == 0){
            coolQ.sendGroupMsg(fromGroup, cQCode.at(fromQQ) + " 暂无卡片。\ntips：\n您可以试试私聊我，自己创建新卡片哦！创建命令请参考：【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");
            return;
        }

        // 暂时设定为一半概率抽不到卡片 默认true为没抽到
        Random random = new Random();
        //if(random.nextBoolean()){
        //    coolQ.sendGroupMsg(fromGroup, cQCode.at(fromQQ) + " 真可惜，您什么卡片都没有抽到呢！要不...再试试吧？");
        //    return;
        //}

        // 随机获取一张卡片
        File[] cards = cardDB.listFiles();
        File drewCard = cards[random.nextInt(cards.length)];

        // 新增抽取记录
            // 读取被抽卡片的详细数据
        BufferedReader reader = new BufferedReader(new FileReader(drewCard));
        StringBuilder builder = new StringBuilder();
        String content;
        while ((content = reader.readLine()) != null){
            builder.append(content);
        }
        Card card = JSONObject.parseObject(builder.toString(), Card.class);
            // 输出抽取记录
        String recordFileUrl = coolQ.getAppDirectory() + "data\\draw_record\\" + fromQQ + "\\" + card.getName() + ".record";
        //String recordFileUrl = BASE_URL + "data\\draw_record\\" + fromQQ + "\\" + card.getName() + ".record";
        File recordFile = new File(recordFileUrl);
        if(!recordFile.getParentFile().exists()){
            recordFile.getParentFile().mkdirs();
        }
        CardDrawRecord cardDrawRecord;
        if(recordFile.exists()){
            BufferedReader recordFileReader = new BufferedReader(new FileReader(recordFile));
            StringBuilder recordFileBuilder = new StringBuilder();
            String recordContent;
            while ((recordContent = recordFileReader.readLine()) != null){
                recordFileBuilder.append(recordContent);
            }
            cardDrawRecord = JSONObject.parseObject(recordFileBuilder.toString(), CardDrawRecord.class);
            cardDrawRecord.setDrawNum(cardDrawRecord.getDrawNum().add(BigInteger.ONE));
        }else {
            recordFile.createNewFile();
            cardDrawRecord = new CardDrawRecord();
            cardDrawRecord.setCardName(card.getName());
            cardDrawRecord.setDrawNum(BigInteger.ONE);
        }
        // new BufferedWriter 时就创建了文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(recordFile));
        writer.write(JSONObject.toJSONString(cardDrawRecord));
        writer.flush();
        writer.close();
        reader.close();

        // 发送成功tip信息
        StringBuilder successTip = new StringBuilder();
        successTip.append(cQCode.at(fromQQ) + " 恭喜您抽取到了：\n");
        successTip.append("【卡片名称】：" + card.getName() + "\n");
        successTip.append("【卡片描述】：" + card.getDescription() + "\n");
        successTip.append("【相关链接】：" + card.getLinkUrl() + "\n");
        successTip.append("【制卡人】：" + cQCode.at(card.getFromQQ()));
        coolQ.sendGroupMsg(fromGroup, successTip.toString());
    }

    public void viewHelp(CoolQ coolQ, CQCode cQCode, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font, String lowerMsg){
        if(!lowerMsg.equals("h") && !lowerMsg.equals("查看帮助") && !lowerMsg.equals("help")){
            return;
        }
        // 发送成功tip信息
        StringBuilder successTip = new StringBuilder();
        successTip.append(cQCode.at(fromQQ) + " 您好，小新是一个基础的抽卡系统，目前小新支持以下功能： \n");
        successTip.append("\n");
        successTip.append("1.抽卡：随机抽取卡片。指令为【抽卡】或【m】\n");
        successTip.append("2.制卡：制作新卡片。指令为【制卡】或【c】\n");
        successTip.append("3.查看卡列表：指令为【卡列表】或【v】\n");
        successTip.append("4.查看卡片详情：指令为【卡详情】或【vd】\n");
        successTip.append("5.查看卡片库：指令为【卡片库】或【cdb】\n");
        successTip.append("6.查看系统帮助：指令为【查看帮助】或【h】或【help】\n");
        successTip.append("\n");
        successTip.append("tips：\n");
        successTip.append("1.【抽卡】、【查看卡片库】、【查看系统帮助】只支持群聊，【制卡】、【查看卡列表】、【查看卡片详情】只支持私聊\n");
        successTip.append("2.【制卡】条件：参考demo指令【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");

        coolQ.sendGroupMsg(fromGroup, successTip.toString());
    }

    public void viewCardDataBase(CoolQ coolQ, CQCode cQCode, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font, String lowerMsg) throws Exception{
        if(!lowerMsg.equals("cdb") && !lowerMsg.equals("卡片库")){
            return;
        }
        // 获取卡片数据库
        String cardDBUrl = coolQ.getAppDirectory() + "data\\cards";
        File cardDB = new File(cardDBUrl);

        // 校验是否有卡片
        if(!cardDB.exists() || cardDB.list().length == 0){
            coolQ.sendGroupMsg(fromGroup, cQCode.at(fromQQ) + " 暂无卡片。\ntips：\n您可以试试私聊我，自己创建新卡片哦！创建命令请参考：【c {name:\"卡片名必填，唯一，长度最大20字符\",description:\"卡片描述必填，长度最大512字符\",linkUrl:\"相关链接选填，长度最大1024字符\"}】");
            return;
        }
        StringBuilder cardList = new StringBuilder();
        String[] cardNames = cardDB.list();
        cardList.append("目前系统中有 "+ cardNames.length +" 张卡片：\n\n");
        for (int i = 0; i < cardNames.length; i++) {
            cardList.append((i+1)+".【" + cardNames[i].replace(".card", "") + "】\n");
        }
        cardList.append("\ntips：\n您可以通过指令【抽卡】或【m】来抽取它们");
        coolQ.sendGroupMsg(fromGroup, cardList.toString());
    }
}
