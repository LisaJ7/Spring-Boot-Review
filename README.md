# Spring Boot Review

本项目是一个基于 Spring Boot 的单体 RESTful 后端工程，实现对数据库中用户表的CRUD，用于基础知识的快速复习和掌握。

主要技术栈：SpringBoot 4.1.1 + MyBatis-Plus 3.5.17 + MySQL 8.0 + Lombok

---

## 一、开发环境

```text
JDK 17
Maven 3.9.x
IntelliJ IDEA
Git
Docker Desktop + WSL2
MySQL 8.0
Apifox
```

Maven 本身是构建工具。它通过 `pom.xml` 描述项目依赖，并把所需 Jar 下载到本地仓库。

例如：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
    <version>3.5.17</version>
</dependency>
```
 MyBatis-Plus、Lombok、Jackson、MySQL Connector 这类依赖通常存在于 Maven 本地仓库中，而不是像 MySQL Server 那样有一个独立安装目录。

---

## 二、创建 Spring Boot 项目

项目创建后，最核心的启动类类似：

```java
@SpringBootApplication
public class SpringbootReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootReviewApplication.class, args);
    }
}
```


> `main()` 仍然是普通 Java 程序入口，Spring Boot 并没有改变 Java 的运行规则。

真正关键的是：

```java
@SpringBootApplication
```

它是一个组合注解，核心包含三类能力：

```text
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

可以理解为：

```text
声明这是 Spring Boot 配置入口
+
根据依赖自动完成常见配置
+
扫描当前包及子包中的 Spring 组件
```

例如项目中加入了 Web Starter，Spring Boot 就会自动配置 Spring MVC、Tomcat、JSON 转换器等组件，因此不需要手工安装、配置外部 Tomcat。

传统 Java Web 常见流程是：

```text
项目
↓
打成 WAR
↓
部署到外部 Tomcat
```

Spring Boot 则变成：

```text
运行 main()
↓
Spring Boot 初始化
↓
内置 Tomcat 启动
↓
监听 HTTP 请求
```

日志中看到：

```text
Tomcat started on port 8080
Started SpringbootReviewApplication
```

就说明 Web 应用已经正常运行。

---

## 三、HTTP 与 Controller

需要明确：
```text
HTTP：负责定义请求和响应的语义
```
GET、POST、PUT、DELETE 都属于 HTTP 应用层。

在典型请求-响应模式中：

```text
浏览器 / Vue / App / Apifox
        ↓
   HTTP Request
        ↓
   Spring Boot
        ↓
   HTTP Response
        ↓
      客户端
```

Spring Boot 中的：

```java
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
```

不是“服务器主动发送请求”，而是在告诉 Spring：

> 当收到某种 HTTP 方法和某个 URL 的请求时，应该调用哪个 Java 方法处理。

例如：

```java
@GetMapping("/hello")
public String hello() {
    return "你好 Spring Boot";
}
```

表示：

```text
GET /hello
↓
调用 hello()
↓
返回 HTTP Response
```

---

## 四、GET、POST、PUT、DELETE 与 REST 风格


通常：

```text
GET     查询资源
POST    创建资源 / 提交操作
PUT     修改资源
DELETE  删除资源
```

例如一组典型用户接口：

```text
GET    /users/1
POST   /users
PUT    /users/1
DELETE /users/1
```

它们分别表示：

```text
查询 id=1 的用户
新增用户
修改 id=1 的用户
删除 id=1 的用户
```

这里要注意，URL 相同并不代表接口相同。

例如：

```text
GET  /users
POST /users
```

虽然路径都是 `/users`，但 HTTP Method 不同，因此是两个不同接口。

### GET 不只是“浏览器地址栏请求”

浏览器地址栏默认最方便发送 GET，例如：

```text
http://localhost:8080/hello
```

但 POST、PUT、DELETE 通常还需要 Body、Header 等信息，因此使用 Apifox 或 Postman 更方便。

---

## 五、请求参数绑定：前端的数据如何进入 Java 方法

Spring MVC 的一个核心能力是把 HTTP 请求中的数据自动绑定到 Java 方法参数。

### 1. `@RequestParam`

例如：

```text
GET /users?name=Lisa&age=20
```

