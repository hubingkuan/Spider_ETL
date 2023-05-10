package com.yeshen.appcenter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Date 2022/2/24/0024
 * author by HuBingKuan
 */
@Slf4j
public class SendMailUtil {
    private static final JavaMailSender MAIL_SENDER = SpringContextUtil.getBean(JavaMailSender.class);

    private static final SpringTemplateEngine SPRING_TEMPLATE_ENGINE = SpringContextUtil.getBean(SpringTemplateEngine.class);

    private static final String FROM = SpringContextUtil.getBean(Environment.class).getProperty("spring.mail.username");



    /**
     * 发送简单邮件
     * @param to      收件人地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param cc      抄送地址
     */
    public static void sendSimpleMail(String to,String subject, String content,String... cc) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        if(cc!=null){
            simpleMailMessage.setCc(cc);
        }
        MAIL_SENDER.send(simpleMailMessage);
    }

    /**
     * 发送带附件的邮件 非html格式
     * @param to       收件人地址
     * @param subject  邮件主题
     * @param content  邮件内容
     * @param filePath 附件地址
     * @param cc       抄送地址
     */
    public static void sendAttachmentMail(String to,String subject, String content,String filePath,String... cc) {
        try {
            MimeMessage mimeMessage = MAIL_SENDER.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(FROM);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            if(cc!=null){
                helper.setCc(cc);
            }
            addFile(filePath,helper);
            MAIL_SENDER.send(mimeMessage);
            log.info("发送邮件成功!");
        } catch (Exception e) {
            log.error("发送邮件失败!", e);
        }
    }



    /**
     * 根据模板发送文件
     */
    public static void sendThymeleafMail(String to,String subject, Object content,String templateName,String filePath,String... cc) {
        // 构建一个邮件对象
        MimeMessage mimeMessage = MAIL_SENDER.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件发送者,这个跟application.yml中设置的要一致
            helper.setFrom(FROM);
            // 设置邮件接收者，可以有多个接收者
            helper.setTo(to);
            // 设置抄送者
            if(cc.length>0){
                helper.setCc(cc);
            }
            // 设置邮件发送日期
            helper.setSentDate(new Date());
            //用于填充模板的参数
            Context context = new Context();
            Map<String, Object> hashMap = new HashMap<>(1);
            hashMap.put("message", content);
            context.setVariables(hashMap);
            // 两个参数:模板名称和内容
            String htmlContent = SPRING_TEMPLATE_ENGINE.process(templateName, context);
            // 设置邮件的正文 html格式
            helper.setText(htmlContent, true);
            addFile(filePath, helper);
            MAIL_SENDER.send(mimeMessage);
            log.info("发送邮件成功!");
        } catch (Exception e) {
            log.error("发送邮件失败!", e);
        }
    }

    private static void addFile(String filePath,MimeMessageHelper helper) throws MessagingException {
        if(StringUtils.hasText(filePath)){
            //添加附件
            AbstractResource file;
            String fileName;
            // http开头为网络资源文件
            if (filePath.startsWith("http")) {
                ResponseEntity<byte[]> responseEntity = HttpClientUtil.getPhotoBytesByUrl(filePath);
                fileName = filePath;
                file =new ByteArrayResource(responseEntity.getBody());
            }else{
                fileName = filePath.substring(filePath.lastIndexOf(File.separator));
                file = new FileSystemResource(new File(filePath));
            }
            helper.addAttachment(fileName, file);
        }
    }
}