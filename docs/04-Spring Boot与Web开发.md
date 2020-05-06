### 一、使用 Spring Boot 的步骤

1、创建 Spring Boot 应用，选中我们需要的模块

2、Spring Boot 已经默认将这些场景配置好了，只需要做少量的配置就可以运行起来

3、编写业务代码

**自动配置原理？**

这个场景  Spring Boot 帮我们配置了什么？能够修改？能够扩占？

xxxAutoConfiguration 帮我们自动配置组件到容器中

xxxProperties：封装配置文件中的内容



### 二、Spring Boot 对静态资源的映射规则

1、ResourceProperties

```java
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
// 可以设置和资源有关的参数，如缓存时间等
public class ResourceProperties {
```

2、通过 webjars 的方式引入

WebMvcAutoConfiguration 下

```java
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			if (!this.resourceProperties.isAddMappings()) {
				logger.debug("Default resource handling disabled");
				return;
			}
			Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
			CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
			if (!registry.hasMappingForPattern("/webjars/**")) {
				customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
						.addResourceLocations("classpath:/META-INF/resources/webjars/")
						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
			}
			String staticPathPattern = this.mvcProperties.getStaticPathPattern();
			if (!registry.hasMappingForPattern(staticPathPattern)) {
				customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
						.addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
						.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
			}
		}
// 配置欢迎页映射
@Bean
		public WelcomePageHandlerMapping welcomePageHandlerMapping(ApplicationContext applicationContext,
				FormattingConversionService mvcConversionService, ResourceUrlProvider mvcResourceUrlProvider) {
			WelcomePageHandlerMapping welcomePageHandlerMapping = new WelcomePageHandlerMapping(
					new TemplateAvailabilityProviders(applicationContext), applicationContext, getWelcomePage(),
					this.mvcProperties.getStaticPathPattern());
			welcomePageHandlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
			return welcomePageHandlerMapping;
		}


// 配置图标
```

所有 /webjars/** ，都去 classpath:/META-INF/resources/webjars/ 找资源

webjars：以 jar 包的方式引入静态资源。

https://www.webjars.org/

jQuery webjars

```xml
<!-- 一如 jQuery webjars 访问是只需要写 webjars 下的资源名称-->
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.3.1</version>
</dependency>
```

![](pics\14-jQuery webjars引入后结构.png)

localhost:8080/webjars/jquery/3.3.1/jquery.js

访问时只需要写 webjars 下的资源名称

3、/** 访问当前项目的任何资源，（静态资源的文件夹）

```
"classpath:/META-INF/resources/",
"classpath:/resources/", 
"classpath:/static/", 
"classpath:/public/"
"/"
```

localhost:8080/abc --> 去上面定义的静态资源文件夹里找 abc

localhost:8080/asserts/img/bootstrap-solid.svg

4、欢迎页：静态资源文件夹下的所有 index.html 页面，被 /** 映射

localhost:8080/index.html

5、所有的 FAVICON("/**/favicon.ico")  都在静态资源文件夹下找

```java
public enum StaticResourceLocation {

	/**
	 * Resources under {@code "/css"}.
	 */
	CSS("/css/**"),

	/**
	 * Resources under {@code "/js"}.
	 */
	JAVA_SCRIPT("/js/**"),

	/**
	 * Resources under {@code "/images"}.
	 */
	IMAGES("/images/**"),

	/**
	 * Resources under {@code "/webjars"}.
	 */
	WEB_JARS("/webjars/**"),

	/**
	 * The {@code "favicon.ico"} resource.
	 */
	FAVICON("/**/favicon.ico");

	private final String[] patterns;

	StaticResourceLocation(String... patterns) {
		this.patterns = patterns;
	}

	public Stream<String> getPatterns() {
		return Arrays.stream(this.patterns);
	}

}
```



### 三、模板引擎

JSP、Velocity、FreeMarker、Thymeleaf

![](pics/15-template-engine.png)

Spring Boot 推荐的 Thymeleaf：语法简单，功能强大。

1、引入 Thymeleaf

```xml
        <!--引入 thymeleaf-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
```

2、Thymeleaf 使用和用法

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";

	/**
	 * Whether to check that the template exists before rendering it.
	 */
	private boolean checkTemplate = true;
    // 只要把 HTML 页面放在 classpath:/templates/ thymeleaf 就可以帮助自动渲染
```

 **只要把 HTML 页面放在 classpath:/templates/ thymeleaf 就可以帮助自动渲染**

使用：

1、导入 thymeleaf 的名称空间

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

2、使用 thymeleaf 语法

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>成功！</h1>
    <!--th:text 将 div 里面的文本内容设置为指定的值-->
    <div th:text="${hello}">这是显示欢迎信息</div>
</body>
</html>
```

### 三、语法规则

1、th:text: 改变当前元素里的文本内容；

th: 任意 html 属性，替换原生属性的值

```html
    <div id="div1" class="divclass" th:text="${hello}"
         th:id="${hello}" th:class="${hello}">这是显示欢迎信息
    </div>
```

![](G:\learn-skills\springbootlearn\docs\pics\16-th语法.png)

2、表达式？

```properties
Simple expressions: {表达式语法}
Variable Expressions: ${...} 获取变量值 OGNL
	1、获取对象的属性，调用方法
	2、使用内置的对象
        #ctx : the context object.
        #vars: the context variables.
        #locale : the context locale.
        #request : (only in Web Contexts) the HttpServletRequest object.
        #response : (only in Web Contexts) the HttpServletResponse object.
        #session : (only in Web Contexts) the HttpSession object.
        #servletContext : (only in Web Contexts) the ServletContext object.
	3、内置的工具对象
        #execInfo : information about the template being processed.
        #messages : methods for obtaining externalized messages inside variables expressions, in the same way as they would be obtained using #{…} syntax.
        #uris : methods for escaping parts of URLs/URIs
        Page 20 of 106
        #conversions : methods for executing the configured conversion service (if any).
        #dates : methods for java.util.Date objects: formatting, component extraction, etc.
        #calendars : analogous to #dates , but for java.util.Calendar objects.
        #numbers : methods for formatting numeric objects.
        #strings : methods for String objects: contains, startsWith, prepending/appending, etc.
        #objects : methods for objects in general.
        #bools : methods for boolean evaluation.
        #arrays : methods for arrays.
        #lists : methods for lists.
        #sets : methods for sets.
        #maps : methods for maps.
        #aggregates : methods for creating aggregates on arrays or collections.
        #ids : methods for dealing with id attributes that might be repeated (for example, as a result of an iteration).

Selection Variable Expressions: *{...} 选择表达式 和 ${} 功能上一样
	补充：配合 th:object 使用
	<div th:object="${session.user}">
    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
    </div>
	<div>
    <p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p>
    </div>
Message Expressions: #{...} 获取国际化内容
Link URL Expressions: @{...} 定义 URL 链接
	@{/order/process(execId=${execId},execType='FAST')}
Fragment Expressions: ~{...} 片段引用表达式
	<div th:insert="~{commons :: main}">...</div
Literals（字面量）
    Text literals: 'one text' , 'Another one!' ,…
    Number literals: 0 , 34 , 3.0 , 12.3 ,…
    Boolean literals: true , false
    Null literal: null
    Literal tokens: one , sometext , main ,…
Text operations:（文本操作）
    String concatenation: +
    Literal substitutions: |The name is ${name}|
Arithmetic operations:（数学运算）
    Binary operators: + , - , * , / , %
    Minus sign (unary operator): -
Boolean operations:（布尔运算）
    Binary operators: and , or
    Boolean negation (unary operator): ! , not
Comparisons and equality:（比较运算）
    Comparators: > , < , >= , <= ( gt , lt , ge , le )
    Equality operators: == , != ( eq , ne )
Conditional operators:（条件运算）三目运算
    If-then: (if) ? (then) 
    If-then-else: (if) ? (then) : (else)
	Default: (value) ?: (defaultvalue)
Special tokens:
    No-Operation: _
```

