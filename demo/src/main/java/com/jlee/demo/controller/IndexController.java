package com.jlee.demo.controller;

import com.jlee.demo.constant.DemoResultStatus;
import com.jlee.exception.ApiException;
import com.jlee.result.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lijia
 * @since 2021/7/15
 */
@RestController("test")
public class IndexController {

    @GetMapping("index")
    public ResponseResult<String> index() {
        return ResponseResult.ok("hahah");
    }

    @GetMapping("testString")
    public String testString() {
        return "hahah";
    }

    @GetMapping("fail")
    public ResponseResult<Void> fail() {
        return ResponseResult.fail(DemoResultStatus.USER_REG_USER_PASSWORD_CONFIRM);
    }

    @GetMapping("httpfail")
    public ResponseResult<Void> httpOk() {
        return ResponseResult.status(DemoResultStatus.USER_REG_USER_PASSWORD_CONFIRM).build();
    }

    @GetMapping("entityOk")
    public ResponseEntity<String> entityOk() {
        return ResponseEntity.status(HttpStatus.OK).body("ceshi");
    }

    @GetMapping("exception")
    public ResponseEntity<String> exception() {
        throw new ApiException(DemoResultStatus.USER_REG_USER_PASSWORD_CONFIRM);
    }

    @GetMapping("httpTest")
    public ResponseEntity<Void> httpTest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
