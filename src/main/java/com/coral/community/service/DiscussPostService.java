package com.coral.community.service;

import com.coral.community.dao.DiscussPostMapper;
import com.coral.community.entity.DiscussPost;
import com.coral.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    /*
    * Return content of this method contains a foreign key
    * which shouldn't be show, we'd better show the userName
    * so, we need to declare the findUserById in Service Layer /service/UserService/findUserById.method
    * then combine the userName and Post (when use redis cache, its will be better)
    * */
    public List<DiscussPost> findDiscussPost(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }


    /* --------------------------------find Discuss Post Rows------------------------------*/
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussionPostRows(userId);
    }

    /* -----------------------------------add post---------------------------------------- */
    @Autowired
    private SensitiveFilter sensitiveFilter;
    public int addDisscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new IllegalArgumentException("Argument cannot be null");
        }

        // handel <script></script>, escape(zhuan yi)HTML
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // filter banned word
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    /* -----------------------------------post content detail----------------------------------*/
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

}