Controller：

```java
@GetMapping("/users")
public String query(
        @RequestParam String name,
        @RequestParam Integer age) {
    ...
}
```

`?` 后面是 Query Parameter，多个参数之间使用 `&` 分隔。

这种形式常用于：

```text
搜索
筛选
分页
排序
```

### 2. `@PathVariable`

例如：

```text
GET /users/10
```

Controller：

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    ...
}
```

这里 URL 中的 `10` 被绑定到 `id`。

可以用一个简单方法区分：

```text
PathVariable
更像“我要找哪一个具体资源”

RequestParam
更像“我要按什么条件查询”
```

### 3. `@RequestBody`

POST 请求常用 JSON Body：

```json
{
  "name": "Lisa",
  "age": 20
}
```

Controller：

```java
@PostMapping("/users")
public User addUser(@RequestBody User user) {
    ...
}
```

Spring 会自动把 JSON 转换为 Java 对象。

这个过程叫：

```text
JSON → Java
反序列化
```

返回 Java 对象时：

```text
Java → JSON
序列化
```

这部分通常由 Jackson 完成。

---

## 六、JavaBean、getter/setter 与 Lombok

最开始为了重新理解对象绑定机制，实体类手写 getter/setter：

```java
public class User {

    private Long id;
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

传统 JavaBean 通常具备：

```text
private 字段
public getter/setter
通常有无参构造
```

框架可以借此读取和写入属性。

例如 JSON：

```json
{
  "name": "Lisa"
}
```

可以理解为框架最终把值写入：

```java
user.setName("Lisa");
```

在理解了 getter/setter 的意义之后，再使用 Lombok：

```java
@Data
public class User {
    private Long id;
    private String name;
    private Integer age;
}
```

`@Data` 会生成常见的：

```text
getter
setter
toString
equals
hashCode
```

工业项目中 Lombok 的作用不是改变 Java Bean 原理，而是减少样板代码。

---

## 七、Spring Boot 分层：Controller、Service、Mapper 到底分别负责什么

Spring Boot 后端最核心的工程结构可以概括成：

```text
客户端
↓
Controller
↓
Service
↓
Mapper
↓
MySQL
```

### Controller：负责 HTTP

Controller 应该主要负责：

```text
接收请求
解析参数
调用 Service
返回响应
```

例如：

```java
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.getUser(id);
}
```

Controller 不应该堆积复杂业务逻辑。

### Service：负责业务规则

Service 负责：

```text
参数业务判断
业务流程编排
多个数据操作组合
对象转换
事务边界
```

例如注册用户未来可能包含：

```text
检查用户是否存在
↓
校验业务规则
↓
创建用户
↓
保存数据库
```

这些不应该全部写在 Controller 中。

### Mapper：负责数据库访问

Mapper 对应数据访问层，也可理解为 DAO。

例如：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

Mapper 负责：

```text
查询
新增
修改
删除
```

而不负责复杂业务判断。

可以记成：

```text
Controller：别人怎么调用我
Service：业务应该怎么做
Mapper：数据怎么存取
```

---

## 八、IoC、DI 与 Bean：为什么不用自己 new Service

传统 Java 中：

```java
UserService service = new UserService();
```

对象由程序员自己创建。

Spring 中：

```java
@Service
public class UserServiceImpl {
}
```

Spring 会扫描这个类并创建对象。

这个由 Spring 管理的对象叫 Bean。

### IoC：控制反转

以前：

```text
程序员控制对象创建
```

现在：

```text
Spring 容器控制对象创建
```

这就是 IoC。

### DI：依赖注入

Controller 依赖 Service：

```java
private final IUserService userService;

public UserController(IUserService userService) {
    this.userService = userService;
}
```

并没有：

```java
new UserServiceImpl()
```

Spring 会找到实现 `IUserService` 的 Bean 并注入进来。

可以理解为：

```text
IoC
解决“对象由谁创建”

DI
解决“对象之间怎么连接”
```

当前项目使用构造器注入，是因为依赖更加显式，也方便使用 `final`，更利于测试和维护。

---

## 九、Service 接口与实现类：为什么要有 `IUserService`

项目中使用：

```text
IUserService
    ↑
UserServiceImpl
```

接口定义业务能力：

```java
public interface IUserService {
    User getUser(Long id);
}
```

实现类负责具体实现：

```java
@Service
public class UserServiceImpl implements IUserService {
    ...
}
```

Controller 依赖接口：

```java
private final IUserService userService;
```

这体现“面向接口编程”。

这样上层只关心：

```text
Service 能做什么
```

而不需要绑定：

```text
Service 具体怎么实现
```

对于非常小的项目，Service 不一定必须拆接口；但在中大型项目、需要多实现或需要更清晰抽象时，这种方式很常见。

---

## 十、Docker 与 MySQL：为什么数据库适合放到容器里

可将 MySQL、Redis 等服务部署在 Docker 容器中。注意 Java 依赖和外部服务不是一回事。 需要区分：

```text
MyBatis-Plus
Lombok
MySQL Connector
```

这些是 Java 库依赖，由 Maven 管理。

而：

```text
MySQL
Redis
RabbitMQ
Nacos
```

属于独立运行的服务，更适合单独运行在 Docker 容器里。

最终常见结构是：

```text
Docker
├── app
├── mysql
└── redis
```

而不是把 MyBatis-Plus、Lombok 各自做成容器。

Docker 与微服务的关系也需要区分：

```text
微服务
是架构思想，回答“系统如何拆分”

Docker
是运行与部署技术，回答“这些服务如何方便运行”
```

使用 Docker 不代表系统就是微服务；不使用 Docker 也仍然可以做微服务。

---

## 十一、MyBatis-Plus：从 Mapper 到 CRUD

数据库中创建：

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    age INT
);
```

实体：

```java
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer age;
}
```

Mapper：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

关键在于：

```java
extends BaseMapper<User>
```

MyBatis-Plus 已经提供常见 CRUD：

```java
selectById()
selectList()
insert()
updateById()
deleteById()
```

增删改方法通常返回 `int`，表示：

> SQL 影响了多少行。

所以 Service 常写：

```java
return userMapper.deleteById(id) > 0;
```

把数据库层的“影响行数”转换成业务层更直观的 `boolean`。

---

## 十二、`Serializable`：为什么 `deleteById` 参数不是直接写 Long

MyBatis-Plus 方法签名中会看到：

```java
int deleteById(Serializable id);
```

这里的 `Serializable` 是：

```java
java.io.Serializable
```

它是一个标记接口，没有需要实现的方法。

作用是声明：

> 这个类型的对象具有可序列化能力。

`Long`、`Integer`、`String` 等常见类型都实现了 `Serializable`，因此 MyBatis-Plus 可以统一支持多种主键类型。

所以：

```java
deleteById(1L);
deleteById("user-001");
```

从方法类型设计上都可以成立。

这里需要区分：

```text
Serializable
= 一个 Java 接口类型

