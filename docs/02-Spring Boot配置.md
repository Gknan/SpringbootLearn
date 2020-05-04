> 配置文件
>
> 加载顺序
>
> 配置原理

### 一、配置文件

Spring Boot 使用一个全局的配置文件，配置文件名是固定的：

applilcation.properties

application.yml

配置文件的作用：修改 Spring Boot 自动配置的默认值。Spring Boot 在底层都给我们配置好。

YAML（YAML ai'nt marup language）

标记语言：以前的配置文件，大多使用的是 xml 文件。

YAML：以数据为中心

```yaml
server:
  port: 8081
```

XML:

```xml
<server>
    <port>8081</port>
</server>
```

### 二、YAML 语法

#### 1、基本语法

`K: V` 表示一对键值对（V前必须有空格）

以空格的缩进控制层级关系；只要是左对齐的一列数据，都是同一个层级的

```yaml
server:
	port: 8081
	path: /hello
```

属性和值也是大小敏感的；

#### 2、值的写法

**字面量：普通的值（数字、字符串、布尔）**
k: v 直接来写

字符串默认不用加上单引号或双引号；
""；双引号，不会转移字符串中的特殊字符；特殊字符作为本身表示的意思

name: 'zhangsan \n list' 输出 zhangsan 换行 list

'': 单引号，转移特殊字符，特殊字符最终只是一个普通的字符串数据
name: 'zhangsan \n list' 输出 zhangsan \n list

**对象（属性和值--键值对）**

k: v
对象是 k: v的方式表示，在下一行写对象的属性和值的关系，注意缩进

```yaml
friends:
	lastName: zhangsan
	age: 20
```

行内写法：

```yaml
friends: {lastName: zhangsan,age: 18}
```

**数组（List，Set）**
用 - 值表示数组中的一个元素：

```yaml
pets:
	- cat
	- dog
	- pig
```

行内写法

```yaml
pets: [cat,dog,pig]
```

PS：解决 IDEA 中提示 `Spring Boot Configuration Annotaion Processor not found in classpath`

加入以下依赖

```xml
        <!--导入配置文件处理器，配置文件进行绑定就会有提示-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
        </dependency>
```

使用 Spring Boot Initializer 创建项目 JUnit5 单元测试卡在 resolving 界面的解决：

```xml
<!--spring boot JUnit 单元测试卡在 revolving junit-platform-launcher-->
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-launcher</artifactId>
            <scope>test</scope>
        </dependency>
```

### 三、获取配置文件值注入

#### 1、@ConfigurationProperties

@ConfigurationProperties 默认从全局配置文件中获取值

配置文件

```yaml
person:
  last-name: zhangsan
  age: 18
  boss: false
  birth: 2018/2/2
  maps: {k1: v1,k2: 12}
  lists:
    - lisi
    - zhaoliu
  dog:
    name: 小强
    age: 2

```

JavaBean

```java

/**
 * 将配置文件中配置的每一个属性的值绑定到这个组件中
 * @ConfigurationProperties 告诉 Spring Boot 将本类中所有的属性和配置文件中相关的配置进行绑定
 * prefix = "person" 配置文件中哪个下面的所有属性进行一一映射
 *
 * 只有这个组件是容器中的组件，才能使用容器提供的 @ConfigurationProperties 功能
 */
@Component
@ConfigurationProperties(prefix = "person")
public class Person {

    private String lastName;
    private Integer age;
    private Boolean boss;
    private Date birth;

    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
```

我们可以导入配置文件处理器，以后编写配置就有提示了

```xml
        <!--导入配置文件处理器，配置文件进行绑定就会有提示-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
        </dependency>
```

使用 application.properties 文件写配置文件

```properties
# 默认 IDEA 使用的是 utf-8 需要转成 ASCII
# 在 settings -> File Encodings 里面设置
# person 属性值
person.last-name=张三
person.age=19
person.birth=2019/2/2
person.boss=false
person.maps.k1=v1
person.maps.k2=v2
person.lists=a,b,c
person.dog.name=dog
person.dog.age=4
```

PS：application.properties 中文乱码问题及解决

#### 2、@Value 获取值和 @ConfigurationProperties 获取值比较

|                      | @ConfigurationProperties | @Value       |
| -------------------- | ------------------------ | ------------ |
| 功能上               | 批量注入配置文件中的属性 | 一个一个注定 |
| 松散绑定（松散语法） | 支持                     | 不支持       |
| SpEL                 | 不支持                   | 支持         |
| JSR303 数据校验      | 支持                     | 不支持       |

配置文件 yml 还是  properties 都能获取到值

如果说，只是在某个业务逻辑中需要获取一下某个配置文件中的某项值，使用 @Value；

如果专门编写一个 JaveBean 来和配置文件进行映射，我们就直接使用 @ConfigurationProperties

#### 3、注入值数据校验

```java
@Component
@ConfigurationProperties(prefix = "person")
@Validated // JSR303 数据校验支持
public class Person {

    /**
     * <bean class="Person">
     *     <property name="lastName" value="字面量/${key}
     *     从环境变量配置文件中获取值/#{SpEL}"></property>
     * </bean>
     */
//    @Value("${person.last-name}")
    @Email // 校验规则， lastName 必须是邮箱格式
    private String lastName;
```

#### 4、@PropertySource @ImportResource

@PropertySource：加载指定的配置文件

