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

}
