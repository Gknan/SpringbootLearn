### 一、日志框架

小张：开发一个大型系统：

1、System.out.pirntln() 将关键数据打印在打印台，去掉？写在一个文件中？

2、框架来记录系统的一些运行时信息：日志框架，zhanglogging.jar

3、高大上的功能？~~异步模式~~？自动归档？xxxx？zhanglogging-good.jar

4、将以前的框架卸下来，换上新的框架，重新修改之前相关的API，zhanglogging-perfect.jar

5、JDBC -- 数据库驱动；写了一个统一的接口层：日志门面（日志的一个抽象层），logging-abstract.jar；给项目中导入具体的日志实现即可。之前的日志框架都是实现的抽象层；

市面上的日志框架：

Logback、log4j、log4j2、slf4j....

| 日志门面(日志的抽象层)                                       | 日志实现                                           |
| ------------------------------------------------------------ | -------------------------------------------------- |
| ~~JCL(Jakarta Commons logging)~~、 Slf4j(Sinple Logging Facada for Java)、 ~~Jboss-logging~~ | Log4j 、JUL(java.,util.logging) 、Log4j2 、Logback |

左边选一个门面（抽象层）、右边来选一个实现；

日志门面：Slf4j

日志实现：Logback

Spring Boot：底层是 Spring 框架，Spring 框架默认使用的是 JCL；Spring Boot **选用的是 SLF4J 和 Logback**

### 二、SLF4J使用

#### 1、如何在系统中使用SLF4j

以后开发的时候，日志记录方法的调用，不应该直接调用日志的实现类，而是调用日志抽象层的方法;

给系统中导入 slf4j 和 Logback 的 jar

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

slf4j 和 logback 的关系

![](pics\10-slf4j&logback.png)

每一个日志的实现框架都有自己的配置文件，使用 Slf4j 以后，**配置文件还是做成日志实现框架自己本身的配置文件**

#### 2、遗留问题

a系统 slf4j + logback:Spring(commons-logging) Hibernate(jjboss-logging) MyBatis、xxx

统一日志记录，即使是别的框架和我一起使用 slf4j 进行输出？

![](pics\11-统一日志记录使用Slf4j.png)

**如何让系统中所有得日志统一到 Slf4j？**

1、将系统各种其他日志框架先排除出去；

2、用中间包来替换原有的日志框架；

3、再来导入 Slf4j 其他的实现

### 三、Spring Boot 日志使用

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.2.6.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

Spring Boot 使用 spring-boot-starter-logging 做日志功能：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
    <version>2.2.6.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

![](pics\12-SpringBoot底层依赖关系.png)

总结：

1、Spring Boot 底层也是使用 slf4j + logback 的方式进行日志记录

2、Spring Boot 也把其他的日志替换成了 Slf4j

3、中间替换包？\jcl-over-slf4j-1.7.25.jar!\org\apache\commons\logging\LogFactory.class

```java
public abstract class LogFactory {

    static String UNSUPPORTED_OPERATION_IN_JCL_OVER_SLF4J = "http://www.slf4j.org/codes.html#unsupported_operation_in_jcl_over_slf4j";

    static LogFactory logFactory = new SLF4JLogFactory();
```

![](pics\13-SpringBoot日志中间转换包.png)

4、如果引入其他框架？一定要把这个框架的默认日志依赖移除掉？

Spring 框架使用的是 commons-logging

Spring Boot 1.5.9 采用的是排除 commons-logging
Spring Boot 2.2.6 不需要

```xml-dtd
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>5.2.5.RELEASE</version>
      <scope>compile</scope>
    </dependency>
```

最佳实践：**Spring Boot 能自动适配所有的日志，底层使用 Slf4j + logback 的方式记录日志，引入其他框架的时候，只需要把这个框架依赖的日志框架排除掉。**

### 四、默认配置

#### 1、默认配置

```java
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
```

| logging.file.name | logging.file.path | 例子     | 描述                                                         |
| ----------------- | ----------------- | -------- | ------------------------------------------------------------ |
| none              | none              |          | 只在控制台打印                                               |
| 特定的文件        | none              | my.log   | 写入指定的日志文件。 名称可以是确切的位置，也可以相对于当前目录。 |
| none              | 特定的文件夹      | /var/log | 将`spring.log`写入指定目录。 名称可以是确切的位置，也可以相对于当前目录。 |

