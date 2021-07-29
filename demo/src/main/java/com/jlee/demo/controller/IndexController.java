package com.jlee.demo.controller;

import com.jlee.demo.constant.DemoResultStatus;
import com.jlee.exception.ApiException;
import com.jlee.result.ResponseResult;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author jlee
 * @since 2021/7/15
 */
@RestController("test")
public class IndexController {

    @PostMapping("/index")
    public ResponseResult<LocalDateTime> index(@RequestBody TestTime testTime) {
        return ResponseResult.ok(LocalDateTime.now());
    }

    @GetMapping("/testString")
    public String testString() {
        return "hahah";
    }

    @GetMapping("/fail")
    public ResponseResult<Void> fail() {
        return ResponseResult.fail(DemoResultStatus.USER_REG_USER_PASSWORD_CONFIRM);
    }

    @GetMapping("/httpfail")
    public ResponseResult<Void> httpOk() {
        return ResponseResult.status(DemoResultStatus.USER_REG_USER_PASSWORD_CONFIRM).build();
    }

    @GetMapping("/exception")
    public ResponseResult<String> exception() {
        throw new ApiException(DemoResultStatus.USER_REG_USER_PASSWORD_CONFIRM);
    }

    @GetMapping("/entityOk")
    public ResponseEntity<String> entityOk() {
        return ResponseEntity.status(HttpStatus.OK).body("ceshi");
    }

    @GetMapping("/httpTest")
    public ResponseEntity<Void> httpTest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @GetMapping("/file")
    public ResponseResult<byte[]> file() {
        return ResponseResult.file("测试文件.txt", "测试文件哦");
    }


    @Data
    public static class TestTime {
        private LocalDateTime localDateTime;
    }

}
