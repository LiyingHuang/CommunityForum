package com.coral.community.controller.interceptor;

import com.coral.community.entity.LoginTicket;
import com.coral.community.entity.User;
import com.coral.community.service.UserService;
import com.coral.community.util.CookieUtil;
import com.coral.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/* Interceptor Demo
 *  1. Define interceptor, implement HandlerInterceptor
 *  2. Config Interceptor: assign the intercept/unintercept path
 *
 *  Interceptor application:
 *  1. query the user when the request start (cause we need the user information to be showed in the header)
 *  2. hold the user data in the request
 *  3. show the user data in the ModelView (put user in the model/Template to be used)
 *  4. destroy/clean up the user data after the request
 *
 * Browser            Server    DB
 * Cookie      ->   ticket -> login_ticket
 * (ticket)          user  <---------|
 *                     |
 *     <-----------template
 * */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. get ticket from cookie (we get cookie by request)
        // will encapsulate "get ticket from request" process in: util/cookieUtil
        String ticket = CookieUtil.getValue(request,"ticket");
        // 2. get loginTicket object(id,userId,ticket,status,expire) by ticket
        if(ticket != null){
            // check ticket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // check LoginTicket is valid or not
            if(loginTicket != null
                && loginTicket.getStatus() == 0  // status==0: already sign in
                && loginTicket.getExpired().after(new Date()) ){  // expired time >/after current time
                // get user by loginTicket
                User user = userService.findUserById(loginTicket.getUserId());
                // hold user in this request ( store the user )
                // sever handle request(one to many: multithreaded , have to store the user isolated for each thread)
                // util/hostHolder
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    // store the user to the Model to be used in the Front end/ before TemplateEngine
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView!= null){
            modelAndView.addObject("loginUser",user);
        }
    }

    // clear up user
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
    }

    //
}
