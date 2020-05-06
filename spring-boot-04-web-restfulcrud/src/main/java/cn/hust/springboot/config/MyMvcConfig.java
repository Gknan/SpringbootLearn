package cn.hust.springboot.config;

import cn.hust.springboot.component.CustomizationBean;
import cn.hust.springboot.component.LoginHandlerInterceptor;
import cn.hust.springboot.component.MyLocaleResolver;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;

// 使用  WebMvcConfigurer 扩展 SpringMVC 的功能
@Configuration
//@EnableWebMvc // 全面接管 SpringMVC 不使用 SpringBoot 提供的自动配置
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // super.addViewControllers
        // 浏览器发送 /hust 来到 success 页面
        registry.addViewController("/hust").setViewName("success");
    }

    // 所有的 WebMvcConfigure 都生效
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        WebMvcConfigurer mvcConfigurer = new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                registry.addViewController("/main.html").setViewName("dashboard");
            }

            // 注册拦截器
//            @Override
//            public void addInterceptors(InterceptorRegistry registry) {
//
//                // 静态资源  css js Spring Boot 做好了静态资源映射了，我们不用处理
//                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns(
//                        "/index.html", "/", "/user/login", "/asserts/**", "/webjars/**");
//            }
        };
        return mvcConfigurer;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }
}
