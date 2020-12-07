package com.coral.community.service;

import com.coral.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

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

}
