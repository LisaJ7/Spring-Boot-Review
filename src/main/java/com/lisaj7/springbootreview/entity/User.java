/*
*Entity → 对应数据库表
*
* User = 数据库实体
* */
package com.lisaj7.springbootreview.entity;

// 使用mybatis-plus插件，实现java与数据库的持久化数据交互
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

// 使用Lombok, 替代手写getter/setter
import lombok.Data;

@Data
@TableName("user") // 绑定表的名称, 表示这个 Java 类对应数据库里的 user 表
public class User {

    @TableId(type = IdType.AUTO)  // 绑定表的主键,表示id 是主键，而且由 MySQL 自增生成
    private Long id;

    private String name;

    private Integer age;

    private String password;

/* 不再需要，由Lombok插件实现，其注解为@Data
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    */
}
