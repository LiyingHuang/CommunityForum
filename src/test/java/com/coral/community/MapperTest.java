package com.coral.community;

import com.coral.community.dao.*;
import com.coral.community.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    /* -----------------------User-Mapper-----------------------*/
    /* -------GET------*/
    @Test
    public void testSelectUser(){

        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("LIYING101@sina.com");
        System.out.println(user);
    }

    /* -------ADD-----*/
    @Test
    public void testInsertUser(){
        User user = new User();

        user.setUsername("liying");
        user.setPassword("1111");
        user.setEmail("liying@qq.com");
        user.setHeaderUrl("www.kkkk");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);  // return the rows of insert
        System.out.println(rows);
        System.out.println(user.getId());
    }

    /* ------UPDATE------*/
    @Test
    public void updateUser() {

        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.qqqqq.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "123456");
    }

    /* -----------------------DiscussPost-Mapper-----------------------*/

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectPosts(){
        List<DiscussPost> postList = discussPostMapper.selectDiscussPosts(0,0,10);
        for(DiscussPost post : postList){
            System.out.println(post);
        }
//
//        // select count(id) from discuss_post where status !=2
//        int rows = discussPostMapper.selectDiscussionPostRows(0);
//        System.out.println(rows);
//
//        // select count(id) from discuss_post where status !=2 and user_id = #{userId}
//        int row = discussPostMapper.selectDiscussionPostRows(102);
//        // select count(id) from discuss_post where status !=2 and user_id = 101
//        System.out.println(row);
    }


    /* -----------------------loginTicket-Mapper-----------------------*/
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setId(1111);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectByTicket(){
        loginTicketMapper.selectByTicket("abcd");
    }

    @Test
    public void testUpdateStatus(){
        loginTicketMapper.updateStatus("abcd",1);
    }


    /* -----------------------add Post with ajax and Jquery Test-----------------------*/
    @Test
    public void testAddPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("Find Job !!!!!!");
        discussPost.setContent("aaàáâäzaaaaaaaa");
        discussPost.setStatus(1);
        discussPost.setType(1);
        discussPost.setCreateTime(new Date());
        System.out.println(new Date());
        discussPostMapper.insertDiscussPost(discussPost);
    }

    /*---------------------------------------Comment Mapper--------------------------------------*/
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void testCommentMapper(){
        // List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);
        List<Comment> commentList= commentMapper.selectCommentsByEntity(1,275,0,5);
        System.out.println(commentList);

        //int selectCountByEntity(int entityType, int entityId);
    }
    @Test
    public void testCommentCount(){
        System.out.println(commentMapper.selectCountByEntity(1,275));
    }
    @Test
    public void testInsertComment(){
        Comment comment = new Comment();
        comment.setCreateTime(new Date());
        commentMapper.insertComment(comment);
    }

    @Test
    public void testUpdateCommentCount(){
        discussPostMapper.updateCommentCount(286,1);
    }


    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for(Message message : list){
            System.out.println(message);
        }
    }
    @Test
    public void testSelectConversationCount(){
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);

        List<Message> list = messageMapper.selectLetters("111_112",0,10);
        System.out.println(list);

        int count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);

    }

    @Test
    public void testInsertMessage(){
        Message message = new Message();
        message.setFromId(112);
        message.setToId(111);
        message.setConversationId("111_112");
        message.setContent("1111test INSERT mESSAGE");
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageMapper.insertMessage(message);

        Message message1 = new Message();
        message1.setFromId(112);
        message1.setToId(111);
        message1.setConversationId("111_112");
        message1.setContent("2222test INSERT mESSAGE");
        message1.setCreateTime(new Date());
        message1.setStatus(0);
        messageMapper.insertMessage(message1);

        List<Integer> list = new ArrayList<>();
        list.add(message.getId());
        list.add(message1.getId());

        messageMapper.updateStatus(list,1);
    }



    @Test
    public void testNotification(){
        System.out.println(messageMapper.selectLatestNotice(111,"comment"));

    }

    @Test
    public void testNotices(){
        System.out.println(messageMapper.selectNotices(111,"comment",0,5));

    }
}

