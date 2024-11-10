package com.example.dao;

import com.example.entity.UserInfo;
import com.example.vo.UserInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface UserInfoDao extends Mapper<UserInfo> {
    UserInfo findByConfirmCode(@Param("confirmCode") String confirmCode) ;

    List<UserInfoVo> findByName(@Param("name") String name);

    int checkRepeat(@Param("column") String column, @Param("value") String value, @Param("id") Long id);
    UserInfoVo findByUsername(String username);
    Integer count();

    //根基邮箱查找用户
    UserInfo findByEmail(@Param("email") String email);

    //删除激活链接失效的且为激活的用户信息
    int deleteOldUser(@Param("email") String email);

    UserInfo  findPassword(@Param("name")String name,@Param("email") String email,@Param("level") Integer level);
}
