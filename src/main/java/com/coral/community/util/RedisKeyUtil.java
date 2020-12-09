package com.coral.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";

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

}
