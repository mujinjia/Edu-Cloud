package com.jlee.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jlee.demo.domain.User;
import com.jlee.demo.domain.mapper.UserMapper;
import com.jlee.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author lijia
 * @since 2021/8/1
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
