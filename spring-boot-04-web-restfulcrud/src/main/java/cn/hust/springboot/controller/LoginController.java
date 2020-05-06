package cn.hust.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

//    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    // 如果请求参数上没有指定的参数，报错 @RequestParam("username")
    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String, Object> map,
                        HttpSession session) {
        if (!StringUtils.isEmpty(username) && "123456".equals(password)) {
            // 登录成功 防止表单重复提交，重定向
//            return "dashboard.html";
            // Request 域中添加了 loginUser 用来将来判断是否已经登录
            session.setAttribute("loginUser", username);
            return "redirect:/main.html";
        } else {
            // 登录失败
            map.put("msg", "用户名或密码错误");
            return "login";
        }
    }
}
