package com.jlee.demo.controller;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jlee.demo.constant.DemoResultStatus;
import com.jlee.exception.ApiException;
import com.jlee.result.ResponseResult;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author jlee
 * @since 2021/7/15
 */
@RestController
public class IndexController {


    @ResponseBody
    @PostMapping("/index")
    public ResponseResult<TestRest> index(@RequestBody TestTime testTime) {
        final TestRest testRest = new TestRest();
        testRest.setLocalDateTime(LocalDateTime.now());
        return ResponseResult.ok(testRest);
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


    public enum Gender {
        /**
         * 性别男
         */
        MALE(10, "男"),
        /**
         * 性别女
         */
        FEMALE(20, "女");

        private final Integer code;
        @JsonValue

        private final String description;

        Gender(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        @JsonCreator
        @JSONCreator
        public static Gender create(String value) {
            try {
                return Gender.valueOf(value);
            } catch (IllegalArgumentException e) {
                for (Gender gender : Gender.values()) {
                    try {
                        if (gender.code.equals(Integer.parseInt(value))) {
                            return gender;
                        }
                    } catch (NumberFormatException n) {
                        if (gender.description.equals(value)) {
                            return gender;
                        }
                    }
                }
                throw new IllegalArgumentException("没有元素匹配 " + value);
            }

        }

        public Integer getCode() {
            return code;
        }

        @JSONField
        public String getDescription() {
            return description;
        }
    }

    @Data
    public static class TestTime {
        //        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JSONField(format = "yyyy+MM+dd HH:mm:ss")
        private LocalDateTime localDateTime;
        @NotNull
        private Gender gender;
    }

    @Data
    public static class TestRest {
        //        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JSONField(format = "yyyy/MM/dd HH:mm:ss")
        private LocalDateTime localDateTime;
        private Gender gender = Gender.FEMALE;
    }

}
