package com.coral.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.incrementer.HsqlMaxValueIncrementer;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    /* Method 1: generate Random string, which can be used when upload picture and set random name for the picture */
    public static String generateUUID(){
        // dont want '-', just need the letters
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    /* Method 2: MD5+salt encryption, store the password with encryption*/
    // MD5: each time 12345 will be encrypt -> abc123def456, not secure,
    // Salt: each time add an random String to the abc123def456 -> ,more secure
    public static String md5(String key){
        // use StringUtils.isBlank from commons-lang3, which will check null/one space/null string
        if(StringUtils.isBlank(key)){
            return null;
        }
        // call the DigestUtils from Spring, need pass into bytes,return Hex
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /* pom.xml -> dependency:fastjson */
    /* post */
    /* encapsulate to a json object*/
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg",msg);
        if(map != null){
            for(String key : map.keySet()){
                jsonObject.put(key, map.get(key));
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name","lily");
        map.put("age",10);
        System.out.println(getJSONString(0,"ok",map));
    }

    // then go to Alpha Controller demo about ajax(asynchronous javaScript and XML/JSON)

}
