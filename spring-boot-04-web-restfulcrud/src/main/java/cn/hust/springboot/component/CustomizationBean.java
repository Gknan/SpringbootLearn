package cn.hust.springboot.component;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

public class CustomizationBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    // 定制嵌入式 Servlet 容器相关的规则
    @Override
    public void customize(ConfigurableServletWebServerFactory server) {
        server.setPort(9000);
    }

}