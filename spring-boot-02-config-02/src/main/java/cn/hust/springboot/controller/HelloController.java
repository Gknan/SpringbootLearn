package cn.hust.springboot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

//    @Value("${my.value}") // 获取值的时机和加载配置文件的时机先后问题
    private String name;

    @RequestMapping("/hello")
    public String hello() {
        return "hello " + name;
    }
}