```java
@PropertySource(value = {"classpath:person.properties"})
@Component
@ConfigurationProperties(prefix = "person")
//@Validated // JSR303 数据校验支持
public class Person {

    /**
     * <bean class="Person">
     *     <property name="lastName" value="字面量/${key}
     *     从环境变量配置文件中获取值/#{SpEL}"></property>
     * </bean>
     */
//    @Value("${person.last-name}")
//    @Email // 校验规则， lastName 必须是邮箱格式
    private String lastName;

//    @Value("#{11*2}")
    private Integer age;

//    @Value("true")
    private Boolean boss;
    private Date birth;

//    @Value("person.maps") // @Value 不支持 复杂类型数据注入
    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
```

 @ImportResource：导入 Spring 的配置文件，让配置里面的内容生效

添加 HelloService 类

```java
public class HelloService {
}
```

Spring Boot 里面没有 Spring 的配置文件，我们自己编写的配置文件也不能自动识别。想让 Spring 配置文件生效，加载进来：使用 @ImportResource标注在主配置类上。

```java
@ImportResource(locations = {"classpath:beans.xml"}) 
@SpringBootApplication
public class SpringBoot02ConfigApplication {
```

@ImportResource(locations = {"classpath:beans.xml"})  导入 Spring 的配置文件让其生效
Spring 的配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="helloService" class="cn.hust.springboot.service.HelloService"></bean>

</beans>
```

Spring Boot 推荐给容器中添加组件的方式：使用全注解的方式

1、配置类 == Spring 配置文件

2、使用 @Bean 给容器中添加组件

```java
/**
 * @Configuration 注明当前类是配置类，用来替代之前的 Spring 配置文件
 * 在配置文件中 <bean></bean> 标签添加组件
 */
@Configuration
public class MyAppConfig {

    // 将方法的返回值添加到容器中，容器中这个组件默认的 id 就是方法名
    @Bean
    public HelloService helloService() {
        System.out.println("配置类@Bean给容器添加组件了。。。");
        return new HelloService();
    }
}
```

### 四、配置文件占位符

#### 1、随机数

```xml
${random.value}
${random.int}
${random.long}
${random.int(10)}
```

#### 2、占位符获取之前配置的值，若没有使用冒号指定默认值

```properties
person.last-name=张三${random.uuid}
person.age=${random.int}
person.dog.name=${person.hello:hello}_dog
# 找不到 ${person.hello}就是使用冒号后面的默认值 hello
#person.dog.name=${person.hello}_dog
```

### 五、Profile

> Profile 是 Spring 对不同环境提供不同配置功能的支持，可以通过激活、指定参数等方法快速切换环境

#### 1、多 Profile 文件

在主配置编写的时候，文件名可以是：application-{profile}.yml/properties

默认使用 application.properties/yml 的配置

#### 2、yml 支持多文档块方式

```yaml
server:
  port: 8081

spring:
  profiles:
    active: prod # 指定激活的 profile
---
server:
  port: 8082
spring:
  profiles: dev # 在文档快中定义 profile 特定的属性
---
server:
  port: 8083
spring:
  profiles: prod

```



#### 3、激活指定的 profile

1、在配置文件中指定 spring.profiles.active=dev

2、命令行：

```bash
java -jar --spring.profiles.active=dev
```

IDEA 中可以在 run -> configuration->environment -> program arguments： `--spring.profiles.active=dev`

3、虚拟机参数

run->configuration->enviroment->VM Options:  `-Dspring.profiles.active=dev`



### 六、配置加载位置

Spring Boot 启动会扫描以下位置的 applicaiton.properties 或者 application.yml 文件作为 Spring Boot 的默认配置文件

flie: ./config/ 

file: ./ **当前项目根目录下 如果是 父 pom 下的子项目启动，这里的配置位置还是父项目根路径下的配置文件，不会去子项目下非资源路径找**

classpath: /config/

classpath: /

以上是按照优先级从高到低的顺序，所有位置的文件都会被加载。高优先级的配置会覆盖低优先级的配置。SpringBoot 会从这四个位置全部加载配置文件。**互补配置**。

高优先级配置部分内容，低优先级配置所有内容。

可以使用 spring.config.location 指定配置文件位置

**项目打包以后，可以使用命令行参数的形式，启动项目的时候指定配置文件的新位置，指定的配置文件和默认加载的配置文件共同起作用，形成互补配置。**

### 七、外部配置加载顺序

Spring Boot 支持多种外部配置方式，可以从以下位置加载配置，**按照优先级从高到低，高优先级的配置覆盖低优先级的配置，不同内容则形成互补配置。**

1. **命令行参数**

   ```bash
   java -jar myapp.jar --server.port=8087 --context-path=/abc
   ```

   多个配置用空格分开：--配置项=值

2. 来自 java:comp/env 的 NDI 属性

3. Java 系统属性（System.getProperties）

4. 操作系统环境变量

5. RandomValuePropertySource 配置的 random.* 属性值

6. **jar 包外部的 application-{profile}.properties/yml(带 spring.profile) 配置文件**

7. **jar 包内部的 application-{profile}.properties/yml(带 spring.profile) 配置文件**

8. **jar 包外部的 application.properties/yml(不带 spring.profile) 配置文件**

9. **jar 包内部的 application.properties/yml(不带 spring.profile) 配置文件**

10. @Configuration 注解类上的 @PropertySource

11. 通过 SpringApplication.setDefaultProperties 指定的默认属性

6/7/8/9 总结：优先加载带 profile ，再来加载不带 profile；由 jar 包外向 jar 包内进行寻找

6 8 外部的配置文件要和 jar 包在同一文件夹下

![](G:\learn-skills\springbootlearn\docs\pics\09-spring外部化配置.png)

[更多详细参考官方文档](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-external-config)

### 八、自动配置原理











