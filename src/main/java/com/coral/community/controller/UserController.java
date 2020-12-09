package com.coral.community.controller;

import com.coral.community.annotation.LoginRequired;
import com.coral.community.entity.User;
import com.coral.community.service.FollowService;
import com.coral.community.service.LikeService;
import com.coral.community.service.UserService;
import com.coral.community.util.CommunityConstant;
import com.coral.community.util.CommunityUtil;
import com.coral.community.util.HostHolder;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /*----------------------------Upload Profile-----------------------------------*/
    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","No picture have benn Choose");
            return "site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","file type not correct!");
            return "site/setting";
        }

        // generate random file name
        fileName = CommunityUtil.generateUUID() + suffix;
        // file store path
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // upload file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("Upload Failure: " + e.getMessage());
            throw new RuntimeException("Upload File Failure, Sever Exception",e);
        }

        // update user header url(web path)
        // http://localhost:8080/community/user/header/XXX.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName; // web path
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";

    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // sever store path
        fileName = uploadPath+"/"+fileName;
        // tell response the type of the upload file
        String suffix = fileName.substring(fileName.lastIndexOf("."));  // file type
        // response picture
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                ){
            // output with a buffer
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("Read Picture Failure: " + e.getMessage());
        }
    }

    /*----------------------------Change Password----------------------------*/
    @LoginRequired
    @RequestMapping(path = "/changepassword", method = RequestMethod.POST)
    public String changePassword(String prePassword, String newPassword, String confirmPassword,
                                 Model model, @CookieValue("ticket") String ticket){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        map = userService.changePassword(prePassword,newPassword,confirmPassword,user);
        if(map == null || map.isEmpty()){
            userService.logout(ticket);
            return "redirect:/login";
            //return "/site/login";
        }else {
            model.addAttribute("prePasswordMsg",map.get("prePasswordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg",map.get("confirmPasswordMsg"));
            return "site/setting";
        }
    }


    /*----------------------------Profile----------------------------*/
    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("User Not Exist!");
        }

        // User
        model.addAttribute("user",user);
        // like count the user got
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);


        // follow Count
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);


        // follower count
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);

        // follow status
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";

    }



}
