/*
DTO（data transition object）→ 接收前端传来的业务数据

UserDTO = 对外传输的数据

我们需要在DTO层对前端传入数据进行前置校验

使用Validation 依赖，给 UserCreateDTO 加校验规则

需要在controller层开启校验，添加@Valid

开启校验后，可以进行全局异常处理
 */
package com.lisaj7.springbootreview.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateDTO {

    private String password;

    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄不能小于0")
    private Integer age;

    /*
    * 校验规则常见注解
        @NotNull
        不能是 null

        @NotBlank
        字符串不能是 null、空串、全空格

        @Min(0)
        数值不能小于 0

        @Max(...)
        数值不能超过某个值

        @Size(...)
        字符串/集合长度限制

        @Email
        邮箱格式校验
    * */
}
