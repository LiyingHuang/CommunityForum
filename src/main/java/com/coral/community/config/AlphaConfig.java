package com.coral.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration  // indicate its a config class (used to manage the other framework's annotation)
public class AlphaConfig {
    // convert java SimpleDateFormat to bean
    @Bean // indicate the return value of the method will be put into the spring container as a bean
    public SimpleDateFormat simpleDateFormat(){  // method name = bean name
        return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    }
}
