package com.coral.community.controller;

import com.coral.community.entity.Comment;
import com.coral.community.entity.DiscussPost;
import com.coral.community.entity.Page;
import com.coral.community.entity.User;
import com.coral.community.service.CommentService;
import com.coral.community.service.DiscussPostService;
import com.coral.community.service.LikeService;
import com.coral.community.service.UserService;
import com.coral.community.util.CommunityConstant;
import com.coral.community.util.CommunityUtil;
import com.coral.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
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

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        // get post by id
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        // get the post author information to show (1. get in xml by myBatis 2. the following method)
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // Like information
        // count
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);
        // status
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        /*------------------------------------------CommentPart----------------------------------------------*/
        // comment Paging Information
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount()); // comment quantity

        // CommentList of a specific Post
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST,post.getId(),page.getOffset(), page.getLimit());

        // Entity_id -> user : show the post comment, mean while show the user header/username
        // Vo : View Object, map is used to view/show
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (commentList != null){
            for(Comment comment : commentList){
                // CommentVO
                Map<String, Object> commentVo = new HashMap<>();
                // comment
                commentVo.put("comment", comment);
                // Author/user who made the comment
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // Comment Like information
                // count
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                // status
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);


                // ReplyList/CommentList of a specific comment
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                // ReplyVoList
                List<Map<String, Object>> replyVoList = new ArrayList<>();

                if(replyList != null){
                    for (Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        // reply
                        replyVo.put("reply", reply);
                        // reply author
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // choose reply target (post or user)
                        User target = reply.getTargetId() == 0?null:userService.findUserById(reply.getTargetId());

                        // Reply Like information
                        // count
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        // status
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);


                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys", replyVoList);
                // reply quantity
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
