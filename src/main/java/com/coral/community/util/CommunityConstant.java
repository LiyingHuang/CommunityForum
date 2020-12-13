package com.coral.community.util;

/* 3 constants <=> 3 activate Status */
public interface CommunityConstant {
    /* Activate Successfully */
    int ACTIVATION_SUCCESS = 0;
    /* Activate Multiple Times */
    int ACTIVATION_REPEAT = 1;
    /* Activate Failure */
    int ACTIVATION_FAILURE = 1;

    /* Remember Me or Not */
    // 1. default ( Not Remember)
    int DEFAULT_EXPIRED_TIME= 3600 * 12; // 3600s * 12 -> 12h
    // 2. Remember
    int REMEMBER_EXPIRED_TIME = 3600 * 12 * 100; // 100 DAYS


    /* Entity Type (Comment on post/comment)*/
    // post
    int ENTITY_TYPE_POST = 1;
    // comment
    int ENTITY_TYPE_COMMENT = 2;
    // User
    int ENTITY_TYPE_USER = 3;


    /*Topic : comment*/
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";

    /*System UserId */
    int SYSTEM_USER_ID = 1;

}
