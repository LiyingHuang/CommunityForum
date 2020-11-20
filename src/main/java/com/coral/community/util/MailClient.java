package com.coral.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;
    // JavaMailSender interface/Component is manage by  Spring Container, so we can inject directly

    /*  ---------Sender/title/subject are the same--------  */
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // true: text -> html
            helper.setText(content,true);
            // send mail: get mail from the helper
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("Send Mail Fail: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
