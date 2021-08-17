package com.jlee.demo.service;

import com.jlee.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author lijia
 * @since 2021/8/16
 */
@FeignClient(name = "service-provider", path = "/server")
public interface ServerDemoApi {
    @GetMapping("/exception2")
    ResponseEntity<ResponseResult<String>> exception();
}