用法案例

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>成功！</h1>
    <!--th:text 将 div 里面的文本内容设置为指定的值-->
    <div id="div1" class="divclass" th:text="${hello}"
         th:id="${hello}" th:class="${hello}">这是显示欢迎信息
    </div>
<hr/>
    <div th:text="${hello}"></div>
    <div th:utext="${hello}"></div>
    <hr/>
<!--th:each 每次遍历都会生成当前这个标签 3个h4-->
    <h4 th:text="${user}" th:each="user:${users}"></h4>
    <hr/>
    <span th:each="user:${users}">[[${user}]]</span>
    <hr/>
</body>
</html>
```



### 四、SpringMVC 自动配置

https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications

#### 1、Spring MVC 自动配置

Spring Boot 自动配置好了 Spring MVC

以下是 Spring Boot 对 SpringMVC 的默认支持

The auto-configuration adds the following features on top of Spring’s defaults:

- Inclusion of `ContentNegotiatingViewResolver` and `BeanNameViewResolver` beans.

  * 自动配置了 ViewResolver（视图解析器，根据方法的返回值得到对象（View），视图对象决定如何渲染（转发？重定向））可在  WebMvcAutoConfiguration 中看到对应的源码

  * `ContentNegotiatingViewResolver`  组合所有的视图解析器；

  * 如何定制？我们可以自己给容器中添加一个视图解析器，自动将其组合进来
    @Bean 注入自定义的 ViewResolver
    DispathcerServlet#doDispatch 方法出断点查看

    ![](pics\17-自定义ViewResolver在DispatchServlet中端点查看.png)

- Support for serving static resources, including support for WebJars (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-static-content))).  静态资源文件夹路径和 webjars

- Automatic registration of `Converter`, `GenericConverter`, and `Formatter` beans. 自动注册了 `Converter`, `GenericConverter`, and `Formatter` 

  * 转换器 Converter：public String hello(User user): 类型转换使用 Converter

  * Formatter格式化器：2017/2/2 == Date

    ```java
    @Override
    public MessageCodesResolver getMessageCodesResolver() {
    			if (this.mvcProperties.getMessageCodesResolverFormat() != null) {
    				DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
    				resolver.setMessageCodeFormatter(this.mvcProperties.getMessageCodesResolverFormat());
    				return resolver;
    			}
    			return null;
    		}
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
    			ApplicationConversionService.addBeans(registry, this.beanFactory);
    		}
    
    
    @ConfigurationProperties(prefix = "spring.mvc") // 在文件中配置日期格式化的规则
    public class WebMvcProperties {
    
    	/**
    	 * Formatting strategy for message codes. For instance, `PREFIX_ERROR_CODE`.
    	 */
    	private DefaultMessageCodesResolver.Format messageCodesResolverFormat;
    ```

    **自定义的格式化转换器，我们只需要放在容器中即可**

- Support for `HttpMessageConverters` (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-message-converters)).

  * `HttpMessageConverters` ：SpringMVC 用来转换HTTP请求和相应的；User --> json

    ```java
    private static class HalMessageConverterSupportedMediaTypesCustomizer implements BeanFactoryAware {
    
    		private volatile BeanFactory beanFactory;
    
    		@PostConstruct
    		void configureHttpMessageConverters() {
    			if (this.beanFactory instanceof ListableBeanFactory) {
    				configureHttpMessageConverters(((ListableBeanFactory) this.beanFactory)
    						.getBeansOfType(RequestMappingHandlerAdapter.class).values());
    			}
    		}
    ```

  * HttpMessageConverters 从容器中确定，获取所有的 HttpMessageConverters ，自定义添加 HttpMessageConverters，只需要将自己的组件注册到容器中（@Bean @Component）

- Automatic registration of `MessageCodesResolver` (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-spring-message-codes)).

  * 定义错误代码生成规则

- Static `index.html` support. 静态首页访问

- Custom `Favicon` support (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-favicon)).

- Automatic use of a `ConfigurableWebBindingInitializer` bean (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-web-binding-initializer)).

  * 我们可以配置一个 `ConfigurableWebBindingInitializer`  替换默认的（添加到容器）
  * `ConfigurableWebBindingInitializer`  ：初始化 WebDataBinder。请求数据===JavaBean

org.springframework.boot.autoconfigure.web：**web 的所有自动配置场景**

If you want to keep those Spring Boot MVC customizations and make more [MVC customizations](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/web.html#mvc) (interceptors, formatters, view controllers, and other features), you can add your own `@Configuration` class of type `WebMvcConfigurer` but **without** `@EnableWebMvc`.

If you want to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`, or `ExceptionHandlerExceptionResolver`, and still keep the Spring Boot MVC customizations, you can declare a bean of type `WebMvcRegistrations` and use it to provide custom instances of those components.

If you want to take complete control of Spring MVC, you can add your own `@Configuration` annotated with `@EnableWebMvc`, or alternatively add your own `@Configuration`-annotated `DelegatingWebMvcConfiguration` as described in the Javadoc of `@EnableWebMvc`.

#### 2、扩展 SpringMVC

```xml
  <mvc:view-controller path="/hello"
                         view-name="success"></mvc:view-controller>
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/hello"/>
            <bean></bean>
        </mvc:interceptor>
    </mvc:interceptors>
```

编写一个配置类，是 `WebMvcConfigurer`  类型，不能标注 `@EnableWebMvc`.

既保留了所有的自动配置，也能用我们的扩展的配置

```java
// 使用  WebMvcConfigurer 扩展 SpringMVC 的功能
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // super.addViewControllers
        // 浏览器发送 /hust 来到 success 页面
        registry.addViewController("/hust").setViewName("success");
    }
}
```

原理：

1、WebMvcAutoConfiguration 是 SpringMVC 的自动配置类

2、在做其他自动配置时会导入：@Import(EnableWebMvcConfiguration.class)

```java
public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration .. {

@Configuration(proxyBeanMethods = false)
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {

	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

	// 从容器中获取所有的 WebMvcConfigure
	@Autowired(required = false)
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addWebMvcConfigurers(configurers);
            // 一个实现是 将所有的 WebMvcConfigurer 相关配置都来一起调用
		   	//@Override
       		//protected void addViewControllers(ViewControllerRegistry registry) {
            //	this.configurers.addViewControllers(registry);
			//}

		}
	}

```

3、容器中所有的 WebMvcConfigurer 都会一起起作用

4、我们的配置类也会被调用

效果：SpringMVC 的自动配置和我们的扩展配置都会起作用

#### 3、全面接管SpringMVC

Spring Boot 对 SpringMVC 的自动配置我们不需要了，所有都是我们自己配置，在配置中添加 `@EnableWebMvc`. 所有的 SpringMVC 的自动配置都失效。

```java
@Configuration
@EnableWebMvc // 全面接管 SpringMVC 不使用 SpringBoot 提供的自动配置
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // super.addViewControllers
        // 浏览器发送 /hust 来到 success 页面
        registry.addViewController("/hust").setViewName("success");
    }
}
```

原理：

为什么加了 @EnableWebMvc 自动配置失效了？

1、EnableWebMvc  的核心

```java
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
```

2、DelegatingWebMvcConfiguration 的类结构

```java
@Configuration(proxyBeanMethods = false)
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
```

3、WebMvcAutoConfiguration 自动配置

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
// 在容器中没有 WebMvcConfigurationSupport 组件时，该配置类生效
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {
```

4、@EnableWebMvc  向容器中导入了  WebMvcConfigurationSupport 组件，所以自动配置类不生效了。

5、而 WebMvcConfigurationSupport 只包含了SpringMVC基本的功能



### 五、如何修改 Spring Boot 默认配置

模式：

1、Spring Boot 在用自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean，@Component），如果有就用用户配置的，如果没有，才自动配置；如果有些组件可以有多个（如 ViewResolver）将用户配置的和自己默认的组合起来。

2、在 Spring Boot 中会有非常多的 xxxConfigure 帮助我们进行扩展配置

3、在 Spring Boot 中会有非常多的 xxxCustomizer 帮助我们进行定制配置

### 六、RestfulCRUD

#### 1、默认访问首页

添加 dao entity 包，静态资源到 classpath:template/

添加方法

```java
    @RequestMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }
```

添加 viewcontroller

```java
// 所有的 WebMvcConfigure 都生效
@Bean
public WebMvcConfigurer webMvcConfigurer() {
    WebMvcConfigurer mvcConfigurer = new WebMvcConfigurer() {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
            registry.addViewController("/index.html").setViewName("index");
        }
    };
    return mvcConfigurer;
}
```

报错：[THYMELEAF][http-nio-8080-exec-1] Exception processing template "index":

访问 localhost:8080 报错解决，在 index.html 中加入

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

使用 webjars 替换所有公共的 资源，这样在添加 tontext-path 等时，SpringBoot 会帮我们自动拼接资源路径

```java
		<!-- Bootstrap core CSS -->
		<link href="asserts/css/bootstrap.min.css"
 th:href="@{/webjars/bootstrap/4.1.0/css/bootstrap.css}"
			  rel="stylesheet">
		<!-- Custom styles for this template -->
		<link href="asserts/css/signin.css" th:href="@{/asserts/css/signin.css}" rel="stylesheet">
	</head>
			<img class="mb-4" src="asserts/img/bootstrap-solid.svg" th:src="@{/asserts/img/bootstrap-solid.svg}"
                 alt=""
                 width="72" height="72">
```

#### 2、国际化

1. 编写国际化配置文件
2. 使用 ResourceBundleMessageSource 管理国际化资源文件
3. 在页面使用 fmt:message 取出国际化内容

步骤：

1、编写国际化配置文件，抽取需要国际化内容

![](pics\18-国际化配置文件.png)

2、SpringBoot 自动配置好了管理国际化资源文件的组件 MessageSourceAutoConfiguration

```java
@ConfigurationProperties(prefix = "spring.messages")
public class MessageSourceAutoConfiguration {
    
    	/**
	 * Comma-separated list of basenames, each following the ResourceBundle convention.
	 * Essentially a fully-qualified classpath location. If it doesn't contain a package
	 * qualifier (such as "org.mypackage"), it will be resolved from the classpath root.
	 */
    // 我们的文件可以直接放在类路径下 messages.properties
    private String basename = "messages";
    
    @Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		if (StringUtils.hasText(this.basename)) {
            //设置国际化国际化资源文件的基础名（去掉语言和国家代码）
			messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(
					StringUtils.trimAllWhitespace(this.basename)));
		}
		if (this.encoding != null) {
			messageSource.setDefaultEncoding(this.encoding.name());
		}
		messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale);
		messageSource.setCacheSeconds(this.cacheSeconds);
		messageSource.setAlwaysUseMessageFormat(this.alwaysUseMessageFormat);
		return messageSource;
	}
