package com.crongze;

import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@NoArgsConstructor
public class ServiceTest {
    @Autowired
    private DrawCard drawCard;

    @Test
    public void createCard(){
        //privateMsg(int subType, int msgId, long fromQQ, String msg, int font
        //drawCard.privateMsg(1, 1, 1111L, "c {name:\"名称\",description:\"描述\",linkUrl:\"相关链接\"}", 1);
    }

}
