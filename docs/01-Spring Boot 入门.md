

### 一、简介

> 简化 Spring 应用开发，约定大于配置，去繁就简
>
> 对 Spring 整个技术栈的整合
>
> J2EE 开发的一站式解决方案

![](G:\learn-skills\springbootlearn\docs\pics\01-spring&springboot&user.png)

优点：

* 快速创建独立运行的 Spring 项目以及与主流框架集成
* 使用嵌入式的 Servlet 容器，应用无需打成 war 包
* starters 自动依赖与版本控制
* 大量的自动配置、简化开发，也可以修改默认值
* 无需配置 XML，无代码生成，开箱即用
* 准生产环境的运行时应用监控
* 与云计算的天然集成

缺点：

* 入门容易，精通难
* 精通 Spring Boot 的前提是精通 Spring

### 二、微服务

2014 Martin fowler

微服务：架构风格，一个应用是一组小型服务，可以通过 HTTP 的方式互通；

单体应用：
![](G:\learn-skills\springbootlearn\docs\pics\02-单体应用.png)

问题：

* 小修改牵动全身

* 大需求

微服务

![](G:\learn-skills\springbootlearn\docs\pics\03-微服务应用.png)

每个功能元素都是可独立替换和升级的单元。

[详细参考文档](https://martinfowler.com/articles/microservices.html)

![](G:\learn-skills\springbootlearn\docs\pics\04-微服务网络图.png)

![](G:\learn-skills\springbootlearn\docs\pics\05-Spring全家桶.png)

Spring Boot 用来构建微服务，Spring Cloud 用来协调调用各个微服务，Spring Cloud Data Flow 完成微服务之间的联通。

* 需要掌握的内容
  * Spring 
  * Maven
  * IDEA
* 环境约束
  * JDK 1.8
  * Maven 3.3+
  * Intellij IDEA2017
  * SpringBoot 1.5.9.RELEASE

### 三、环境准备

#### 1、Maven 设置 

yourmavendir/conf/settings.xml 添加下面代码，表示使用 JDK 1.8 编译

```xml
	<profile>
	  <id>jdk-1.8</id>
	  <activation>
		<activeByDefault>true</activeByDefault>
		<jdk>1.8</jdk>
	  </activation>

	  <properties>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
		<maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
	  </properties>
	</profile>
  </profiles>
```

#### 2、IDEA 设置

setting->Maven(搜索)->使用自己的maven和maven配置文件



### 四、 Spring Boot HelloWorld

功能：浏览器发送 hello 请求，服务器接收请求并处理，响应 Hello World 字符串。

#### 1、创建 Maven 工程

#### 2、导入 Spring Boot 相关的依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
	<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
```

#### 3、编写主程序

```java
/**
 * @SpringBootApplication 标注一个主程序类，说明这是一个 Spring Boot 应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    /* Spring 应用启动入口函数 */
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}

```

#### 4、编写 Controller

```java
@Controller
public class HelloController {

    @ResponseBody // 返回值封装到 响应体中返回
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}

```

#### 5、启动并访问 localhost:8080/hello

#### 6、简化部署

pom 中导入 Maven 插件

```xml
    <!--该插件的作用是可以将应用打包成一个可执行的 jar 包-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

打包后是可执行 jar 包，可通过 下面的命令 启动项目

```bash
java -jar target\spring-boot-01-helloworld-1.0-SNAPSHOT.jar
```

解压 jar 包，可看到 BOOT-INF/lib 下，有项目，由项目需要的 jar 包。
![](G:\learn-skills\springbootlearn\docs\pics\06-打包后的jar中的lib下的文件.png)



### 五、HelloWorld 探析

#### 1、POM  文件

##### 1、父项目

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
<!--他的父pom-->
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-dependencies</artifactId>
		<version>1.5.9.RELEASE</version>
		<relativePath>../spring-boot-dependencies</relativePath>
	</parent>
<!--来真正管理 Spring Boot 应用里面的所有依赖版本，也叫作 Spring Boot 的版本仲裁中心-->
```

有了版本仲裁中心，以后我们导入依赖默认是不需要写版本的，没有在 dependencies 里管理的需要自己声明版本号。

##### 2、导入的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

spring-boot-starter-web:

* spring-boot-starter：spring-boot 场景启动器，帮我们导入 web  模块正常运行所依赖的组件；

Spring Boot 将所有的功能场景都抽取出来，做成一个个的 starters（启动器），只需要在项目中引入这些 starters 相关场景，所有依赖都会导入进来。用什么功能就导入什么场景。

#### 2、主程序类、主入口

```java
@SpringBootApplication
public class HelloWorldMainApplication {

