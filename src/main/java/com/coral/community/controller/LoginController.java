package com.coral.community.controller;

import com.coral.community.entity.User;
import com.coral.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController {

    /* Register and active the account */
    @Autowired
    private UserService userService; // Register+SendActiveEmail Function

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "site/register";  // /resource/templates/site/register.html
    }


    @RequestMapping(path = "/register", method = RequestMethod.POST)
    // return the view name
    // model: store the data and pass to model
    // user: save the user information(username,password,email), we can also use 3 parameter to save them
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        // map==null ==> successfully register -> activation -> homepage
        if(map == null || map.isEmpty()){
            // offer the note msg to  templates/site/operate-result.html
            model.addAttribute("msg", "Registration is successful, we have sent an activation email to your mailbox, please activate as soon as possible.");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }

}
