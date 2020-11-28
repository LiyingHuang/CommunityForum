package com.coral.community.controller;

import com.coral.community.entity.DiscussPost;
import com.coral.community.entity.User;
import com.coral.community.service.DiscussPostService;
import com.coral.community.service.UserService;
import com.coral.community.util.CommunityUtil;
import com.coral.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"not login! ");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());
        discussPostService.addDisscussPost(discussPost);

        // error msg will handle in the future
        return CommunityUtil.getJSONString(0,"post success! ");
    }

    /*
    * ---------------------Details of the Post Content------------------------
    * DiscussPostMapper DiscussPostService DiscussPostController
    * index.html: add the post content url/path in the index html
    * discuss-detail.html
    *  * handle the utl/path of the static resource
    *  * reuse header
    *  * show the post title, user, post_time,content
    * */
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        // get post by id
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        // get the post author information to show (1. get in xml by myBatis 2. the following method)
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // comment

        return "/site/discuss-detail";
    }
}