```

配置路径

```properties
# 配置国际化资源文件位置
spring.messages.basename=i18n.login
```



3、去页面获取国际化值

`#{}` 获取 messages

```html
	<body class="text-center">
		<form class="form-signin" action="dashboard.html">
			<img class="mb-4" src="asserts/img/bootstrap-solid.svg" th:src="@{asserts/img/bootstrap-solid.svg}"
                 alt=""
                 width="72" height="72">
			<h1 class="h3 mb-3 font-weight-normal" th:text="#{login.tip}">Please sign
                in</h1>
			<label class="sr-only" th:text="#{login.username}">Username</label>
			<input type="text" class="form-control" th:placeholder="#{login.username}"
                   placeholder="Username" required="" autofocus="">
			<label class="sr-only" th:text="#{login.password}">Password</label>
			<input type="password" class="form-control"
                   th:placeholder="#{login.password}"
                   placeholder="Password" required="">
			<div class="checkbox mb-3">
				<label>
          <input type="checkbox" value="remember-me" >
                    [[#{login.remember}]]
        </label>
			</div>
			<button class="btn btn-lg btn-primary btn-block"
                    th:text="#{login.btn}"
                    type="submit">Sign in</button>
			<p class="mt-5 mb-3 text-muted">© 2017-2018</p>
			<a class="btn btn-sm">中文</a>
			<a class="btn btn-sm">English</a>
		</form>

	</body>
```

效果：根据浏览器语言的信息切换国际化

原理：国际化 Locale（区域信息对象）LocalResolver(获取区域对象)

