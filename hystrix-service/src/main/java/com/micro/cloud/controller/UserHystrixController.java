package com.micro.cloud.controller;

import com.micro.cloud.service.UserService;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserHystrixController {

    @Autowired
    private UserService userService;

    /**
     * 测试服务降级
     *
     * @return
     */
    @GetMapping("/testFallback")
    public String testFallback() {
        return userService.testFallback();

    }

    /**
     * 测试请求缓存
     *
     * @return
     */
    @GetMapping("/testCache")
    public String testCache() {
        userService.testCache(1l);
        userService.testCache(2l);
        userService.testCache(3l);
        return "测试缓存";
    }

    @GetMapping("/testCollapser")
    public String testCollapser() throws InterruptedException {
        userService.getUserFuture(1L);
//        userService.getUserFuture(2L);
//        Thread.sleep(200);
//        userService.getUserFuture(3L);
        return "测试请求合并";
    }

}
