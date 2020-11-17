package com.coral.community.service;

import com.coral.community.dao.DiscussPostMapper;
import com.coral.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


    /* */
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussionPostRows(userId);
    }

}