```java
		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnProperty(prefix = "spring.mvc", name = "locale")
		public LocaleResolver localeResolver() {
			if (this.mvcProperties.getLocaleResolver() == WebMvcProperties.LocaleResolver.FIXED) {
				return new FixedLocaleResolver(this.mvcProperties.getLocale());
			}
			AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
			localeResolver.setDefaultLocale(this.mvcProperties.getLocale());
			return localeResolver;
		}
// 默认的区域信息解析是根据请求头带来的信息进行国家化
```

实现点击按钮实现国际化切换

1、html 标签带上参数

```html
			<a class="btn btn-sm" th:href="@{/index.html(l='zh_CN')}">中文</a>
			<a class="btn btn-sm" th:href="@{/index.html(l='en_US')}">English</a>
```

2、自定义 LocaleResolver 实现点击链接切换的逻辑

```java
public class MyLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String l = request.getParameter("l");
        // 默认获取操作系统默认的语言
        Locale locale = Locale.getDefault();
        if (!StringUtils.isEmpty(l)) {
            String[] split = l.split("_");
            locale = new Locale(split[0], split[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
```

加入容器中

```java
    @Bean
    public LocaleResolver localeResolver() {
        return new MyLocaleResolver();
    }
```

#### 3、登录

模板引擎页面修改之后，实时生效

1，禁用模板引擎的缓存

```properties
spring.thymeleaf.cache=false
```

2，页面修改完成以后，ctrl + shift + F9 重新编译修改后的页面

登录错误消息的显示

```html
<!--判断显示错误信息-->
    <p style="color: crimson" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}"></p>
```

防止表单重复提交，添加重定向和视图解析器

```java
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        WebMvcConfigurer mvcConfigurer = new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                registry.addViewController("/main.html").setViewName("dashboard");
            }
        };
        return mvcConfigurer;
    }
```

Controller 里重定向

```java
@Controller
public class LoginController {

//    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    // 如果请求参数上没有指定的参数，报错 @RequestParam("username")
    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String, Object> map) {
        if (!StringUtils.isEmpty(username) && "123456".equals(password)) {
            // 登录成功 防止表单重复提交，重定向
//            return "dashboard.html";
            return "redirect:/main.html";
        } else {
            // 登录失败
            map.put("msg", "用户名或密码错误");
            return "login";
        }
    }
```

3，存在的问题，访问 http://localhost:8080/main.html 可以直接到达后台页面，不安全，使用拦截器进行登录检查

#### 4、拦截器登录检查

WebMvcConfigure 中添加拦截器

```java
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
        @Override
        public void addInterceptors(InterceptorRegistry registry) {

            // 静态资源  css js Spring Boot 做好了静态资源映射了，我们不用处理
            registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns(
                    "/index.html", "/", "/user/login", "/asserts/**", "/webjars/**");
        }
    };
    return mvcConfigurer;
```

自定义拦截器

```java
public class LoginHandlerInterceptor implements HandlerInterceptor {

    // 目标防止执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginUser");
        if (user == null) {
            // 未登录 拦截 返回登录页面
            // 转发
            request.setAttribute("msg", "没有权限，先登录");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        } else {
            // 已登录

            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
```

#### 5、CRUD-员工列表

实验要求：

1、Restful:CURD 满足 Restful 风格

URI： /资源名称/资源标识 HTTP 请求方式区分对资源CRUD操作

|      | 普通CRUD（uri区分操作） | RestfulCRUD     |
| ---- | ----------------------- | --------------- |
| 查询 | getEmp                  | emp--GET        |
| 添加 | addEmp?xxx              | emp--POST       |
| 修改 | updateEmp?id=xxx&xxx=xx | emp{id}--PUT    |
| 删除 | deleteEmp?id=1          | emp{id}--DELETE |

2、实验的请求架构

|                                      | 请求 URI | 请求方式 |
| ------------------------------------ | -------- | -------- |
| 查询所有员工                         | emps     | GET      |
| 查询某个员工                         | emp/{id} | GET      |
| 来到添加页面                         | emp      | GET      |
| 添加员工                             | emp      | POST     |
| 来到修改页面（查询员工进行信息回显） | emp/{id} | GET      |
| 修改员工                             | emp      | PUT      |
| 删除员工                             | emp/{id} | DELETE   |

3、员工列表

thymeleaf 公共页面元素抽取

1. 抽取公共片段

   ```xml
   <div th:fragment="copy">
   &copy; 2011 The Good Thymes Virtual Grocery
   </div>
   ```

2. 引入公共片段

   ```xml
   <div th:insert="~{footer :: copy}"></div>
   ~{templatename::selector} 模板名::选择器
   ~{templatename::fragmentname} 模板名::片段名
   ```

   三种引入公共片段的 th 属性：

   * th:insert 将公共片段整个插入到声明引入的元素中
   * th:replace 将声明引入的元素替换为片段

   * th:include 被引入的片段的内容包含进标签中

   ```html
   <footer th:fragment="copy">
   &copy; 2011 The Good Thymes Virtual Grocery
   </footer>
   
   引入方式
   <div th:insert="footer :: copy"></div>
   <div th:replace="footer :: copy"></div>
   <div th:include="footer :: copy"></div>
   
   效果
   <div>
   <footer>
   &copy; 2011 The Good Thymes Virtual Grocery
   </footer>
   </div>
   <footer>
   &copy; 2011 The Good Thymes Virtual Grocery
   </footer>
   <div>
   &copy; 2011 The Good Thymes Virtual Grocery
   </div>
   ```

   如果使用 th:insert 等属性进行引入，可以不用写 ~{}；

   行内写法需要写：[[~{}]] [(~{})]

   练习：sidebar 的抽取和引入

   

3、高亮控制，使用判断变量控制

```html
<!--bar.html-->
<li class="nav-item">
                <a class="nav-link active" th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}"
                   href="http://getbootstrap.com/docs/4.0/examples/dashboard/#"
                   th:href="@{/main.html}">
                    </svg>
                    Dashboard <span class="sr-only">(current)</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" th:class="${activeUri=='emps'?'nav-link active':'nav-link'}"
                   href="http://getbootstrap.com/docs/4.0/examples/dashboard/#"
                   th:href="@{/emps}">
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    员工管理
                </a>
            </li>

```

dashboard 下 传入变量 activeUri=main.html

```html
<div th:replace="~{commons/bar::#sidebar(activeUri='main.html')}"></div>
```

list下：传入变量 activeUri=emps

```html
<div th:replace="~{commons/bar::#sidebar(activeUri='emps')}"></div>
```



thymeleaf 日期工具

```html
/*
* Format date with the specified pattern
* Also works with arrays, lists or sets
*/
${#dates.format(date, 'dd/MMM/yyyy HH:mm')}
${#dates.arrayFormat(datesArray, 'dd/MMM/yyyy HH:mm')}
${#dates.listFormat(datesList, 'dd/MMM/yyyy HH:mm')}
${#dates.setFormat(datesSet, 'dd/MMM/yyyy HH:mm')}
```

