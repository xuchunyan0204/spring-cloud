package com.micro.cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/info")
    public String getInfo() {
        LOGGER.info(new Date() + "调用用户服务");
        return "调用用户服务";
    }

}