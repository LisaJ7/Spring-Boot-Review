package com.lisaj7.springbootreview.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lisaj7.springbootreview.DTO.UserCreateDTO;
import com.lisaj7.springbootreview.DTO.UserDTO;
import com.lisaj7.springbootreview.VO.UserVO;
import com.lisaj7.springbootreview.entity.User;

import java.util.List;

public interface IUserService {

    User getUser(Long id);

    UserVO getUserVO(Long id);

    User addUser(User user);

    UserDTO addUserCreateDTO(UserCreateDTO user);

    boolean updateUser(User user);

    boolean deleteUser(Long id);

    List<User> listUsers();

    List<User> searchByName(String name);

    List<User> searchByAge(Integer age);

    /* 分页查询 带排序*/
    Page<User> pageUsers(Long current, Long size);

    /* 分页查询 与 条件查询 组合*/
    Page<User> pageUsers(Long current, Long size, String name);
    /*
    String getUser(Long id);

    String addUser(User user);
    * */

}