4、添加员工

```html
					<form>
                        <div class="form-group">
                            <label>LastName</label>
                            <input type="text" class="form-control" placeholder="zhangsan">
                        </div>
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" class="form-control" placeholder="zhangsan@atguigu.com">
                        </div>
                        <div class="form-group">
                            <label>Gender</label><br/>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" value="1">
                                <label class="form-check-label">男</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="gender" value="0">
                                <label class="form-check-label">女</label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>department</label>
                            <select class="form-control">
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>4</option>
                                <option>5</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Birth</label>
                            <input type="text" class="form-control" placeholder="zhangsan">
                        </div>
                        <button type="submit" class="btn btn-primary">添加</button>
					</form>
```

转发和重定向

ThymeleafViewResolver#createView 方法详见

Controller

```java
    // 员工添加功能
    // SpringMVC 自动将请求参数和入参对象的属性一一绑定
    // 要求请求参数的名字和 JavaBean 入参的对象里面的属性名是一样的
    @PostMapping("/emp")
    public String addEmp(Employee employee) {
        // 来到员工列表页面
        System.out.println("保存的员工信息：" + employee);

        // 保存员工
        employeeDao.save(employee);
        // redirect 重定向到一个地址 / 代表当前项目路径
        // forward 转发
        return "redirect:/emps";
    }
```

员工添加最容易的问题：提交的格式不对：生日：日期

2019-12-12 2019/12/12 

日期的格式化：SpringMVC 将页面提交的数据转换为指定的类型

2019-12-12 - Date，类型转换，格式化

默认日期是按照 / 的方式

可以通过 spring.mvc.date-fomat 指定日志格式

```properties
# 日期格式
spring.mvc.date-format=yyyy-MM-dd HH:mm:ss
```

5、员工修改(使用了和添加同一个页面完成 通过判断决定哪个生效)

点击按钮，进入修改页面

需要区分是员工修改还是添加，添加页面 emp 是空的，修改也买 emp 不为空

```html
<input name="lastName" type="text" class="form-control" placeholder="zhangsan"
                                   th:value="${emp!=null}?${emp.lastName}">
```

Controller

```java
    // 员工修改 需要提交员工 id
    @PutMapping("/emp")
    public String updateEmp(Employee employee) {
        System.out.println("员工数据：" + employee);
		// 将修改保存到数据库
        employeeDao.save(employee);

        return "redirect:/emps";
    }
```

html 页面提交员工 id 信息

```html
<main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
<!--发送 PUT 请求进行员工修改-->
<!--
1、SpringMVC 中配置 HiddenHttpMethodFilter（SpringBoot自动配置的）
2、页面创建一个 POST 表单
3、创建一个 input 项，name=_method,值就是我们指定的方式
                        -->
<form th:action="@{/emp}" method="post">
      <input type="hidden" name="_method" value="put" th:if="${emp!=null}"/>
      <!--员工id以隐藏的方式传递-->
      <input type="hidden" name="id" th:if="${emp!=null}" th:value="${emp.id}"/>
```

6、员工删除

list 中添加 form 表单

```html
<form th:action="@{/emp/}+${emp.id}" method="post">
	<input type="hidden" name="_method" value="delete"/>
	<button type="submit" class="btn btn-sm btn-danger">删除</button>	
</form>
```

spirng boot 2 默认关闭了 HiddenHttpMethodFilter 需要开启

```properties
spring.mvc.hiddenmethod.filter.enabled=true
```

修改 list 中的 form 出，使用 js 方式提交表单，给 btn 绑定表单 提交

7、错误处理机制

