package cn.hust.springboot.controller;

import cn.hust.springboot.exception.UserNotExistException;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MyExceptionHandler {

//    @ResponseBody
//    @ExceptionHandler(UserNotExistException.class) // 拦截的异常
//    public Map<String, Object> handlerException(Exception e) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", "user not exist");
//        map.put("msg", e.getMessage());
//        return map;
//    }

    @ExceptionHandler({UserNotExistException.class}) // 拦截的异常
    public String handlerException(Exception e, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        // 传入自己的错误状态码，4xx 5xx。不传则是 200
        request.setAttribute("javax.servlet.error.status_code", 500);

        map.put("code", "user not exist");
        map.put("msg", e.getMessage());

        // 把自己定义的信息放到 request 中，在 ErrorAttributes 中获取
        request.setAttribute("ext", map);
        // 转发到 /error
        return "forward:/error";
    }
}
