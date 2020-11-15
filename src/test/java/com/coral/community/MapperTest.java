package com.coral.community;

import com.coral.community.dao.UserMapper;
import com.coral.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;


    /* -----------------------GET-----------------------*/
    @Test
    public void testSelectUser(){

        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("LIYING101@sina.com");
        System.out.println(user);
    }

    /* ------------------------ADD-----------------------*/
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

    /* ---------------------UPDATE-----------------------*/
    @Test
    public void updateUser() {

        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.qqqqq.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "123456");
    }
}
