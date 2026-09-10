package com.lisaj7.springbootreview.common;

import lombok.Data;

/*
* 真实项目通常不会让不同接口“随便返回不同类型”。
* 因为这样会导致前端处理起来不统一。
* 常见做法是定义：Result<T>, 统一返回：JSON
* T 是 Java 泛型里的类型参数，也可以理解成“类型占位符”。
* */
@Data
public class Result<T> {

    private boolean success;
    private String message;
    private T data; // data 字段暂时不确定是什么类型，等使用时再指定

    // <T> 是在方法上声明泛型，意思是这个方法可以接收任意类型的数据，并返回对应类型的Result
    // 调用时 Java 会自动推断 T
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

}