1. SpringBoot 错误机制

   默认效果：

   1. 返回一个错误的页面
      ![](pics\19-SpringBoot默认错误页面.png)
      浏览器发送请求的请求头：
      ![](pics\21-浏览器发送请求的请求头.png)

   2. 其他客户端访问，默认相应一个 JSON 数据

      ![](pics\20-Postman请求返回的错误信息.png)

      其他客户端的请求头：
      ![](pics\22-postman发送请求的请求头.png)

   3. 原理：
      可以参照 ErrorMvcAutoConfiguration 错误处理的自动配置类
      给容器中添加了以下组件：
      1、DefaultErrorAttributes

      帮我们在页面共享信息

      ```java
      	@Override
      	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
      		Map<String, Object> errorAttributes = new LinkedHashMap<>();
      		errorAttributes.put("timestamp", new Date());
      		addStatus(errorAttributes, webRequest);
      		addErrorDetails(errorAttributes, webRequest, includeStackTrace);
      		addPath(errorAttributes, webRequest);
      		return errorAttributes;
      	}
      ```

      

      2、BasicErrorController

      ```java
      @Controller
      @RequestMapping("${server.error.path:${error.path:/error}}")
      public class BasicErrorController extends AbstractErrorController {
          
          // 产生 html 类型的数据 浏览器的请求这个方法处理
      	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
      	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
      		HttpStatus status = getStatus(request);
      		Map<String, Object> model = Collections
      				.unmodifiableMap(getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
      		response.setStatus(status.value());
              // 去哪个页面作为错误页面，包含页面地址和页面数据
      		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
              // 如果没有解析到，根据前面的 model 数据构建 ModelAndView
      		return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
      	}
      
          // 产生 json 类型数据 其他客户端的请求这里处理
      	@RequestMapping
      	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
      		HttpStatus status = getStatus(request);
      		if (status == HttpStatus.NO_CONTENT) {
      			return new ResponseEntity<>(status);
      		}
      		Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
      		return new ResponseEntity<>(body, status);
      	}
      ```

      处理默认的 /error 请求

      3、ErrorPageCustomizer

      ```java
      @Override
      public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
      			ErrorPage errorPage = new ErrorPage(     this.dispatcherServletPath.getRelativePath(this.properties.getError().getPath()));
      	errorPageRegistry.addErrorPages(errorPage);
      }
      	@Value("${error.path:/error}")
      	private String path = "/error";
      ```

      系统出现错误以后来到 error 请求进行处理：(web.xml注册的页面规则)，来到 /error 请求

      4、DefaultErrorViewResolver

      ```java
      	@Override
      	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
      		ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);
      		if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
      			modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
      		}
      		return modelAndView;
      	}
      
      	private ModelAndView resolve(String viewName, Map<String, Object> model) {
              // 默认 Spring Boot 可以找到一个页面 error/404
      		String errorViewName = "error/" + viewName;
              // 如果模板引擎可以解析，使用模板引擎
      		TemplateAvailabilityProvider provider = this.templateAvailabilityProviders.getProvider(errorViewName,
      				this.applicationContext);
      		if (provider != null) {
                  // 模板引擎可以，返回解析的视图
      			return new ModelAndView(errorViewName, model);
      		}
              // 在静态文件夹下，找 errorViewName 对应的页面 error/404.html
      		return resolveResource(errorViewName, model);
      	}
      private ModelAndView resolveResource(String viewName, Map<String, Object> model) {
      		for (String location : this.resourceProperties.getStaticLocations()) {
      			try {
      				Resource resource = this.applicationContext.getResource(location);
      				resource = resource.createRelative(viewName + ".html");
      				if (resource.exists()) {
      					return new ModelAndView(new HtmlResourceView(resource), model);
      				}
      			}
      			catch (Exception ex) {
      			}
      		}
      		return null;
      	}
      ```

      

      

      步骤：一旦系统出现 4XX 或者 5XX 之类的错误，ErrorPageCustomizer 生效，定制响应的规则，就会来到 /error 请求，就会被 BasicErrorController 处理；
      响应页面解析代码：哪个页面由 DefaultErrorViewResolver 解析得到

      ```java
      	protected ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
      			Map<String, Object> model) {
              // 所有的 ErrorViewResolver 得到 ModelAndView
      		for (ErrorViewResolver resolver : this.errorViewResolvers) {
      			ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);
      			if (modelAndView != null) {
      				return modelAndView;
      			}
      		}
      		return null;
      	}
      ```

      

   4.  如何定制错误响应

      1. 如何定制错误页面
         1、有模板引擎的情况下：error/状态码，将错误页面命名为 错误状态码.html 放在模板引擎文件夹下的 error 文件夹下，发生此状态码的错误，就会来到对应的页面。也可以使用 4xx 5xx 作为错误页面的而文件名来匹配这种类型的所有错误，精确优先（优先寻找y精确的状态码.html）.

         页面的错误信息：

         * timestamp：时间戳
         * status：状态码
         * error：错误提示
         * exception：异常对象
         * message：异常消息
         * errors：JSR303 数据校验错误
         * path：路径
         * trace

         在 模板引擎方式取出数据显示到 html 上

         ```html
         <h1>status:[[${status}]]</h1>
         <h1>timestamp:[[${timestamp}]]</h1>
         <h1>error:[[${error}]]</h1>
         ```

         2、没有模板引擎(模板引擎找不到这个错误页面)，静态资源文件夹下找
         3、以上都没有找到错误页面，默认来到 Spring Boot 的错误提示页面

      2. 定义定制错误的 JSON 数据
         定义用户不存在异常

         ```java
         public class UserNotExistException extends RuntimeException {
         
             public UserNotExistException() {
                 super("用户不存在");
             }
         }
         
         ```

         Controller

         ```java
             @RequestMapping("/hello")
             @ResponseBody
             public String hello(@RequestParam("user") String user) {
         
                 if ("aaa".equals(user)) {
                     throw new UserNotExistException();
                 }
         
                 return "Hello!";
             }
         ```

         1、自定义异常处理器返回定制 json 数据

         ```java
         @ControllerAdvice
         public class MyExceptionHandler {
         	// 浏览器客户端都是 json
             @ResponseBody
             @ExceptionHandler(UserNotExistException.class) // 拦截的异常
             public Map<String, Object> handlerException(Exception e) {
                 Map<String, Object> map = new HashMap<>();
                 map.put("code", "user not exist");
                 map.put("msg", e.getMessage());
                 return map;
             }
         }
         // 没有自适应效果
         ```

         2、转发到 /error 进行自适应处理

         ```java
             @ExceptionHandler({UserNotExistException.class}) // 拦截的异常
             public String handlerException(Exception e, HttpServletRequest request) {
                 Map<String, Object> map = new HashMap<>();
                 // 传入自己的错误状态码，4xx 5xx。不传则是 200
                 // 否则不会进入定制错误页面的解析流程
                 request.setAttribute("javax.servlet.error.status_code", 500);
         
                 map.put("code", "user not exist");
                 map.put("msg", e.getMessage());
                 // 转发到 /error
                 return "forward:/error";
             }
         ```

         3、将我们的定制数据携带出去
         出现错误以后，会来到 /error 请求，会被 BasicErrorController 处理，相应出去可以获取的数据是由 getErrorAttributes 方法得到（是AbstractErrorController （ErrorController）的方法 ）；
         方法一：编写一个 ErrorController 的实现类（或者继承编写 AbstractErrorController ），放在容器中

         方法二：页面上能用的数据，或者 json 返回的数据都是通过 this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace); 得到的，相当于容器中 DefaultErrorAttributes  默认进行处理的

         自定义 ErrorAttributes

         ```java
         /**
          * 给容器中加入自定义的错误属性
          *
          */
         @Component
         public class MyErrorAttributes extends DefaultErrorAttributes {
         
             // 返回的 map 是页面和 json 能获取的所有字段
             @Override
             public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                 Map<String, Object> map = super.getErrorAttributes(webRequest, includeStackTrace);
         
                 map.put("company", "hust");
         
                 // 异常处理器携带的数据
                 Map<String, Object> ext = (Map<String, Object>)webRequest.getAttribute("ext", 0);
         
                 map.put("ext", ext);
                 return map;
             }
         }
         ```

         最终的效果，响应式自适应的，可以通过自定义 ErrorAttributes 改变需要返回的内容。



8、配置嵌入式的 Servlet 容器

Spring Boot 默认使用的是 嵌入式的 Servlet 容器（Tomcat）

![](pics\23-Spring Boot 默认的Servlet容器.png)

问题？

1、如何定制和修改 Servlet 容器的相关配置

1）修改和 server 有关的配置(ServerProperties)

```properties
server.port=9090

// 通用的 Servlet 容器设置
server.xxx
// tomcat 相关的设置
server.tomcat.accept-count=
```

2）编写一个实现了  **WebServerFactoryCustomizer**<**ConfigurableServletWebServerFactory**> 的类 修改 Servlet 容器的配置

https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-embedded-container

```java
public class CustomizationBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    // 定制嵌入式 Servlet 容器相关的规则
    @Override
    public void customize(ConfigurableServletWebServerFactory server) {
        server.setPort(9000);
    }

}
```



2、如何注册三大组件

由于 Spring Boot 默认是以 jar 包的方式启动嵌入式的 Servlet 容器来启动 Spring Boot 的 web 应用，没有 web.xml 文件，通过下面的方式注册三大组件：

ServletRegistrationBean

```java
@Configuration
public class MyServerConfig {

    // 注册三大组件
    @Bean
    public ServletRegistrationBean myServlet() {
        ServletRegistrationBean<Servlet> registrationBean =
                new ServletRegistrationBean<>(new MyServlet(), "/myservlet");

        return registrationBean;

    }

    // 配置嵌入式 Servlet 容器
    @Bean
    public CustomizationBean customizationBean() {
        return new CustomizationBean();
    }

}

    @Bean
    public ServletRegistrationBean myServlet() {
        ServletRegistrationBean<Servlet> registrationBean =
                new ServletRegistrationBean<>(new MyServlet(), "/myservlet");

        return registrationBean;

    }
```

