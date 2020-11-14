package com.coral.community.controller;

import com.coral.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

@Controller         // indicate bean to the Scanner,can be replaced with @Component/@Repository/@Service/@Controller, applied to class
@RequestMapping("/alpha")
public class AlphaController {

    // interact with the  front end
    @RequestMapping("/hello")
    @ResponseBody   // indicate the content/string of return will be put in ResponseBody rather than return part of the url, and show in front end
    public String sayHello(){
        return "hello spring boot";
    }

    // controller have to call service while handle the request, inject service first
    @Autowired
    private AlphaService alphaService;
    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }
}
