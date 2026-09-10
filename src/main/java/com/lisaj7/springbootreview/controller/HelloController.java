package com.lisaj7.springbootreview.controller;
import org.springframework.web.bind.annotation.*;
import com.lisaj7.springbootreview.entity.User;

/*
表示这个类是一个 Web 控制器，其中方法返回值直接作为 HTTP 响应内容返回
*/
@RestController
public class HelloController {

    /*
    当客户端发送 GET /hello 请求时，调用这个 hello() 方法
     */
    // 示例 http://localhost:8080/hello
    @GetMapping("/hello")
    public String hello(){
        return "你好 Springboot";
    }

    /*
    客户端向controller传参数的方式
     */
    // @RequestParam
    // 从 HTTP 请求的查询参数中取值,参数放在 ? 后面
    // 示例 http://localhost:8080/hello/info?name=Liu&age=18
    @GetMapping("/hello/info")
    public String helloInfo(
            @RequestParam String name,
            @RequestParam Integer age){
        return "name " + name + " age " + age;
    }

    @GetMapping("/hello/users")
    public String searchUser(
            @RequestParam String name,
            @RequestParam Integer age) {

        return "查询用户，name = " + name + "，age = " + age;
    }

    // @PathVariable
    // 从 URL 路径里提取 name 这个变量,参数直接是 URL 路径的一部分
    @GetMapping("/hello/{name}")
    public String helloPath(@PathVariable String name) {
        return "你好 " + name;
    }
    @GetMapping("/hello/users/{id}")
    public String getUser(@PathVariable Long id) {
        return "查询用户，id = " + id;
    }

    // @RequestBody
    // HTTP请求，通过POST提交数据，请求体里要放一个JSON
    // 服务器接收JSON 做序列化 转成Java对象
    // Spring对结果反序列化后，JSON传回客户端
    @PostMapping("/hello/users")
    public User addUser(@RequestBody User user) {
        return user;
    }
}
