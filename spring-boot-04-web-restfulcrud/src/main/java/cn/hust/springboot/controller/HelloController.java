package cn.hust.springboot.controller;

import cn.hust.springboot.exception.UserNotExistException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

@Controller
public class HelloController {

//    @RequestMapping({"/", "/index.html"})
//    public String index() {
//        return "index";
//    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam("user") String user) {

        if ("aaa".equals(user)) {
            throw new UserNotExistException();
        }

        return "Hello!";
    }

    // 查出一些数据，在页面展示
    @RequestMapping("/success")
    public String success(Map<String, Object> map) {
        // classpath:/templates/ thymeleaf
//        map.put("hello", "您好"); // 数据被放到请求域中
        map.put("hello", "<h1>你好</h1>"); // 数据被放到请求域中
        map.put("users", Arrays.asList("zhangsan", "lisi", "david"));
        return "success";
    }
}
