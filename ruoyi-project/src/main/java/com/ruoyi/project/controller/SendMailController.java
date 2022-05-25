package com.ruoyi.project.controller;

import com.ruoyi.common.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/project/mail")
public class SendMailController extends BaseController {
    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/test")
    public void send(){
        String subject="Test";
        String to = "1192827025@qq.com";
        String content = "test";
        SimpleMailMessage message = new SimpleMailMessage();
        String username = "2712544015@qq.com";
        //邮件主题
        message.setSubject(subject);
        //发件人
        message.setFrom(username);
        //收件人
        message.setTo(to);
        //内容
        message.setText(content);
        message.setSentDate(new Date());
        //执行发送
        javaMailSender.send(message);
    }
}
