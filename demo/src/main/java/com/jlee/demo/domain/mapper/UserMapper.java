package com.jlee.demo.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jlee.demo.domain.User;
import org.springframework.stereotype.Repository;

/**
 * @author jlee
 * @since 2021/8/1
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
}
