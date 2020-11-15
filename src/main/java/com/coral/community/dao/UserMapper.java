package com.coral.community.dao;

import com.coral.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper                       //@Mapper (MyBatis, required)
@Repository                   //@Repository (spring, optional),
public interface UserMapper{
    // we need to create a configuration file for UserMapper to be used ( resource/mapper/user-mapper.xml)
    // config file need to provide server for each method
    // after config, MyBatis will generate the implementation class automatically at the bottom

    /* Get User by ID */
    User selectById(int id);
    /* Get User by name */
    User selectByName(String username);
    /* Get User by email */
    User selectByEmail(String email);
    /* Insert User */
    int insertUser(User user);
    /* Update User Status */
    int updateStatus(int id,int status);
    /* Update User Header */
    int updateHeader(int id,String headerUrl);
    /* Update User Password */
    int updatePassword(int id,String password);
}
