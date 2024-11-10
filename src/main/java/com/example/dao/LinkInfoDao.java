package com.example.dao;

import com.example.entity.LinkInfo;
import com.example.vo.LinkInfoVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface LinkInfoDao extends Mapper<LinkInfo> {
    List<LinkInfoVo> findByName(@Param("name") String name);
    
    
    
    Integer count();
}
