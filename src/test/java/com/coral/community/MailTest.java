package com.coral.community;

import com.coral.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Test
    public void testTestMail(){
        mailClient.sendMail("jiahao.quan@me.com", "hi", "wake up");
    }

    // use Thymeleaf send a HTML mail: resource/templates/mail/demo.html
    @Autowired
    private TemplateEngine  templateEngine; // managed by spring container

    @Test
    public void testHtmlMail(){
        /* 1. generate dynamic Html by thymeleaf */
        Context context = new Context(); // use thymeleaf Context to
        context.setVariable("username", "Sunny"); // set value of username
        String content = templateEngine.process("/mail/demo", context); // generate dynamic Html by thymeleaf
        System.out.println(content);
        /* 2. send Html mail*/
        mailClient.sendMail("jiahao.quan@me.com", "hi", content);
//<!DOCTYPE html>
//<html lang="en">
//<head>
//    <meta charset="UTF-8">
//    <title>Mail Demo</title>
//</head>
//<body>
//    <p>
//        <!--use thymeleaf to pass username dynamically-->
//                Welcome, <span style="color:palevioletred">Sunny</span>!
//                </p>
//</body>
//</html>
    }
}
