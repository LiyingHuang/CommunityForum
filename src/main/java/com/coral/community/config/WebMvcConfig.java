package com.coral.community.config;

import com.coral.community.controller.interceptor.AlphaInterceptor;
import com.coral.community.controller.interceptor.LoginRequiredInterceptor;
import com.coral.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /* --------------------demo-------------------*/
    // inject interceptor first then config it
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    // implement this to register the interceptor in a method to use
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(alphaInterceptor)
//                .excludePathPatterns("/css/*.css", "/js/*.js", "/**/*.png",  "/**/*.jpg", "/**/*.jpeg")// dont need intercept the static resources, /css (/**) all directory static
//                .addPathPatterns("/register", "/login");  // need intercept
//    }


    /*-------------LoginTicketInterceptor-------------*/
    // inject interceptor first then config it
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    // implement this to register the interceptor in a method to use
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/css/*.css", "/js/*.js", "/**/*.png",  "/**/*.jpg", "/**/*.jpeg")// dont need intercept the static resources, /css (/**) all directory static
                .addPathPatterns("/register", "/login");  // need intercept

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/*.css", "/js/*.js", "/**/*.png",  "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/css/*.css", "/js/*.js", "/**/*.png",  "/**/*.jpg", "/**/*.jpeg");
    }
}
