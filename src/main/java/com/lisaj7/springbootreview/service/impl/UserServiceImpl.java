package com.lisaj7.springbootreview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lisaj7.springbootreview.DTO.UserCreateDTO;
import com.lisaj7.springbootreview.DTO.UserDTO;
import com.lisaj7.springbootreview.VO.UserVO;
import com.lisaj7.springbootreview.mapper.UserMapper;
import com.lisaj7.springbootreview.service.IUserService;

import com.lisaj7.springbootreview.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j  // 记录日志
@Service
public class UserServiceImpl implements IUserService {

    // service注入mapper
    // 现在，service不再返回假数据，而是真的访问数据库
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserVO getUserVO(Long id) {
        log.debug("准备查询用户，id={}" , id);

        User user = userMapper.selectById(id);

        if (user == null) {
            log.warn("用户不存在，id={}", id);
            return null;
        }

        log.info("查询用户成功，id={}, name={}", user.getId(), user.getName());

        UserVO result = new UserVO();
        result.setId(user.getId());
        result.setName(user.getName());
        result.setAgeText(user.getAge() + "岁");

        return result;
    }

    @Override
    public User addUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public  UserDTO addUserCreateDTO(UserCreateDTO dto) {
        // 这里userMapper已经指定了操作类型是User，所以必须匹配类型
        User user = new User();
        user.setAge(dto.getAge());
        user.setName(dto.getName());
        user.setPassword(dto.getPassword()); // 传入时有密码无ID

        userMapper.insert(user); // 数据库插入行，自动创建ID

        UserDTO result = new UserDTO();
        result.setId(user.getId());
        result.setName(user.getName());
        result.setAge(user.getAge()); // 传出时有ID，不显示密码, UserDTO充当ResponseDTO

        return result;
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public List<User> listUsers() {
        // 传入null，表示不加任何查询条件，查询全部
        return userMapper.selectList(null);
    }

    @Override
    public List<User> searchByName(String name) {
        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        // 此处为“方法引用”，MyBatis-Plus 用它来判断查询的是 User.name 字段
        // 大致对应 WHERE name = ？
        wrapper.eq(User::getName, name);

        return userMapper.selectList(wrapper);
    }

    @Override
    public List<User> searchByAge(Integer age) {
        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        // 大致对应 WHERE age >= age, 注意第二个age是来自HTTP请求的传入参数值
        wrapper.ge(User::getAge, age);

        return userMapper.selectList(wrapper);
    }
    /*
    * 常用查询条件：
        eq() =
        ne() !=
        gt() >
        ge() >=
        lt() <
        le() <=
        like() LIKE
        in() IN
        orderByAsc() ORDER BY 升序
        orderByDesc() ORDER BY 降序
    *
    * */

    @Override
    public Page<User> pageUsers(Long current, Long size) {

        Page<User> page = new Page<>(current, size);

        /*
        * 大致生成类似
            SELECT *
            FROM user
            LIMIT ?, ?;

        return userMapper.selectPage(page, null);
        * */

        // 下面实现有排序的分页查询，按照年龄倒序
        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        wrapper.orderByDesc(User::getAge);

        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<User> pageUsers(Long current, Long size, String name) {
        Page<User> page = new Page<>(current, size);

        LambdaQueryWrapper<User> wrapper =
                new LambdaQueryWrapper<>();

        /*
        * 如果HTTP请求传入的参数是name=Lisa，那么以下SQL类似
            SELECT *
            FROM user
            WHERE name LIKE '%Lisa%'
            ORDER BY id DESC
            LIMIT ...;
        * */
        wrapper.like(
                name != null && !name.isBlank(),
                User::getName,
                name
        );
        wrapper.orderByDesc(User::getId);

        return userMapper.selectPage(page, wrapper);
    }

    /*
    * 无mapper前的交互逻辑，已废弃
    *
    @Override
    public String getUser(Long id) {
        return "Service 查询用户，id = " + id;
    }

    @Override
    public String addUser(User user) {
        return "Service 新增用户： " + user.getName();
    }
    *
    * */
}
