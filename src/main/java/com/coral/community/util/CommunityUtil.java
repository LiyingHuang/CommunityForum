package com.coral.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

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
}
