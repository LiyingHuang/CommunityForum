package com.coral.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AlphaInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    // preHandle: before Controller process the request
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandler: " + handler.toString());
        return true;
    }

    // postHandle: called after the controller
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: " + handler.toString());
    }

    // afterCompletion: after the template Engine
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion: " + handler.toString());
    }

    // Then need config: config/webMvcConfig to use it



/*
/----------------------- we can also use customized annotation to use Interceptor -------------------------------/
* 1. use Interceptor
*    a. declare customized annotation before the method
*    b. intercept all request, only process methods with annotation
* 2. customized annotation
*    * @Target : declare the active part(method/field/class) of the annotation
*    * @Retention : active time(compile/runtime)
*    * @Document
*    * @Inherited : subclass inherited from parent class with customized annotation, declare inherit the annotation or not
* 3. how to read the annotation (Reflection)
*   * Method.getDeclaredAnnotations()
*   * Method.getAnnotation(Class<T> annotationClass)

* */


}