FilterRegistrationBean

```java
public class MyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter Process.....");
        // 放行
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
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
```



ServletListenerRegistrationBean

```java
public class MyListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("contextInitialized::web应用启动");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("contextDestroyed::当前项目销毁");
    }
}

    // 注册 Listener
    @Bean
    public ServletListenerRegistrationBean myListener() {
        ServletListenerRegistrationBean registrationBean = new ServletListenerRegistrationBean(new MyListener());

        return registrationBean;
    }
```

Spring Boot 帮我们自动配置 Spring MVC 的时候，自动的注册 Spring MVC 的前端控制器：DispacherServlet

```java
@Configuration(proxyBeanMethods = false)
	@Conditional(DispatcherServletRegistrationCondition.class)
	@ConditionalOnClass(ServletRegistration.class)
	@EnableConfigurationProperties(WebMvcProperties.class)
	@Import(DispatcherServletConfiguration.class)
	protected static class DispatcherServletRegistrationConfiguration {

		@Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
		@ConditionalOnBean(value = DispatcherServlet.class, name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
		public DispatcherServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet,
				WebMvcProperties webMvcProperties, ObjectProvider<MultipartConfigElement> multipartConfig) {
            
            // 默认拦截的是 / 所有请求，包括静态资源，但是不拦截 JSP 请求，/* 会拦截 JSP
            // 可以通过 spring.mvc.servlet.path 修改SpringMVC前端控制器拦截的请求路径
			DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(dispatcherServlet,
					webMvcProperties.getServlet().getPath());
			registration.setName(DEFAULT_DISPATCHER_SERVLET_BEAN_NAME); 
			registration.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
			multipartConfig.ifAvailable(registration::setMultipartConfig);
			return registration;
		}

	}
```

3、使用其他的 Servlet 容器

Jetty（长连接）

Undertow（不支持 JSP 非阻塞）
![](pics\24-可配置的Servlet容器.png)

默认支持：Tomcat Jetty Undertow
切换步骤：

1）取消 tomcat staters

2）引入 Jetty 坐标

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                    <groupId>org.springframework.boot</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <!--引入 jetty 作为嵌入式 Servlet 容器-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jetty</artifactId>
        </dependency>

```

切换为 Undertow

```xml
<!--spring boot JUnit 单元测试卡在 revolving junit-platform-launcher-->
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-launcher</artifactId>
            <scope>test</scope>
        </dependency>
```

引入 starter-web 默认引入的是 Tomcat 作为内置 Servlet 容器

```xml
 		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

4、嵌入式 Servlet 自动配置原理
ServletWebServerFactoryAutoConfiguration

```java
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(ServletRequest.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ServerProperties.class)
@Import({ ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class,
		ServletWebServerFactoryConfiguration.EmbeddedTomcat.class,
		ServletWebServerFactoryConfiguration.EmbeddedJetty.class,
		ServletWebServerFactoryConfiguration.EmbeddedUndertow.class })
//BeanPostProcessorsRegistrar 导入了 WebServerFactoryCustomizerBeanPostProcessor
// 在 Bean 初始化前后（刚创建完对象，还没属性赋值）执行，执行一些初始化工作
public class ServletWebServerFactoryAutoConfiguration {
    
    
	@Bean
	public ServletWebServerFactoryCustomizer servletWebServerFactoryCustomizer(ServerProperties serverProperties) {
		return new ServletWebServerFactoryCustomizer(serverProperties);
	}

	@Bean // 判断是否有 Tomcat 依赖
	@ConditionalOnClass(name = "org.apache.catalina.startup.Tomcat")
	public TomcatServletWebServerFactoryCustomizer tomcatServletWebServerFactoryCustomizer(
			ServerProperties serverProperties) {
		return new TomcatServletWebServerFactoryCustomizer(serverProperties);
	}

```

ServletWebServerFactoryCustomizer

```java
public class ServletWebServerFactoryCustomizer
		implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {

	private final ServerProperties serverProperties;

	public ServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}
```

![](G:\learn-skills\springbootlearn\docs\pics\25-WebServletFactoryCustomizer.png)

TomcatServletWebServerFactory

```java
@Override
	public WebServer getWebServer(ServletContextInitializer... initializers) {
		if (this.disableMBeanRegistry) {
			Registry.disableRegistry();
		}
        // 创建 Tomcat
		Tomcat tomcat = new Tomcat();
        
        // 配置 tomcat 的基本环境
		File baseDir = (this.baseDirectory != null) ? this.baseDirectory : createTempDir("tomcat");
		tomcat.setBaseDir(baseDir.getAbsolutePath());
		Connector connector = new Connector(this.protocol);
		connector.setThrowOnFailure(true);
		tomcat.getService().addConnector(connector);
		customizeConnector(connector);
		tomcat.setConnector(connector);
		tomcat.getHost().setAutoDeploy(false);
		configureEngine(tomcat.getEngine());
		for (Connector additionalConnector : this.additionalTomcatConnectors) {
			tomcat.getService().addConnector(additionalConnector);
		}
		prepareContext(tomcat.getHost(), initializers);
        
        // 将配置好的 Tomcat 传入进去，返回一个 WebServer
		return getTomcatWebServer(tomcat);
	}

protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
		return new TomcatWebServer(tomcat, getPort() >= 0);
	}
```

TomcatWebServer

```java
public TomcatWebServer(Tomcat tomcat, boolean autoStart) {
   Assert.notNull(tomcat, "Tomcat Server must not be null");
   this.tomcat = tomcat;
   this.autoStart = autoStart;
   initialize();
}
private void initialize() throws WebServerException {
		logger.info("Tomcat initialized with port(s): " + getPortsDescription(false));
		synchronized (this.monitor) {
			try {
				addInstanceIdToEngineName();

				Context context = findContext();
				context.addLifecycleListener((event) -> {
					if (context.equals(event.getSource()) && Lifecycle.START_EVENT.equals(event.getType())) {
						// Remove service connectors so that protocol binding doesn't
						// happen when the service is started.
						removeServiceConnectors();
					}
				});

				// Start the server to trigger initialization listeners
				this.tomcat.start();

				// We can re-throw failure exception directly in the main thread
				rethrowDeferredStartupExceptions();

				try {
					ContextBindings.bindClassLoader(context, context.getNamingToken(), getClass().getClassLoader());
				}
				catch (NamingException ex) {
					// Naming is not enabled. Continue
				}

				// Unlike Jetty, all Tomcat threads are daemon threads. We create a
				// blocking non-daemon to stop immediate shutdown
				startDaemonAwaitThread();
			}
			catch (Exception ex) {
				stopSilently();
				destroySilently();
				throw new WebServerException("Unable to start embedded Tomcat", ex);
			}
		}
	}
```

