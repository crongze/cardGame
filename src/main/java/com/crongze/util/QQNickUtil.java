package com.crongze.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.client.RestTemplate;

public class QQNickUtil {
    private static final String BASE_URL = "https://r.qzone.qq.com/fcg-bin/cgi_get_score.fcg?mask=7&uins=";
    private static RestTemplate restTemplate = new RestTemplate();

    public static String getQQNick(long qq){
        String url = BASE_URL + qq;
        // portraitCallBack({"***QQ号":["http://qlogo4.store.qq.com/qzone/***QQ号/***QQ号/100",0,-1,0,0,0,"昵称",0,0,-1]});
        String response = restTemplate.getForObject(url, String.class);
        if(response == null){
            return "匿名";
        }
        response = response.replace("portraitCallBack(", "");
        response = response.replace(");", "");
        JSONObject callBackJson = JSONObject.parseObject(response);
        JSONArray infoArray = (JSONArray) callBackJson.get(String.valueOf(qq));
        return (String) infoArray.get(6);
    }
}
