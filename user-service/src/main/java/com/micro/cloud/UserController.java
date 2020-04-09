package com.micro.cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/info")
    public String getInfo() {
        LOGGER.info(new Date() + "调用用户服务");
        return "调用用户服务";
    }

    @GetMapping("/testFallback")
    public String testFallback() {
        return "测试Hystrix服务降级";
    }

    @GetMapping("/testCache/{id}")
    public String testCache(@PathVariable Long id) {
        return "测试Hystrix请求缓存" + id;
    }

    @GetMapping("/getBatchMethod")
    public List<String> getBatchMethod(@RequestParam List<Long> ids) {
        List<String> resultList = new ArrayList<>();
        ids.forEach(value -> resultList.add("测试Hystrix请求合并" + value));
        return resultList;
    }


}