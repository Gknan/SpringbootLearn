package cn.hust.springboot.config;

import cn.hust.springboot.component.CustomizationBean;
import cn.hust.springboot.filter.MyFilter;
import cn.hust.springboot.listener.MyListener;
import cn.hust.springboot.servlet.MyServlet;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.Arrays;

@Configuration
public class MyServerConfig {

    // 注册三大组件
    // 注册 Servlet
    @Bean
    public ServletRegistrationBean myServlet() {
        ServletRegistrationBean<Servlet> registrationBean =
                new ServletRegistrationBean<>(new MyServlet(), "/myservlet");

        return registrationBean;

    }

    // 注册Filter
    @Bean
    public FilterRegistrationBean myFilter() {
        FilterRegistrationBean<Filter> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(new MyFilter());
        registrationBean.setUrlPatterns(Arrays.asList("/hello", "/myservlet"));

        return registrationBean;
    }

    // 注册 Listener
    @Bean
    public ServletListenerRegistrationBean myListener() {
        ServletListenerRegistrationBean registrationBean = new ServletListenerRegistrationBean(new MyListener());

        return registrationBean;
    }

    // 配置嵌入式 Servlet 容器
    @Bean
    public CustomizationBean customizationBean() {
        return new CustomizationBean();
    }

}
