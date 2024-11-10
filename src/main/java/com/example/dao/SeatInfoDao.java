package com.example.dao;

import com.example.entity.SeatInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface SeatInfoDao extends Mapper<SeatInfo> {
    @Select("select * from seat_info where goodsId = #{goodsId}")
    SeatInfo findDetail(Long goodsId);

    @Delete("delete from seat_info where userId = #{userId} and level = #{level} and goodsId = #{goodsId}")
    void deleteByUserIdAndGoodsId(Long userId, Integer level, Long goodsId);
}
