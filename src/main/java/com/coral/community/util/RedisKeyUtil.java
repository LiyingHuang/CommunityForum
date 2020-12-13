package com.coral.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_USER = "user";

    // Like of a Entity(/Post/Comment/Reply)
    // like:entity:entityType:entityId -> set(userId)
    // entityType: 1 Comment 2 Reply  entityId:PostId
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    // like quantity a user got
    // like:user:userId
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    // User's Followee / User follows Entity(User/Post)
    // followee : userId : entityType -> zset(entityId,nowtime-sort)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // Entity's Follower
    // follower: entityType : entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }



/*
---------------------------------------Optimize Login Process with Redis--------------------------------------------

*   1. Store the verification code with Redis
      * verificationCode need frequently access and refresh, need to have a great performance
      *  Don't need store it for a long time, will inactive in a short time
      *  Avoid the shared session problem when Distribution Deployment

*  2. Store the log ticket with Redis
      * we will check the log ticket when handle each request, high frequency

*  3. Cache the user information
      * check the user information according to the log ticket for each request, high frequency

* */


    private static final String PREFIX_KAPTCHA = "kaptcha";
    // Login verification Code
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    private static final String PREFIX_TICKET = "ticket";

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }
    // loginTicketMapper: SET it as unrecommended (@Deprecated)


    // 3. user
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }


}
