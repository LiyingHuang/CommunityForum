package com.coral.community.dao;

import com.coral.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {
    /*-----------------------Paging-----------------------*/
    // when we search a post, we dont need the userId
    // passing userId because later we'll have a function for each user - "My posts", and we can call this method
    // Paging Function : so the return type should be List
    // Dynamic SQL: (concat the userId or not)
    // sql Paging: start row id (offset), maximum row (limit)
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /*-------------Count the row number of posts------------*/
    // @Param("NickName")
    // if we need concat userId dynamically within <If>, and there are only one parameter, we have to use @Param
    int selectDiscussionPostRows(@Param("userId") int userId);


    /*-----------------------add Post with ajax and Jquery-----------------------*/
    int insertDiscussPost(DiscussPost discussPost);
    // implement it in mapper

    /*-----------------------get posts by post Id-----------------------*/
    /* -----Details of the Post Content------
     * DiscussPostMapper DiscussPostService DiscussPostController
     * index.html: add the post content url/path in the index html
     * discuss-detail.html
     *  * handle the utl/path of the static resource
     *  * reuse header
     *  * show the post title, user, post_time,content
     * */
    DiscussPost selectDiscussPostById(int id);
    // then go to mapper.xml write the query



}
