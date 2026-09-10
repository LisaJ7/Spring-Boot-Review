package com.lisaj7.springbootreview.service;

import com.lisaj7.springbootreview.entity.User;
import org.springframework.stereotype.Service;

/*
该注解表示，这是一个业务层组件，请交给 Spring 管理
Spring 启动时会扫描到它，然后创建一个UserService 对象
并放进 Spring 的 IoC（控制反转）容器
这个被 Spring 管理的对象就叫 Bean
*/
/*
@Service
public class UserService {
    public String addUser(User user) {
        return "Service 已处理用户：" + user.getName();
    }

    public String getUser(Long id) {
        return "Service 查询用户，id = " + id;
    }
}

 */
