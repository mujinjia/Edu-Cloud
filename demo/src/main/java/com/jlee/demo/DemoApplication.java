package com.jlee.demo;

import com.jlee.annotation.EnableEnumMvcConfigure;
import com.jlee.annotation.EnableJacksonDateFormatConfigure;
import com.jlee.common.swagger.annotation.EnableCustomSwagger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author jlee
 */
@EnableCustomSwagger
@SpringBootApplication
@MapperScan("com.jlee.demo.domain.mapper")
@EnableEnumMvcConfigure
@EnableJacksonDateFormatConfigure
@EnableDiscoveryClient
@EnableFeignClients
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
