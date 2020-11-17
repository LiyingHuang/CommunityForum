package com.coral.community.controller;

import com.coral.community.entity.DiscussPost;
import com.coral.community.entity.Page;
import com.coral.community.entity.User;
import com.coral.community.service.DiscussPostService;
import com.coral.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller   // path can be ignore
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    /* GET 10 Page */

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0)); // get the total rows
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPost(0,page.getOffset(),page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();  // store the result of concat
        // get userName by id, then concat the post and userName
        if(list != null){
            for(DiscussPost post : list){
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId()); // get user
                map.put("user", user);
                discussPosts.add(map);
            }
        }

        // before we called this method, springMVC/dispatch servlet will instantiate the Model and Page, and inject Page into Model
        // so, we can access the data of the Page Object in Thymeleaf
        // so, we dont need to model.addAttribute to declare Page Object

        model.addAttribute("discussPosts", discussPosts);// data that will display on the front end need to store in Model with a name
        model.addAttribute("page",page);
        return "/index";
    }
}
