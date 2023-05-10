package com.yeshen.appcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeshen.appcenter.domain.valid$json.User;
import com.yeshen.appcenter.repository.mysql.UserDAO;
import com.yeshen.appcenter.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Date 2022/04/25  13:53
 * author by HuBingKuan
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDAO, User> implements UserService {
}