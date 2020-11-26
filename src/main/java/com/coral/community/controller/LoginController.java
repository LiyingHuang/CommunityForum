package com.coral.community.controller;

import com.coral.community.entity.User;
import com.coral.community.service.UserService;
import com.coral.community.util.CommunityConstant;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    /* Register and active the account */
    @Autowired
    private UserService userService; // Register+SendActiveEmail Function

    /* visit the Register Page */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "site/register";  // /resource/templates/site/register.html
    }

    /* visit the login Page */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "site/login";  // /resource/templates/site/login.html
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    // return the view name
    // model: store the data and pass to model
    // user: save the user information(username,password,email), we can also use 3 parameter to save them
    public String register(Model model, User user){  // pass into 2 parameter, user will be put to model automatically
        Map<String, Object> map = userService.register(user);
        // map==null ==> successfully register -> activation -> homepage
        if(map == null || map.isEmpty()){
            // offer the note msg to  templates/site/operate-result.html
            model.addAttribute("msg", "Registration is successful, we have sent an activation email to your mailbox, please activate as soon as possible.");
            model.addAttribute("target", "/index");
            // map == null => no input error => jump to /site/operate-result
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            // map != null => input error => jump to site/register and show error msg
            return "site/register";
        }
    }

    // success/failure => operate_result show the result message for both situation
    // success => login to use
    // failure => homepage(/index) without login
    // "http://localhost:8080/community/  activation/101(UserId)/xxx(ActivationCode)  "
    @RequestMapping(path = "/activation/{userId}/{ActivationCode}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("ActivationCode") String activationCode){
        int result = userService.activation(userId, activationCode); // result - activation status code/result
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","Activation Success !"); // set value for the declared variable in HTML/frontEnd
            model.addAttribute("target","/login");            // HTML refer to the return part "/site/operate-result"
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","Activation Multiple Times !");
            model.addAttribute("target","/index"); // in site/operate-result.html: set the target to /index
        }else{
            model.addAttribute("msg","Activation Failure !");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }


    // the generate picture will be used in the client, but the code should be saved in the server with session
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private Producer kaptchaProducer;
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 1. create verification code
        String text = kaptchaProducer.createText();
        // 2.  create picture with verification code
        BufferedImage image = kaptchaProducer.createImage(text);

        // 3. verification code store in session
        session.setAttribute("kaptcha", text);

        // 4. picture send to browser. need declare the type of output
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os); // dont need close, spring will do that
        } catch (IOException e) {
            logger.error("response verification code Failure: " + e.getMessage());
        }
    }


    /* ---------------------------------Login(Post)--------------------------------------------- */

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password,String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response){    // code: verificationCode
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha)  || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","Verification Code Incorrect!");
            return "/site/login";
        }

        // check username,password,expired time
        // rememberMe: expired time become longer (both store in db, later in Redis) -> util/CommunityConstant
        int expiredSecond = rememberMe ? REMEMBER_EXPIRED_TIME : DEFAULT_EXPIRED_TIME;

        Map<String, Object> map = userService.login(username, password, expiredSecond);
        // success
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath); // set active path/range
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie); // send cookie to browser/page by response to show it on the inspect
            return "redirect:/index";   // redirect
        }else{
            // Model: need to show something on page/html, cause html will use the thymeleaf Model Engine to do it
            // send errorMsg to page by Model
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /* -----------------LogOut----------------- */
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

}