serialization
= 把对象转换成可保存/传输形式的过程
```

`deleteById(Serializable id)` 并不是要求先把数字“序列化后再传入”。

---

## 十三、自增主键、删除与工业实践

删除 id=2 后，数据库可能变成：

```text
1
3
4
```

这是完全正常的。

工业界通常不会回收已经使用过的主键，因为主键的职责是：

> 唯一标识一条记录，而不是保证数字连续。

如果旧 ID 被重新使用，日志、缓存、订单引用、审计记录等可能产生歧义。

另外，自增 ID 本身也不保证严格连续。事务回滚、并发插入等场景都可能出现跳号。

### 物理删除与逻辑删除

当前项目的：

```java
deleteById(id)
```

执行的是物理删除，数据库那一行真正消失。

工业项目中很多重要业务会采用逻辑删除：

```text
id | name | deleted
1  | Lisa | 0
2  | Tom  | 1
```

用户看来数据已经删除，但数据库中仍保留记录。

MyBatis-Plus 可以通过：

```java
@TableLogic
```

支持逻辑删除。

订单、支付、合同、财务、审计类数据尤其常见逻辑删除或历史保留。

---

## 十四、条件查询：`LambdaQueryWrapper`

项目继续加入条件查询。

例如：

```java
LambdaQueryWrapper<User> wrapper =
        new LambdaQueryWrapper<>();

