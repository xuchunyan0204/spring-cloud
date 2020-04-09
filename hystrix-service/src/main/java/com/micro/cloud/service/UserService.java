package com.micro.cloud.service;

import cn.hutool.core.collection.CollUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service-url.user-service}")
    private String userServiceUrl;

    @HystrixCommand(fallbackMethod = "getDefaultMethod")
    public String testFallback() {
        return restTemplate.getForObject(userServiceUrl + "/user/testFallback", String.class);
    }

    public String getDefaultMethod() {
        return "发生服务降级";
    }

    public String getDefaultMethod(Long id) {
        return "发生服务降级";
    }

    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(fallbackMethod = "getDefaultMethod", commandKey = "testCache")
    public String testCache(Long id) {
        System.out.println("请求user-service");
        return restTemplate.getForObject(userServiceUrl + "/user/testCache/{1}", String.class, id);
    }

    /**
     * 为缓存生成key的方法
     */
    public String getCacheKey(Long id) {
        System.out.println("调用缓存");
        return String.valueOf(id);
    }

    @HystrixCollapser(batchMethod = "getBatchMethod", collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "100")
    })
    public String getUserFuture(Long id) {
        System.out.println(id);
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", String.class, id);
    }

    @HystrixCommand
    public String getBatchMethod(List<Long> ids) {
        StringBuilder idStr = new StringBuilder();
        ids.forEach(value -> idStr.append(value).append(","));
        return restTemplate.getForObject(userServiceUrl + "/user/getBatchMethod?ids={1}", String.class,
                CollUtil.join(ids,","));
    }


}
