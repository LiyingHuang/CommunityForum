package com.coral.community.controller;

import com.coral.community.service.AlphaService;
import com.coral.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;

@Controller
// indicate bean to the Scanner,can be replaced with @Component/@Repository/@Service/@Controller, applied to class
@RequestMapping("/alpha")
public class AlphaController {

    // interact with the  front end
    @RequestMapping("/hello")
    @ResponseBody
    // indicate the content/string of return will be put in ResponseBody rather than return part of the url, and show in front end
    public String sayHello() {
        return "hello spring boot";
    }

    // controller have to call service while handle the request, inject service first
    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    // how to get the request/response data (base/bottom way)
    // dispatch servlet will help us find the HttpServletResponse/Response
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());//GET
        System.out.println(request.getServletPath());//alpha/http
        // get the header, contains multiple key-value pairs
        Enumeration<String> enumeration = request.getHeaderNames(); // got the all keys
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
//host:localhost:8080
//connection:keep-alive
//cache-control:max-age=0
//upgrade-insecure-requests:1
//user-agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.193 Safari/537.36
//accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
//sec-fetch-site:none
//sec-fetch-mode:navigate
//sec-fetch-user:?1
//sec-fetch-dest:document
//accept-encoding:gzip, deflate, br
//accept-language:en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7
//cookie:Idea-9e542ce2=0f90c486-dd80-480c-bbd9-f342464a621b

        // http://localhost:8080/community/alpha/http?name=liying
        System.out.println(request.getParameter("name")); // liying

        // return response data
        // set the return type
        response.setContentType("text/html;charset=utf-8");
        // response can get the output stream
        try (PrintWriter writer= response.getWriter();){   // will close the writer automatically
            //PrintWriter writer= response.getWriter();
            writer.write("<h1>CoralForum</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // through writer output content to the front end

    }


    /*
    * GET request
    * --- frontEnd/Browser/client want get data from sever ---
    * --- how sever get parameter from the frontEnd url ---
    *  */
    // --- 1. @RequestParam ---
    // /student?current=1&limit=20      // we can use this to do Paging Function later
    @RequestMapping(path = "/students", method = RequestMethod.GET)  // restriction on the request method
    @ResponseBody
    //  /students?current=1&limit=20
    // public String getStudents(int current, int limit){   // will auto mapping according to the same name
    public String getStudents(
            // specific the input parameter
            @RequestParam (name = "current", required = false, defaultValue = "1") int current,
            int limit){
        System.out.println(current);  // 1
        System.out.println(limit);    // 20
        return "some Students";
    }

    // --- 2. @PathVariable ---
    // /student/2
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
     public String getStudent( @PathVariable("id") int id){
        System.out.println(id);  // 1
        return "a Students";
    }


    /*
     * POST request
     * --- frontEnd/Browser/client  post data to sever ---
     *
     * use POST rather than GET
     * 1. use GET, the url/parameter will appear on the browser, but some information we want to hide like password
     * 2. the length of the url is limited
     *  */
    // -------creat a static website--------
    //  http://localhost:8080/community/html/AlphaStudent.html
    //  resource-static: creat a static website - AlphaStudent.html
    //  frontend url -> html form url -> controller path
    // static.html can be accessed from the frontEnd by
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    // we can also add the @RequestParam
    public String saveStudent(String namE, int agE){
        System.out.println(namE);
        System.out.println(agE);
        return "success!";
    }

    /*
     * sever/controller response a dynamic HTML
     * 2 ways
     *  */
    // ----- 1 -----

    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    // @ResponseBody dont need this, default return is html
    // (model+view) + thymeleaf model engine-> dynamic html
    // dispatchServlet will called the method which return the  ModelAndView of Controller
    public ModelAndView getTeacher(){  // return the model+view object to the dispatchServlet and offer it to the model engine
        ModelAndView mav = new ModelAndView();
        // pass the dynamic data to ModelAndView
        mav.addObject("name","liying");
        mav.addObject("age", 300);
        // set the ModelAndView's url(Templates Directory) and file name(view = view.html)
        // then put the model to Templates Directory
        mav.setViewName("/demo/view");
        return mav;
    }

    // ----- 2 better-----
    // http://localhost:8080/community/alpha/teacher
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){   // return string -> view path
        // parameter model is instantiate by the dispatchServlet when called the method
        // Model: save data to the model
        model.addAttribute("name","Peking University");
        model.addAttribute("age",100);
        return "/demo/view";// return the view path (html) to the dispatchServlet,
                            // at the same time, dispatchServlet have the reference of the model,
                            // so dispatchServlet can do : (model+view) + thymeleaf model engine-> dynamic html
    }

    // ------response JSON datatype（Asynchronous request）------
    // Asynchronous request: the page not refresh, but already access the sever/db, and return a response
    //      sign up in a website: type the username, then response "this user name is already benn used"
    //      which means the process of access the sever -> database -> response already happen, without refresh the page
    // JAVA object(BackEnd) -> JSON -> JavaScript object(Browser/FrontEnd wish to receive JS object)
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name", "lilian");
        emp.put("age", 20);
        emp.put("salary", 80000);
        return emp; // with @ResponseBody/emp/Map<String, Object>, dispatchServlet will return a JSON automatically
    }


    // return multiple emps
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String,Object> emp = new HashMap<>();
        emp.put("name", "lilian");
        emp.put("age", 20);
        emp.put("salary", 80000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "Bilian");
        emp.put("age", 27);
        emp.put("salary", 70000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "Cilian");
        emp.put("age", 70);
        emp.put("salary", 60000);
        list.add(emp);

        return list; // DispatchServlet will return a JSON automatically
        // [
        // {"name":"lilian","salary":80000,"age":20},  // object 1
        // {"name":"Bilian","salary":70000,"age":27},  // object 2
        // {"name":"Cilian","salary":60000,"age":70}   // object 3
        // ]

    }

    /*Cookie is a txt file which is created and sent by the serve, store in the client side*/
    @RequestMapping(path="/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        // 1. create cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 2. set cookie active range
        cookie.setPath("/community/alpha");
        // 3. active time(default: close the page, cookie disappear)
        cookie.setMaxAge(60*10); // 10min
        // 4. add cookie to response object, then send
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    /* Session, can be generate by springMVC, we can injection*/
    @RequestMapping(path="/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session";
        // client => Set-Cookie: JSESSIONID=27E19375058C7B60FD4E8EFF46B17411; Path=/community; HttpOnly
    }

    @RequestMapping(path="/session/get",method = RequestMethod.GET)
    @ResponseBody
    public void getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
    }

    /* DistributeSystem(multiple server): Session is not good，
     * as there are many session, we might access another session the next time
     * */


    /*----------demo about ajax(asynchronous javaScript and XML/JSON)---------------*/
    @RequestMapping(path="/ajax", method = RequestMethod.POST)
    @ResponseBody // return string, not html
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"success!");
    }
    // then create a html page with jquery code: -> resource/static/html/ajax demo.html/



}