    /* Spring 应用启动入口函数 */
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
```

@SpringBootApplication：Spring Boot 应用标注在某个类上说明这个类是 Spring Boot 的主配置类，Spring Boot 就应该运行这个类的 main 方法来启动 Spring Boot 应用。

```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```

**@SpringBootConfiguration** : Spring Boot 的配置类

​	标准在某个类上，表示这是一个 Spring Boot 配置类。
​	@Configuration 配置类上来标注这个注解。
​		配置类也是容器中的一个组价： @Component
​		配置类 -- 代替配置文件

**@EnableAutoConfiguration**：开启自动配置功能
	以前需要配置的东西，Spring Boot 帮我们自动配置；**@EnableAutoConfiguration** 告诉 Spring Boot 开启自动配置功能。

```java
@AutoConfigurationPackage
@Import({EnableAutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
```

* @AutoConfigurationPackage ：自动配置包
  	@Import({Registrar.class}) Spring 的底层注解 `@Import` ，给容器中导入一个组件，导入的组件由 Registrar.class 指定

  Registrar.class：**将主配置类（@SpringBootApplicaiton标注的类）所在包及其子包里面的所有组件扫描到 Spring 容器。**

  所以，将 HelloController 放到 cn 包下，启动程序，发现不能访问 localhost:8080/hello ，因为 @Import 只是完成了主启动类所在包以及子包下的所有组件的扫描和注册，调换位置后不属于 @Import 支持的范围了。

* @Import({EnableAutoConfigurationImportSelector.class})
  给容器中导入组件？

  在 EnableAutoConfigurationImportSelector 的父类 AutoConfigurationImportSelector#selectImports 断点调试

  EnableAutoConfigurationImportSelector：导入哪些组件的选择器
  将所有需要导入的组件以全类名的方式返回，这些组件就会被导入到 Spring 容器中。

  会给容器中导入非常多的自动配置类（xxxAutoConfiguration），给容器中导入这个场景需要的所有组件，并配置好这些组件。
  ![](G:\learn-skills\springbootlearn\docs\pics\07-EnableAutoConfigurationImportSelector选择器选择的自动配置类.png)

  有了自动配置类，免去了手动编写配置注入功能组件等的工作。

  自动配置的过程：

  getCandidateConfigurations() 

  ​	-> SpringFactoriesLoader.loadFactoryNames(
  ​				EnableAutoConfiguration.class , classLoader)

  ​		-> classLoader.getResources("META-INF/spring.factories") 

  在 spring-boot-autoconfigure-1.5.9.RELEASE.jar! 下的 META-IINF 下的 spring.factories 文件：

  ![](G:\learn-skills\springbootlearn\docs\pics\08-spring.facotries文件.png)

  获取到 spirng.factories 下key为 EnableAutoConfiguration 的所有自动配置类名。
  **Spring Boot 启动的时候就从类路径下的 META-INF/spring.factories 中获取 EnableAutoConfiguration 指定的值，将这些值作为自动配置类导入到容器中，自动配置类生效，帮助我们进行自动配置，**而不需要我们手动配置了。

  J2EE 的整体整体解决方案和自动配置都在 spring-boot-autoconfigure-1.5.9.RELEASE.jar 下。

### 六、使用 Spring Initializer 快速创建 Spring Boot 项目

IDE 都支持使用 Spring 的项目创建向导快速创建 Spring Boot 项目

new -> spring initailizer -> 选择需要的模块

向导会联网创建 Spring Boot 项目
默认生成的 Spring Boot 项目：

* 主程序已经生成好了，只需要编写自己的逻辑
* resources 文件夹中目录结构
  * static: 保存所有的静态资源；js css images;
  * templates：保存所有的模板页面（Spring Boot 默认 jar 包使用 嵌入式的 Tomcat，默认不支持 JSP），可以使用模板引擎（freemarker, thymeleaf）；
  * application.properties: Spring Boot 应用的配置文件，可以修改一些默认设置

