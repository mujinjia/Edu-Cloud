package com.jlee;

import com.jlee.annotation.EnableEnumMvcConfigure;
import com.jlee.annotation.EnableJacksonDateFormatConfigure;
import com.jlee.common.swagger.annotation.EnableCustomSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author jlee
 */
@EnableCustomSwagger
@SpringBootApplication
@EnableEnumMvcConfigure
@EnableJacksonDateFormatConfigure
@EnableDiscoveryClient
public class ServerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerDemoApplication.class, args);
    }

}
