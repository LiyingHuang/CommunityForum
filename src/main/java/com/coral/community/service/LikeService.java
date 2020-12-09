package com.coral.community.service;

import com.coral.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;
/*
    // Like
    public void like(int userId, int entityType, int entityId){
        // redis key
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // check if the user already liked or not
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if(isMember){
            // cancel like
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }else{
            // like
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    // query like count
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // like status
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
*/


    /*
     *  "how many like I got"
     *  Reconstruct the Like function
     *     1. use user as key, count the like user get
     *     2. increment(key), decrement(key)
     *
     *  Develop Profile
     *     1. user as key, count the like
     * */


    // Like
    public void like(int userId, int entityType, int entityId, int entityUserId){
//        // redis key
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        // check if the user already liked or not
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if(isMember){
//            // cancel like
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else{
//            // like
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }

        // Transaction
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                // query must out of the transaction
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);
                redisOperations.multi();// transaction start

                if(isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else{
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });

    }

    // query like count
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // like status
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // query the like count of a user
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }

}