wrapper.eq(User::getName, name);
```

大致对应：

```sql
WHERE name = ?
```

这里：

```java
User::getName
```

是 Java 方法引用。

MyBatis-Plus 用它表达：

```text
User 的 name 字段
```

相比：

```java
wrapper.eq("name", name);
```

方法引用更安全，字段重构时 IDE 更容易发现问题。

常用条件包括：

```java
eq()
ne()
gt()
ge()
lt()
le()
like()
in()
orderByAsc()
orderByDesc()
```

例如：

```java
wrapper.gt(User::getAge, 18);
```

对应：

```sql
WHERE age > 18
```

动态条件可以这样写：

```java
wrapper.eq(name != null, User::getName, name);
```

只有条件为 `true` 时，MyBatis-Plus 才会拼接这一段 SQL。

---

## 十五、分页查询：Page 与分页拦截器

分页常见请求：

```text
GET /users/page?current=1&size=2
```

其中：

```text
current = 当前页
size    = 每页条数
```

MyBatis-Plus 使用：

```java
Page<User> page = new Page<>(current, size);
```

然后：

```java
userMapper.selectPage(page, wrapper);
```

分页结果中通常包含：

```text
records
total
current
size
pages
```

需要特别理解：

> `Page` 对象只是承载分页参数和分页结果，真正把 SQL 改造成分页 SQL 的是分页插件。

项目中曾出现：

```json
{
  "current": 1,
  "pages": 0,
  "records": [...全部数据...],
  "size": 2,
  "total": 0
}
```

这说明 `Page` 存在，但分页拦截器没有生效。

随后加入：

```java
PaginationInnerInterceptor
```

并注册到：

```java
MybatisPlusInterceptor
```

分页插件会拦截 SQL，大致产生：

```sql
SELECT COUNT(*)
FROM user;
```

以及：

```sql
SELECT *
FROM user
LIMIT ?, ?;
```

从而填充 `total`、`pages`、`records`。

### Ambiguous Mapping

项目还遇到两个 Controller 方法都映射：

```text
GET /users/page
```

虽然 Java 允许方法重载：

```java
pageUsers(Long, Long)
pageUsers(Long, Long, String)
```

但 Spring MVC 判断接口主要看：

```text
HTTP Method + URL
```

所以会报：

```text
Ambiguous mapping
```

解决方式是保留一个方法，把额外筛选参数设计成可选参数：

```java
@RequestParam(required = false) String name
```

这体现一个重要原则：

> Java 方法签名不同，不代表 HTTP 路由不同。

---

## 十六、统一响应 `Result<T>`

最开始 Controller 可能分别返回：

```text
User
boolean
Page<User>
```

工业项目中更常见的是统一响应格式，例如：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {}
}
```

因此创建：

```java
public class Result<T> {
    private boolean success;
    private String message;
    private T data;
}
```

泛型 `<T>` 使一个统一响应结构可以承载：

```text
Result<User>
Result<UserVO>
Result<List<User>>
Result<Page<User>>
Result<Void>
```

需要注意：

> `Result` 通常是项目自己定义的工具类，不是 Spring Boot 自带 API。

统一响应的价值在于让前端可以用一致方式处理所有接口。

---

## 十七、Entity、DTO、VO：为什么不能让一个 User 贯穿所有层

这是从“能写 CRUD”进入“工程化设计”的重要一步。

### Entity

Entity 面向数据库，例如：

```java
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String password;
}
```

它反映数据库结构。

### DTO

DTO 用于数据传输。

例如创建用户：

```java
public class UserCreateDTO {
    private String name;
    private Integer age;
    private String password;
}
```

Controller 接收：

```java
@RequestBody UserCreateDTO dto
```

Service 中转换为 Entity：

```text
UserCreateDTO
↓
User
↓
Mapper
↓
MySQL
```

### VO

VO 更偏向前端展示，例如：

```java
public class UserVO {
    private Long id;
    private String name;
    private Integer age;
}
```

它可以隐藏：

```text
password
deleted
内部状态字段
```

也可以增加数据库中不存在的展示字段，例如：

```text
ageText
statusText
```

返回链路：

```text
MySQL
↓
Entity
↓
Service
↓
VO
↓
Controller
↓
前端
```

不同公司对 DTO、VO 命名并不完全统一。

