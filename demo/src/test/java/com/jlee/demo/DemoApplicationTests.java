package com.jlee.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlee.result.ResponseResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ResponseResult<?> responseResult = objectMapper.readValue("{\"code\":200,\"message\":\"测试\",\"data\":\"ceshi\"}", ResponseResult.class);
            System.out.println(responseResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
