package cn.hust.springboot;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
class SpringBoot03LoggingApplicationTests {

    @Test
    void contextLoads() {
        // 记录器
        Logger logger = LoggerFactory.getLogger(SpringBoot03LoggingApplicationTests.class);
        // 日志级别，由低到高 trace debug info warn error
        // 可以调整输出的日志级别，日志就只会在这个级别和高于这个级别的生效
        logger.trace("这是trace日志....");
        logger.debug("这是debug日志...");

        // Spring Boot 默认日志级别使用的是 info 级别，可以在 application.propperties
        // 中指定哪个包的日志级别，没有指定的使用 默认级别
        logger.info("这是info日志...");
        logger.warn("这是warn日志...");
        logger.error("这是error日志...");

    }

}
