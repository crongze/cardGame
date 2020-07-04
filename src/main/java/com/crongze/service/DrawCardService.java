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


    /**
     *          制卡c 示例：c {name:"",description:"",linkUrl:""}
     *          名称必填，唯一，长度最大20字符
     *          描述必填，长度最大512字符
     *          相关链接必填，长度最大1024字符
     *          卡片数据不作隔离，相当于只有世界服
     * @param subType
     * @param msgId
     * @param fromQQ
     * @param msg
     * @param font
     */
    public void createCard(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font) throws Exception{
        if(msg.equals("c")){
            coolQ.sendPrivateMsg(fromQQ, "您没有指定卡片创建数据。请参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
            return;
        }
        if(!msg.startsWith("c ")){
            return;
        }

        StringBuilder msgBuilder = new StringBuilder(msg);
        msgBuilder.delete(0, 2);

        // 校验参数
        Card newCard;
        try {
            newCard = JSONObject.parseObject(msgBuilder.toString(), Card.class);
            if(!StringUtils.hasText(newCard.getName())){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，name不得为空。请参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
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
                coolQ.sendPrivateMsg(fromQQ, "制作失败，description不得为空。请参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
                return;
            }
            if(newCard.getDescription().length() > 512){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，description超出最大长度512字符");
                return;
            }
            if(!StringUtils.hasText(newCard.getLinkUrl())){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，linkUrl不得为空。请参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
                return;
            }
            if(newCard.getLinkUrl().length() > 1024){
                coolQ.sendPrivateMsg(fromQQ, "制作失败，linkUrl超出最大长度1024字符");
                return;
            }
        }catch (Exception e){
            coolQ.sendPrivateMsg(fromQQ, "卡片数据格式错误，非json格式，请仔细检查。请参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
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
        coolQ.sendPrivateMsg(fromQQ, "恭喜！您成功制作了一张新卡片：" + newCard.getName());
    }

    public void viewCard(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font) throws Exception{
        if(!msg.equals("v")){
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
        cardList.append("以下是您已抽取到的卡片：\n");
        for (int i = 0; i < recordFiles.length; i++) {
            BufferedReader recordFileReader = new BufferedReader(new FileReader(recordFiles[i]));
            StringBuilder recordFileBuilder = new StringBuilder();
            String recordContent;
            while ((recordContent = recordFileReader.readLine()) != null){
                recordFileBuilder.append(recordContent);
            }
            CardDrawRecord cardDrawRecord = JSONObject.parseObject(recordFileBuilder.toString(), CardDrawRecord.class);
            cardList.append((i + 1) + ". " + cardDrawRecord.getCardName() + " * " + cardDrawRecord.getDrawNum() + "\n");
        }
        cardList.append("您可以通过指令 vd 列表序号/卡片名 来查看某张卡片的详细信息哦！比如：vd 1 or vd 卡片名");
        coolQ.sendPrivateMsg(fromQQ, cardList.toString());
    }

    public void viewCardDetail(CoolQ coolQ, int subType, int msgId, long fromQQ, String msg, int font) throws Exception{
        if(msg.equals("vd")){
            coolQ.sendPrivateMsg(fromQQ, "您没有指定查看的卡片。提示：您可以通过指令 vd 列表序号/卡片名 来查看某张卡片的详细信息哦！比如：vd 1 or vd 卡片名");
            return;
        }
        if(!msg.startsWith("vd ")){
            return;
        }

        String recordDirectoryUrl = coolQ.getAppDirectory() + "data\\draw_record\\" + fromQQ;
        File recordDirectoryFile = new File(recordDirectoryUrl);
        if(!recordDirectoryFile.exists() || recordDirectoryFile.list().length == 0){
            coolQ.sendPrivateMsg(fromQQ, "您一张卡片都没有...");
            return;
        }

        msg = msg.substring("vd ".length());
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
                coolQ.sendPrivateMsg(fromQQ, "抱歉!您还没有获得该卡片：" + msg + " , 您可以试试通过抽取得到哦");
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
        cardMessage.append("卡片名称：" + card.getName() + "\n");
        cardMessage.append("卡片描述：" + card.getDescription() + "\n");
        cardMessage.append("相关链接：" + card.getLinkUrl() + "\n");
        cardMessage.append("制卡人：" + card.getFromQQ());
        coolQ.sendPrivateMsg(fromQQ, cardMessage.toString());
    }

    public void drawCard(CoolQ coolQ, CQCode cQCode, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) throws Exception{
        if(!msg.equals("m")){
            return;
        }

        //String BASE_URL = "E:\\";

        // 获取卡片数据库
        String cardDBUrl = coolQ.getAppDirectory() + "data\\cards";
        //String cardDBUrl = BASE_URL + "data\\cards";
        File cardDB = new File(cardDBUrl);

        // 校验是否有卡片
        if(!cardDB.exists() || cardDB.list().length == 0){
            coolQ.sendGroupMsg(fromGroup, cQCode.at(fromQQ) + " 暂无卡片。您可以试试私聊我，自己创建新卡片哦！创建命令请参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}");
            return;
        }

        // 暂时设定为一半概率抽不到卡片 默认true为没抽到
        Random random = new Random();
        if(random.nextBoolean()){
            coolQ.sendGroupMsg(fromGroup, cQCode.at(fromQQ) + " 真可惜，您什么卡片都没有抽到呢！要不...再试试吧？");
            return;
        }

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

        // 发送成功提示信息
        StringBuilder successTip = new StringBuilder();
        successTip.append(cQCode.at(fromQQ) + " 恭喜您抽取到了：" + card.getName() + "\n");
        successTip.append("卡片名称：" + card.getName() + "\n");
        successTip.append("卡片描述：" + card.getDescription() + "\n");
        successTip.append("相关链接：" + card.getLinkUrl() + "\n");
        successTip.append("制卡人：" + cQCode.at(card.getFromQQ()));
        coolQ.sendGroupMsg(fromGroup, successTip.toString());
    }

    public void viewHelp(CoolQ coolQ, CQCode cQCode, int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font){
        if(!msg.equals("s -h") && !msg.equals("s -help")){
            return;
        }
        // 发送成功提示信息
        StringBuilder successTip = new StringBuilder();
        successTip.append(cQCode.at(fromQQ) + " 您好，小新是一个基础的抽卡系统，目前小新支持以下功能： \n");
        successTip.append("群聊场景：\n");
        successTip.append("\tm：抽卡\n");
        successTip.append("\ts -h or s -help：查看帮助\n");
        successTip.append("私聊场景：\n");
        successTip.append("\tc：制卡，制作新卡片（该卡片要通过抽取才能获得哦），参数为json格式的卡片数据，可参考：c {name:\"测试name\",description:\"测试description\",linkUrl:\"测试linkUrl\"}，name是卡片名（必填，最长20字符），description是卡片描述（必填，最长512字符），linkUrl是卡片相关链接（必填，最长1024字符）\n");
        successTip.append("\tv：查看已抽取的卡片列表\n");
        successTip.append("\tvd：查看某张卡片的详情，参数为 列表序号 or 卡片名，可参考：vd 1 or vd 卡片名\n");
        coolQ.sendGroupMsg(fromGroup, successTip.toString());
    }
}
