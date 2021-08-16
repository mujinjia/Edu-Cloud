package com.jlee.controller;

import com.jlee.constant.DemoResultStatus;
import com.jlee.exception.ApiException;
import com.jlee.result.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jlee
 * @since 2021/7/15
 */
@RestController
@RequestMapping("/server")
public class IndexController {

    @GetMapping("/exception2")
    public ResponseResult<String> exception() {
        throw new ApiException(DemoResultStatus.TEST_FAILURE_STATUS);
    }
}
