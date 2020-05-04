package cn.hust.springboot;

import cn.hust.springboot.bean.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Spring Boot 单元测试
 *
 * 可以在测试期间很方便的类似编码一样进行自动注入等容器功能
 */
@SpringBootTest
class SpringBoot02ConfigApplicationTests {

    @Autowired
    private Person person;

    @Autowired
    private ApplicationContext ioc;

    @Test
    public void testHelloService() {
        boolean flag = ioc.containsBean("helloService");
        System.out.println(flag);
    }

    @Test
    void contextLoads() {
        System.out.println(person);
    }

}