有些项目会直接让 `UserDTO` 承担返回对象职责，而不额外创建 VO。关键不是死记名字，而是理解：

> 数据库模型、请求模型、响应模型之间应该适度隔离。

对于简单项目，如果 DTO 和 VO 完全一样，就没有必要为了形式而制造重复对象。

---

## 十八、参数校验：不要让非法数据进入业务层

例如前端提交：

```json
{
  "name": "",
  "age": -5
}
```

这种数据不应该进入 Service。

因此在 DTO 中声明约束：

```java
@NotBlank(message = "用户名不能为空")
private String name;

@NotNull(message = "年龄不能为空")
@Min(value = 0, message = "年龄不能小于0")
private Integer age;
```

Controller 使用：

```java
@Valid @RequestBody UserCreateDTO dto
```

流程变成：

```text
HTTP Request
↓
JSON → DTO
↓
参数校验
↓
合法
→ Controller → Service

非法
→ 抛出校验异常
```

常见校验注解包括：

```text
@NotNull
@NotBlank
@Min
@Max
@Size
@Email
```

Spring Boot 新版本使用的是：

```java
jakarta.validation
```

而旧项目可能仍然使用：

```java
javax.validation
```

这是以后阅读旧版 Spring Boot 项目时需要注意的版本差异。

---

## 十九、全局异常处理：不要在每个 Controller 里 try-catch

为了统一错误响应，创建全局异常处理器：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

再通过：

```java
@ExceptionHandler(...)
```

处理不同异常。

例如参数校验异常：

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
```

最终可以统一返回：

```json
{
  "success": false,
  "message": "用户名不能为空",
  "data": null
}
```

这样 Controller 不需要到处写：

```java
try {
    ...
} catch (...) {
    ...
}
```

职责更清晰：

```text
DTO
负责声明合法性规则

@Valid
负责触发校验

GlobalExceptionHandler
负责统一异常响应

Controller
负责正常请求入口
```

---

## 二十、Profile：同一套代码如何适配开发和生产环境

项目最终使用：

```text
application.yaml
application-dev.yaml
application-prod.yaml
```

主配置：

```yaml
spring:
  profiles:
    active: dev
```

开发环境配置数据库、本地调试参数；生产环境通过环境变量注入。

例如：

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

语法：

```text
${环境变量名:默认值}
```

例如：

```yaml
password: ${DB_PASSWORD:123456}
```

表示：

```text
优先使用 DB_PASSWORD
不存在时使用默认值 123456
```

工业项目中，真实生产密码不应该写进 Git 仓库。

更重要的思想是：

> 同一套代码，通过不同配置运行在不同环境，而不是复制多份代码。

---

## 二十一、日志：为什么不用 `System.out.println`

项目使用 Lombok：

```java
@Slf4j
```

然后：

```java
log.debug("准备查询用户，id={}", id);
log.info("查询用户成功，id={}", id);
log.warn("用户不存在，id={}", id);
log.error("创建用户失败", e);
```

常见日志级别：

```text
DEBUG
开发调试信息

INFO
正常业务运行信息

WARN
出现异常情况，但系统仍可继续

ERROR
严重错误或异常
```

推荐：

```java
log.info("查询用户，id={}", id);
```

而不是：

```java
log.info("查询用户，id=" + id);
```

前者是日志框架标准的参数化写法。

日志级别可以在配置文件中控制：

```yaml
logging:
  level:
    root: info
    com.lisaj7.springbootreview: debug
```

---

## 二十二、当前项目完整调用链

到这里，`springboot-review` 已经恢复了一条较完整的工业后端主链路：

```text
Apifox / 前端
↓
HTTP Request
↓
Tomcat
↓
Spring MVC
↓
Controller
↓
Request DTO
↓
Service
↓
Entity
↓
Mapper
↓
MyBatis-Plus
↓
JDBC
↓
MySQL
↓
Entity
↓
VO / DTO
↓
Result<T>
↓
HTTP Response
↓
客户端
```

外围还有：

```text
Validation
GlobalExceptionHandler
Profile
Environment Variables
Logging
Git
Docker
```

这些共同构成了一个最小但完整的 Spring Boot 后端开发基础。

---
