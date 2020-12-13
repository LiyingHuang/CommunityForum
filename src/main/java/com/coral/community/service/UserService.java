package com.coral.community.service;

import com.coral.community.dao.LoginTicketMapper;
import com.coral.community.dao.UserMapper;
import com.coral.community.entity.LoginTicket;
import com.coral.community.entity.User;
import com.coral.community.util.CommunityConstant;
import com.coral.community.util.CommunityUtil;
import com.coral.community.util.MailClient;
import com.coral.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {


    // 1. Try to get data from Cache
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2. store data in Redis Cache  when we can't get it from redis Cache
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    // 3. delete the cache data when a data updated
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    /*--------------------Register------------------ */

    /* 1. First, we need Send Active Mail,
    *  so we need the Template Engine to generate dynamic html,
    *  and MailClient to send the email */
    @Autowired
    private MailClient mailClient;  // create dependency util/MailClient, so we can inject here

    @Autowired
    private TemplateEngine templateEngine;

    /* 2. inject Project Name and Domain Name with @Value*/
    @Value("${server.servlet.context-path}")  // application.properties/severProperties: server.servlet.context-path=/community
    private String contextPath;               // Project Name/:/community

    @Value("${community.path.domain}")  // http:localhost:8080

    private String domain;

    /* 3. Register */
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        /* Check the if input parameter is Null */
        // use map to store the error msg, and return the map
        // 3.1. Null
        if(user == null){
            throw new IllegalArgumentException("Parameter can not be null !");
        }
        // username/password/email is null
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","Username can not be null !");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","Password can not be null !");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","Email can not be null !");
            return map;
        }
        // 3.2 Not null -> Validate
        // Validate the username
        User u = userMapper.selectByName(user.getUsername()); // Mapper-DAO
        if(u != null){
            map.put("usernameMsg","Username already exist!");
            return map;
        }
        // Validate the email
        u = userMapper.selectByEmail(user.getEmail()); // Mapper-DAO
        if(u != null){
            map.put("emailMsg","Email already exist!");
            return map;
        }

        /* 3.3. register user */
        // 3.3.1 add salt to the mad5 password
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));

        // 3.3.2 other
        //user.setUsername(user);
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        // String.format(placeholder %d, int pass into placeholder)
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        /* 4. send html active mail to Register user*/
        // consider: html model(resource/templates/mail/activation.html) + parameter

        // 4.1 Set email parameter of the activation.html
        Context context = new Context(); // thymeleaf object
        context.setVariable("email", user.getEmail());

        // 4.2 Define and set URL parameter of the activation.html
        // url: [domain]http://localhost:8080[contextPath]/community[]/activation/101(UserId)/xxx(ActivationCode)
        // url: http://localhost:8080/community/activation/101(UserId)/xxx(ActivationCode)
        // Mybatis: we already set auto generate id in property file,so we can use getId()
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        // 4.3 Generate HTML mail by Template Engine(thymeleaf)
        String content = templateEngine.process("/mail/activation",context);

        // 4.4 Send Mail
        mailClient.sendMail(user.getEmail(),"Activate Account", content);

        return map; // if the map is null, indicate no error
    }


    /* 4. Activate User Account by access Service*/
    // three results: 1.success   2. multiple Activate  3. Failure/Fake Activate Code
    // declare 3 constant to represent those status /util/CommunityConstant
    // from the activation URL "http://localhost:8080/community/activation/101(UserId)/xxx(ActivationCode)"
    // we can get : UserId + ActivationCode
    public int activation(int userId, String activationCode){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){   // repeat
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(activationCode)){ // activationCode is right/ not fake activationCode
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
    // -> loginController

    /*----------------------------------- Login --------------------------------------- */
    // as there are kinds of siutations can leads to loginFailure, so we return map
    // front end: password   sever: encrypted password

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        // 1. null value
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg","Username can not be null!");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg","Password can not be null!");
            return map;
        }

        // 2.1 Validate the username and password by compare with the dataset data
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","Account doesn't exist!");
            return map;
        }
        // 2.2 validate status
        if(user.getStatus()==0){
            map.put("usernameMsg","Account doesn't active!");
            return map;
        }
        // 2.3 validate password  ( md5(password+salt) )
        password = CommunityUtil.md5(password + user.getSalt());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","Password is wrong!");
            return map;
        }

        // 3. login successful -> generate login ticket/proof
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket); // Redis will serialize the loginTicket to a json string to store

        // 4. browser only need to memory the ticket
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    /*--------------------LogOut------------------ */
    // change status from 0 -> 1 (not active/validate)
    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }



    /*---------get LoginTicket object by ticket------- */
    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }


    /*---------------------update Header-------------- */
    public int updateHeader(int userId, String headerUrl){
        //return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;

    }


    /*--------------------Change Password------------------ */
    public Map<String, Object> changePassword(String prePassword, String newPassword, String confirmPassword, User user){
        Map<String, Object> map = new HashMap<>();
        // 1. null value
        if(StringUtils.isBlank(prePassword)) {
            map.put("prePasswordMsg","Previous password can not be null!");
            return map;
        }
        if(StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg","New password can not be null!");
            return map;
        }
        if(StringUtils.isBlank(confirmPassword)) {
            map.put("confirmPasswordMsg","Confirmed password can not be null!");
            return map;
        }

        // 2. Validate the password by compare with the dataset data
        String password = user.getPassword();

        prePassword = CommunityUtil.md5(prePassword + user.getSalt());

        if(!password.equals(prePassword)){
            map.put("prePasswordMsg","Previous password doesn't correct!");
            return map;
        }

        if(!newPassword.equals(confirmPassword)){
            map.put("confirmPasswordMsg","Confirmed password doesn't the same with the New Password!");
            return map;
        }

        // 3. update the password
        userMapper.updatePassword(user.getId(),CommunityUtil.md5(newPassword+user.getSalt()));
        return map;
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }


    // Optimize
    @Autowired
    private UserMapper userMapper;

    /* return one user */
    public User findUserById(int id){
        // return userMapper.selectById(id);

        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }


}
