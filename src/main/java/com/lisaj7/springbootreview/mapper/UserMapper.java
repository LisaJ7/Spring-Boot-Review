package com.lisaj7.springbootreview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lisaj7.springbootreview.entity.User;
import org.apache.ibatis.annotations.Mapper;

/*
* 对user表的持久化数据交互, 这里是接口
* 通过继承BaseMapper，MyBatis-Plus 已经提供
* selectById  selectList  insert  updateById  deleteById
* 现在甚至不用自己写 SQL
* */
@Mapper
public interface UserMapper extends BaseMapper<User>{
}