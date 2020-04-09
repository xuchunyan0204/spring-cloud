**一、微服务**
 
 微服务是一种架构风格，微服务架构是一个分布式系统，按照业务进行划分，成为不同的服务单元，服务单元可以单独部署，各服务单元之间是松耦合的。

**二、微服务之间的通信**

1.REST HTTP协议(如ribbon-service模块中UserRibbonController使用RestTemplate调用user-service模块中的服务)

2.RPC  TCP协议

**三、SpringCloud**
 
 SpringCloud是一套完整的微服务解决方案。它是一个容器，将市面上较好的微服务框架集成进来，简化开发

**四、eureka 服务治理**

1.eureka-server: 服务注册中心，专门用于其它服务注册

eureka-server、eureka-server-01为两个服务注册中心（集群），可以互相注册

eureka-security-server为带用户名、密码的服务注册中心，

client注册时需要带上用户名和密码，参见eureka-client模块中application-security.yml配置文件中defaultZone属性

2.eureka-client: 分为服务的提供者和服务的调用者(一个服务可以同时是服务的提供者和调用者)

服务的提供者：user-service

服务的调用者：ribbon-service

3.eureka常用配置

```
eureka:
   client: #eureka客户端配置
     register-with-eureka: true #是否将自己注册到eureka服务端上去
     fetch-registry: true #是否获取eureka服务端上注册的服务列表
     service-url:
       defaultZone: http://localhost:8001/eureka/ # 指定注册中心地址
     enabled: true # 启用eureka客户端
     registry-fetch-interval-seconds: 30 #定义去eureka服务端获取服务列表的时间间隔
   instance: #eureka客户端实例配置
     lease-renewal-interval-in-seconds: 30 #定义服务多久去注册中心续约
     lease-expiration-duration-in-seconds: 90 #定义服务多久不去续约认为服务失效
     metadata-map:
       zone: jiangsu #所在区域
     hostname: localhost #服务主机名称
     prefer-ip-address: false #是否优先使用ip来作为主机名
   server: #eureka服务端配置
     enable-self-preservation: false #关闭eureka服务端的保护机制
```

**五、Ribbon负载均衡**

1.RibbonConfig中使用@LoadBalanced赋予RestTemplate负载均衡的能力

2.同时启动多个user-service（使用配置文件配置不同端口号）ribbon-service调用user-service中的服务，会均衡的请求多个user-service

3.Ribbon配置

1）全局配置

```
ribbon:
  ConnectTimeout: 1000 #服务请求连接超时时间（毫秒）
  ReadTimeout: 3000 #服务请求处理超时时间（毫秒）
  OkToRetryOnAllOperations: true #对超时请求启用重试机制
  MaxAutoRetriesNextServer: 1 #切换重试实例的最大个数
  MaxAutoRetries: 1 # 切换实例后重试最大次数
  NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule #修改负载均衡算法
```

2）针对指定服务进行配置

```
user-service:
  ribbon:
    ConnectTimeout: 1000 #服务请求连接超时时间（毫秒）
    ReadTimeout: 3000 #服务请求处理超时时间（毫秒）
    OkToRetryOnAllOperations: true #对超时请求启用重试机制
    MaxAutoRetriesNextServer: 1 #切换重试实例的最大个数
    MaxAutoRetries: 1 # 切换实例后重试最大次数
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule #修改负载均衡算法
 ```   
3）负载均衡算法

**六.Hystrix断容器**：

1.使用原因

 微服务架构中，服务与服务之间通过远程调用的方式进行通信，一旦某个被调用的服务发生故障，其依赖的服务也会发生故障。并发生故障蔓延，

最终导致系统瘫痪，Hystrix实现了短路器模式，当某个服务发生故障时，通过断路器的监控，给调用方返回一个错误响应，而非长时间的等待。

2.功能：服务监控、服务降级、服务熔断、线程隔离、请求缓存、请求合并等

3.服务降级

1）演示：hystrix-service模块中发起请求http://localhost:8401/user/testFallback通过

```
@HystrixCommand(fallbackMethod = "getDefaultMethod")
```

注解在user-service服务断开时降级调用本模块中的getDefaultMethod方法

2）HystrixCommand常用参数

```
fallbackMethod：指定服务降级处理方法；
ignoreExceptions：忽略某些异常，不发生服务降级；
commandKey：命令名称，用于区分不同的命令；
groupKey：分组名称，Hystrix会根据不同的分组来统计命令的告警及仪表盘信息；
threadPoolKey：线程池名称，用于划分线程池。
```

4.请求缓存

1）相关注解

```
@CacheResult：开启缓存，默认所有参数作为缓存的key，cacheKeyMethod可以通过返回String类型的方法指定key；
@CacheKey：指定缓存的key，可以指定参数或指定参数中的属性值为缓存key，cacheKeyMethod还可以通过返回String类型的方法指定；
@CacheRemove：移除缓存，需要指定commandKey。
```

2）演示：hystrix-service模块中发起请求http://localhost:8401/user/testCache，控制台打印一次请求user-service,其余两次调用缓存

5.请求合并

1）注解及常用属性

```
@HystrixCollapser
    batchMethod：用于设置请求合并的方法；
    collapserProperties：请求合并属性，用于控制实例属性，有很多；
    timerDelayInMilliseconds：collapserProperties中的属性，用于控制每隔多少时间合并一次请求；
```

6.服务监控 Hystrix Dashboard

1）Hystrix Dashboard是spring cloud中用来查看Hystrix实例执行情况的一种仪表盘组件。支持查看单个实例和集群实例


notes:仅供自己学习，参考来源：[https://github.com/macrozheng/springcloud-learning]