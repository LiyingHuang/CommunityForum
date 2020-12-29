package com.coral.community;

import com.coral.community.entity.DiscussPost;
import com.coral.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    @Autowired
    private DiscussPostService postService;

    private DiscussPost post;

    // 只调用一次 static
    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }
    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass");
    }

    // 有几个test调用几次 not static
    @Before
    public void before() {
        System.out.println("before : initialize the instance");
        post = new DiscussPost();
        post.setUserId(999);
        post.setTitle("Test Title");
        post.setContent("Test Content hhhhhhhhhhhhhhhh");
        post.setCreateTime(new Date());
        // postService.addDisscussPost(post);
    }
    @After
    public void after() {
        System.out.println("after : delete the instance");
    }

    // test
    @Test
    public void test1(){
        System.out.println("test1");
    }
    @Test
    public void test2(){
        System.out.println("test2");
    }

}
