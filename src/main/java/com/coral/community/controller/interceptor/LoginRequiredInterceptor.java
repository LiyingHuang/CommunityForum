package com.coral.community.controller.interceptor;

import com.coral.community.annotation.LoginRequired;
import com.coral.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // check the intercept object is a method or not
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // get method
            Method method = handlerMethod.getMethod();
            // get LoginRequired annotation from method
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // loginRequired != null : current method have the loginRequired annotation, need login to access this method
            // hostHolder.getUser() == null :  user not login
            if (loginRequired != null && hostHolder.getUser() == null){
                // need to use response to redirect ( in the interface ),
                // return "xxxx" in the bottom also use response.sendRedirect
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;

        // then need config : exclude the static resource --> WebMvcConfig
    }
}
