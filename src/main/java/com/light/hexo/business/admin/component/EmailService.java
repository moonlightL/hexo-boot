package com.light.hexo.business.admin.component;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @Author: MoonlightL
 * @ClassName: EmailService
 * @ProjectName: hexo-boot
 * @Description: 邮件相关
 * @DateTime: 2020/10/1 11:14 上午
 */
@Component
@Slf4j
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ConfigService configService;

    @Async
    public void sendEmail(String toEmail, String subject, String content) {

        if (StringUtils.isBlank(toEmail)) {
            return;
        }

        String email = this.configService.getConfigValue(ConfigEnum.EMAIL.getName());
        if (StringUtils.isBlank(email)) {
            log.info("======== sendEmail 邮箱地址未配置 =========");
            return;
        }

        String emailHost = this.configService.getConfigValue(ConfigEnum.EMAIL_HOST.getName());
        String username = this.configService.getConfigValue(ConfigEnum.EMAIL_USERNAME.getName());
        String password = this.configService.getConfigValue(ConfigEnum.EMAIL_PASSWORD.getName());
        if (StringUtils.isBlank(emailHost) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            log.info("======== sendEmail 发送邮件服务器/用户名/密码未配置 =========");
            return;
        }

       try {
           JavaMailSender sender = this.getSender(emailHost, username, password);

           Context con = new Context();
           con.setVariable("content", content);
           con.setVariable("homePage", this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName()));
           String emailTemplate = templateEngine.process("admin/email.html", con);
           MimeMessage message = sender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
           helper.setFrom(email, this.configService.getConfigValue(ConfigEnum.BLOG_NAME.getName()));
           helper.setTo(toEmail);
           helper.setSubject(subject);
           helper.setText(emailTemplate, true);
           sender.send(message);

           log.info("发送邮件内容: {}", content);
       } catch (Exception e) {
            e.printStackTrace();
           log.error("发送邮件失败: {}", e.getMessage());
       }

    }

    private JavaMailSender getSender(String host, String username, String password) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding("Utf-8");
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        prop.setProperty("mail.smtp.starttls.required", "true");
        prop.setProperty("mail.smtp.port", "465");
        prop.setProperty("mail.smtp.socketFactory.port", "465");
        prop.setProperty("mail.smtp.socketFactory.fallback", "false");
        prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sender.setJavaMailProperties(prop);
        return sender;
    }
}
