package com.example.dao;

import com.example.entity.AdminInfo;
import com.example.vo.AdminInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface AdminInfoDao extends Mapper<AdminInfo> {
    //根据名查询管理员信息
    List<AdminInfoVo> findByName(@Param("name") String name);

    //检验该管理员是否存在
    int checkRepeat(@Param("column") String column, @Param("value") String value, @Param("id") Long id);

    //根据用户名查询讯管理员信息
    AdminInfoVo findByUsername(String username);

    //统计管理员和用户成员数量
    Integer count();


    AdminInfoVo  findByEmail(@Param("email") String email);

    //根据确认码查询挂力源信息
    AdminInfo findByConfirmCode(@Param("confirmCode") String confirmCode);

    int deleteOldAdmin(@Param("email") String email);

    //找回密码，先判断成员的信息是否正确
    AdminInfo findPassword(@Param("name")String name,@Param("email") String email,@Param("level") Integer level);
}
