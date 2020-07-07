package com.crongze.util;

import org.springframework.web.client.RestTemplate;

public class QQNickUtil {
    private static final String BASE_URL = "https://r.qzone.qq.com/fcg-bin/cgi_get_score.fcg?mask=7&uins=";
    private static RestTemplate restTemplate = new RestTemplate();

    public static String getQQNick(long qq){
        String url = BASE_URL + qq;
        String response = restTemplate.getForObject(url, String.class);
        if(response == null){
            return "匿名";
        }
        response.replace("", "");
        response.replace("", "");
        return "匿名";
    }
}