问题：我们对嵌入式容器的配置修改是怎么生效的？

```
ServerProperties、XxxServletWebServerFactoryCustomizer
```

BeanPostProcessorsRegistrar 给容器中导入组件

```java
public static class BeanPostProcessorsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

   private ConfigurableListableBeanFactory beanFactory;

   @Override
   public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
      if (beanFactory instanceof ConfigurableListableBeanFactory) {
         this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
      }
   }

   @Override
   public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
         BeanDefinitionRegistry registry) {
      if (this.beanFactory == null) {
         return;
      }
      registerSyntheticBeanIfMissing(registry, "webServerFactoryCustomizerBeanPostProcessor",
            WebServerFactoryCustomizerBeanPostProcessor.class);
      registerSyntheticBeanIfMissing(registry, "errorPageRegistrarBeanPostProcessor",
            ErrorPageRegistrarBeanPostProcessor.class);
   }

   private void registerSyntheticBeanIfMissing(BeanDefinitionRegistry registry, String name, Class<?> beanClass) {
      if (ObjectUtils.isEmpty(this.beanFactory.getBeanNamesForType(beanClass, true, false))) {
         RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
         beanDefinition.setSynthetic(true);
         registry.registerBeanDefinition(name, beanDefinition);
      }
   }

}
```

容器中导入了  WebServerFactoryCustomizerBeanPostProcessor 组件

```java
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof WebServerFactory) {
            // 如果当前是 WebServerFactory 组件
			postProcessBeforeInitialization((WebServerFactory) bean);
		}
		return bean;
	}

private void postProcessBeforeInitialization(WebServerFactory webServerFactory) {
    // 获取所有的定制器，调用每一定制器的 customize 方法来给 Servlet 进行属性赋值
		LambdaSafe.callbacks(WebServerFactoryCustomizer.class, getCustomizers(), webServerFactory)
				.withLogger(WebServerFactoryCustomizerBeanPostProcessor.class)
				.invoke((customizer) -> customizer.customize(webServerFactory));
	}

private Collection<WebServerFactoryCustomizer<?>> getCustomizers() {
		if (this.customizers == null) {
			// Look up does not include the parent context
            // 从容器中获取所有 WebServerFactoryCustomizer 的组件
			this.customizers = new ArrayList<>(getWebServerFactoryCustomizerBeans());
			this.customizers.sort(AnnotationAwareOrderComparator.INSTANCE);
			this.customizers = Collections.unmodifiableList(this.customizers);
		}
		return this.customizers;
	}

private Collection<WebServerFactoryCustomizer<?>> getWebServerFactoryCustomizerBeans() {
		return (Collection) this.beanFactory.getBeansOfType(WebServerFactoryCustomizer.class, false, false).values();
	}
```

所以，定制 Servlet 容器，给容器中 可以添加一个 WebServerFactoryCustomizer 的组件

步骤：

1）、Spring Boot 根据导入的依赖情况，给容器中添加相应的 TomcatServletWebServerFactoryCustomizer

2）、容器中某个组件要创建对象，经过后置处理器 WebServerFactoryCustomizerBeanPostProcessor，从容器中获取所有的WebServerFactoryCustomizer，调用定制器的定制方法

5、Servlet 容器启动原理
什么时候创建 WebServerFactoryCustomizer  ？什么时候获取 Servlet 容器并启动 Tomcat

创建 Servlet 容器工厂：

1）、Spring Boot 应用启动运行 run 方法

2）、refreshContext 创建 IOC 容器（创建 IOC 容器，并初始化容器，创建容器中每个组件），如果是 web 应用，创建的是 ServletWebServerApplicationContext

```java
	private void refreshContext(ConfigurableApplicationContext context) {
		refresh(context);
		if (this.registerShutdownHook) {
			try {
				context.registerShutdownHook();
			}
			catch (AccessControlException ex) {
				// Not allowed in some environments.
			}
		}
	}
```



3）、refresh 刷新刚才创建的 IOC 容器

```java
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
			prepareRefresh();

			// Tell the subclass to refresh the internal bean factory.
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
				invokeBeanFactoryPostProcessors(beanFactory);

				// Register bean processors that intercept bean creation.
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
				initMessageSource();

				// Initialize event multicaster for this context.
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
				onRefresh();

				// Check for listener beans and register them.
				registerListeners();

				// Instantiate all remaining (non-lazy-init) singletons.
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();

				// Reset 'active' flag.
				cancelRefresh(ex);

				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}
```



4）、onrefresh Web 的IOC容器重写了方法,创建 Servlet 容器

```java
protected void onRefresh() {
		super.onRefresh();
		try {
			createWebServer();
		}
		catch (Throwable ex) {
			throw new ApplicationContextException("Unable to start web server", ex);
		}
	}

	private void createWebServer() {
		WebServer webServer = this.webServer;
		ServletContext servletContext = getServletContext();
		if (webServer == null && servletContext == null) {
            // 获取Servlet容器工厂
			ServletWebServerFactory factory = getWebServerFactory();
            // 获取容器
			this.webServer = factory.getWebServer(getSelfInitializer());
		}
		else if (servletContext != null) {
			try {
				getSelfInitializer().onStartup(servletContext);
			}
			catch (ServletException ex) {
				throw new ApplicationContextException("Cannot initialize servlet context", ex);
			}
		}
		initPropertySources();
	}

protected ServletWebServerFactory getWebServerFactory() {
		// Use bean names so that we don't consider the hierarchy
		String[] beanNames = getBeanFactory().getBeanNamesForType(ServletWebServerFactory.class);
		if (beanNames.length == 0) {
			throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to missing "
					+ "ServletWebServerFactory bean.");
		}
		if (beanNames.length > 1) {
			throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to multiple "
					+ "ServletWebServerFactory beans : " + StringUtils.arrayToCommaDelimitedString(beanNames));
		}
		return getBeanFactory().getBean(beanNames[0], ServletWebServerFactory.class);
	}
```

5、获取 Servlet 的容器工厂，ServletWebServerFactory factory = getWebServerFactory();

6、后置处理器检查是这个对象，就获取所有的定制器来先定制 Servlet 容器的相关配置。

7、从 IOC 容器中获取 this.webServer = factory.getWebServer(getSelfInitializer()); TomcatServletWebServerFactory 创建对象

8、Servlet 容器创建对象并启动 Servlet 容器

**先启动 Servlet 容器，再将 IOC 容器中剩下没有创建出来的对象获取出来**

**IOC 容器启动创建嵌入式的 Servlet 容器**

9、使用外置的 Servlet 容器

嵌入式 Servlet 容器：

优点：简单、便捷

缺点：默认不支持 JSP，优化定制比较复杂（自定义定制器，自己编写嵌入式 Servlet 的工厂【ServletWebServerFactory】）

外置的 Servlet 容器，外面安装 Tomcat -- 应用 war 包的方式打包

