package com.coral.community.config;

import io.lettuce.core.dynamic.RedisCommandFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;


/* Redis Spring */
/*
    1. import dependency
    2. Config Redis
        * Configurate  parameters of db
        * Write Configuration Class, create RedisTemplate
    3. Use Redis
        * redisTemplate.opsForValue()
        * redisTemplate.opsForHash()
        * redisTemplate.opsForList()
        * redisTemplate.opsForSet()
        * redisTemplate.opsForZSet()
*/

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory); // set the factory for the template, so it can access the db

        // java data saved to redis data(KEY-VALUE), so we need to define the serialization method

        // KEY serialization method
        template.setKeySerializer(RedisSerializer.string());

        // VALUE serialization method
        template.setValueSerializer(RedisSerializer.json());

        // key of HASH serialization method
        template.setHashKeySerializer(RedisSerializer.string());

        // value of HASH serialization method
        template.setHashKeySerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