日志输出格式

```xml
<!--
        日志输出格式：
			%d表示日期时间，
			%thread表示线程名，
			%-5level：级别从左显示5个字符宽度
			%logger{50} 表示logger名字最长50个字符，否则按照句点分割。 
			%msg：日志消息，
			%n是换行符
        -->
%d{yyyy-MM-dd HH:mm:ss.SSS} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n

2020-05-05 07:19:21.821  INFO 12368 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2020-05-05 07:19:21.825  INFO 12368 --- [           main] c.h.s.SpringBoot03LoggingApplication     : Started SpringBoot03LoggingApplication in 3.539 seconds (JVM running for 5.232)
```

Spring Boot 默认日志属性修改：

```properties
logging.level.cn.hust.springboot=trace
# 指定具体目录下的日志级别

# 在当前项目的root目录下创建 springboot2.log 文件
logging.file.name=springboot2.log
# 顶级项目中生成 springboot.log ，目录 日志文件默认是 spring.log
#logging.file.path=G:/springboot.log

# 在当前磁盘的根路径下创建 spring/log 文件夹，使用 spring.log 作为默认文件
#logging.file.path=/spring/log

# 控制台日志格式控制
logging.pattern.console=%d{yyyy-MM-dd} [ %thread ] - [ %-5level ] [ %logger{50} : %line ] - %msg%n
# 日志文件中格式控制
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} ==== [ %thread ] - [ %-5level\
   ] [ %logger{50} : %line ] - %msg%n
```

默认是追加的方式写日志

默认配置更多信息可以查看 
\org\springframework\boot\spring-boot\2.2.6.RELEASE\spring-boot-2.2.6.RELEASE.jar!\org\springframework\boot\logging 目录下的文件

#### 2、指定配置

[自定义日志格式](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-logging)

给类路径下放上每个日志框架自己的配置文件即可，SpringBoot 就使用自定义的配置文件

| 日志系统                | 定制                                                         |
| ----------------------- | ------------------------------------------------------------ |
| Logback                 | logback-spring.xml`, `logback-spring.groovy`, `logback.xml`, or `logback.groovy |
| Log4j2                  | log4j2-spring.xml` or `log4j2.xml                            |
| JDK (Java Util Logging) | logging.properties                                           |

logback.xml :直接被日志框架识别了

**logback-spring.xml** 日志框架不直接加载日志的配置项，由 Spring Boot 加载，好处是可以使用 Spring Boot 提供的功能根据不同的 profile 进行日志的配置。可以使用 Spring Boot 的高级 Profile 功能。

(两个都存在，logback-spring.xml 生效)

```xml
<appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <!--
        日志输出格式：
			%d表示日期时间，
			%thread表示线程名，
			%-5level：级别从左显示5个字符宽度
			%logger{50} 表示logger名字最长50个字符，否则按照句点分割。 
			%msg：日志消息，
			%n是换行符
        -->
        <layout class="ch.qos.logback.classic.PatternLayout">
            <!--根据 springProfile 的配置不同 profile 不同的格式-->
            <springProfile name="dev">
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} ------> [%thread] %-5level
                %logger{50} - %msg%n</pattern>
            </springProfile>

            <springProfile name="!dev">
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} ======= [%thread] %-5level
                    %logger{50} - %msg%n</pattern>
            </springProfile>
        </layout>
    </appender>
```

否则，若找不到 springProfile 标签

```
no applicable action for [springProfile]
```



### 五、切换日志框架

#### 1、按照 Slf4j 日志适配图，进行相关的切换：

1、切换日志实现为 log4j

2、排除原来的 logback

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>logback-classic</artifactId>
                    <groupId>ch.qos.logback</groupId>
                </exclusion>
            </exclusions>
        </dependency>
```



3、引入中间包 slf4j-log4j12.jar

4、加入 log4j 配置文件

#### 2、切换为 log4j 2

排除之前的 [`spring-boot-starter-logging`](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#spring-boot-starter-logging) 依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!--排除 starter-logging-->
            <exclusions>
                <exclusion>
                    <artifactId>spring-boot-starter-logging</artifactId>
                    <groupId>org.springframework.boot</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <!--切换日志框架为 log4j2-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>

```

