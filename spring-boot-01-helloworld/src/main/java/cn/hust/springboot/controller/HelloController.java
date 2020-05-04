package cn.hust.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
public class HelloController {

    @ResponseBody // 返回值封装到 响应体中返回
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
