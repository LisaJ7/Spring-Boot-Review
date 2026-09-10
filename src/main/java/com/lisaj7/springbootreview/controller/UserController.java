package com.lisaj7.springbootreview.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lisaj7.springbootreview.DTO.UserCreateDTO;
import com.lisaj7.springbootreview.DTO.UserDTO;
import com.lisaj7.springbootreview.VO.UserVO;
import com.lisaj7.springbootreview.common.Result;
import com.lisaj7.springbootreview.entity.User;
import com.lisaj7.springbootreview.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
/*
类上写 @RequestMapping("/users")
方法上只需要 @GetMapping("/{id}")
Spring 会把两部分拼起来
最终： /users/{id}

@RestController和@RequestMapping("/users")拼起来
可以理解为这个 Controller 专门处理 /users 开头的请求
*/
@RequestMapping("/users")
public class UserController {

    /*
    * 依赖注入，Dependency Injection，DI
    * 这里没有写new UserService();但是UserService userService照样有对象
    * 因为 Spring 帮我们创建并注入了，这就是依赖注入
    * */

    // private final UserService userService;

    /*
    * 控制反转IoC：以前我们定义了一个类以后，通过new来自己创建对象；现在通过@Service，Spring 创建类的对象
    * 以前 程序员控制对象创建， 现在 Spring 控制对象创建， 所以称之为 控制反转

    * UserController 依赖 UserService：
    * 以前需要手动new UserService()
    * 现在Spring自动通过IoC容器，找到UserService Bean，注入UserController
    * 这就是依赖注入
    *
    * IoC解决“对象由谁创建”
    * DI解决“对象之间怎么连接”
    * */

    /*
    public UserController(UserService userService) {
        this.userService = userService;
    }
    */

    /*
    * 为了将业务的定义和实现逻辑分开，对service采用接口和实现类的形式
    * 接口只声明：“业务层提供哪些能力。” 但不写具体怎么实现。例如 IUserService
    * 实现类负责：“这些业务能力具体怎么做。” 例如UserServiceImpl
    * 注意，此时，推荐让Controller依赖接口，这是“面向接口编程”，表示Controller 只关心“Service 能做什么”，不关心“具体怎么实现”。
    *
    * 其中，controller依赖接口，Spring注入实现类
    * */

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {

        User user = userService.getUser(id);

        if(user == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(user); // 在controller层统一响应
    }

    @PostMapping
    public Result<User> addUser(@RequestBody User user) {

        User newUser = userService.addUser(user);

        if(newUser == null) {
            return Result.fail("添加用户失败");
        }
        return Result.ok(newUser);
    }

    /* 处理 PUT 修改 的 HTTP请求
    *  这里同时用两种传参 */
    @PutMapping("/{id}")
    public Result<Void> updateUser( // boolean不能作为泛型，改用Void
            @PathVariable Long id,
            @RequestBody User user) {

        user.setId(id);  // 注意，这里要修改的用户的id来自HTTP请求URL解析，
        // 而不是JSON body, 所以要把参数回填到JAVA对象里
        boolean success = userService.updateUser(user);

        if(!success) {
            return Result.fail("修改用户信息失败");
        }
        return Result.ok(null);
    }

    /* 处理 DELETE 删除 的 HTTP请求 */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {

        boolean success =  userService.deleteUser(id);

        if(!success) {
            return Result.fail("删除用户信息失败");
        }
        return Result.ok(null);
    }

    /* 查询全部用户 */
    @GetMapping
    public Result<List<User>> listUsers() {

        List<User> searchResult = userService.listUsers();

        if(searchResult == null) {
            return Result.fail("查询全部用户失败");
        }
        return Result.ok(searchResult);
    }

    /* 条件查询用户 */
    @GetMapping("search")
    public Result<List<User>> searchUsers(@RequestParam String name) {

        List<User> searchResult = userService.searchByName(name);

        if(searchResult == null) {
            return Result.fail("按name条件查询失败");
        }
        return Result.ok(searchResult);
    }

    @GetMapping("search/{age}")
    public Result<List<User>> searchByAge(@PathVariable Integer age) {

        List<User> searchResult = userService.searchByAge(age); // 返回年龄 >= age 的对象

        if(searchResult == null) {
            return Result.fail("按年龄（>=）条件查询失败");
        }
        return Result.ok(searchResult);
    }

    // 分页查询，查询第current页，每页size条。 例如 GET /users/page?current=1&size=10
    // Page<> 是MyBatis-Plus 的分页对象，通常包含：
    // records   当前页数据
    // total     总记录数
    // size      每页大小
    // current   当前页
    // pages     总页数
    // 注意，MyBatis-Plus 新版本里 分页需要配置PaginationInnerInterceptor
    // 已配置 MyBatisPlusConfig.java
    @GetMapping("/page")
    public Result<Page<User>> pageUsers(
            @RequestParam Long current,
            @RequestParam Long size) {
        Page<User> searchResult = userService.pageUsers(current, size);

        if(searchResult == null) {
            return Result.fail("分页查询失败");
        }
        return Result.ok(searchResult);
    }

    // 测试 http://localhost:8080/users/page1?current=1&size=5&name=Lisa
    @GetMapping("/page1")
    public Result<Page<User>> pageUsers(
            @RequestParam Long current,
            @RequestParam Long size,
            @RequestParam String name) {

        Page<User> searchResult = userService.pageUsers(current, size, name);

        if(searchResult == null) {
            return Result.fail("按name分页查询失败");
        }
        return Result.ok(searchResult);
    }

    /*
    * 下面来学习DTO和VO，为了节约时间，仅对getUser和addUser改造
    *
    * user entity 中含有4个字段：id，name，age，password 对应数据库
    * userDTO用于数据传输，在service和controller层，需要从entity获取的信息中提取3个字段给controller
    * userVO用于前端展示，在controller层，需要向userDTO中加上数据库没有的字段ageText，返回给前端。
    * */
    @GetMapping("/VO/{id}")
    public Result<UserVO> getUserVO(@PathVariable Long id) {

        UserVO user = userService.getUserVO(id);

        if(user == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(user); // 在controller层统一响应
    }

    @PostMapping("/DTO")
    public Result<UserDTO> addUserDTO(@Valid @RequestBody UserCreateDTO user) { // @Valid开启DTO校验规则

        UserDTO newUser = userService.addUserCreateDTO(user);

        if(newUser == null) {
            return Result.fail("添加用户失败");
        }
        return Result.ok(newUser);
    }

}
