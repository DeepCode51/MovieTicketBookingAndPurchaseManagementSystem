package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;
    /**
     * 发送邮件
     * @param activationUrl
     * @param email
     */
    public void sendMailForActivaitionAccount(String activationUrl, String email) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true );
            //设置邮件主题
            message.setSubject("欢迎来到北辰电影院--个人账号激活");
            //设置邮件发送着
            message.setFrom(mailUsername);
            //设置邮件接受者，可以多个
            message.setTo(email);
            //设置邮件抄送人，可以有多个
//            message.setCc();
            //设置隐秘超偶是那个人，可以有多个
//            message.setBcc();
            //设置邮件发送日期
            message.setSentDate(new Date());
            Context context =new Context();
            context.setVariable("activationUrl",activationUrl);
//            String text = templateEngine.process("end/page/active-account.html", context);
            String text = templateEngine.process("front/active-account.html", context);
            //设置邮件正文
            message.setText(text,true);
        }catch (Exception e){
            e.printStackTrace();
        }
        //邮件发送
        javaMailSender.send(mimeMessage);
    }
}
