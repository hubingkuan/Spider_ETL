package com.yeshen.appcenter.controller;

import com.yeshen.appcenter.domain.valid$json.InsertGroup;
import com.yeshen.appcenter.domain.valid$json.User;
import com.yeshen.appcenter.service.UserService;
import com.yeshen.appcenter.utils.HttpClientUtil;
import com.yeshen.appcenter.utils.SendMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.groups.Default;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Date 2022/04/21  22:43
 * author by HuBingKuan
 *
 * @Validated中的value针对不同的接口进行不同的限制条件(可能CRUD的接口校验的参数不一致)
 */
@Slf4j
@RestController
public class TestController {
    @Autowired
    private UserService userService;

    @GetMapping("/test/email")
    public void testEmail() {
        SendMailUtil.sendSimpleMail("hubingkuan@noxgroup.com", "测试内网邮箱", "hello" + "\n" + "world!");
    }

    @GetMapping("/test")
    // {Default.class} 默认分组生效，也就是实体类的校验注解不标注group
    public User test(@RequestBody @Validated(value = {Default.class}) User user1) {
        System.out.println(user1);
        User user = new User();
        //user.setName("");
        //user.setAge(10);
        user.setBir(LocalDateTime.now());
        return user;
    }

    @GetMapping("/test2")
    public User test2(@RequestBody @Validated(value = {InsertGroup.class}) User user1) {
        System.out.println(user1);
        User user = new User();
        //user.setName("");
        //user.setAge(10);
        user.setBir(LocalDateTime.now());
        return user;
    }

    @PostMapping("/save")
    public void list(@RequestBody @Validated(InsertGroup.class) User user) {
        userService.save(user);
    }

    @GetMapping("/http")
    public void resttemplate() throws IOException {
        String url = "https://res09.bignox.com/appcenter/tw/2022/04/放肆武林icon.png";
        byte[] bytes2 = HttpClientUtil.getRequest(url, byte[].class);
    }
}