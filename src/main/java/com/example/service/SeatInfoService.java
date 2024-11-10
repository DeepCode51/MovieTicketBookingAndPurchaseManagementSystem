package com.example.service;

import com.example.dao.SeatInfoDao;
import com.example.entity.SeatInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 座位
 */
@Service
public class SeatInfoService {

    @Resource
    private SeatInfoDao seatInfoDao;

    public SeatInfo findDetail(Long goodsId) {
        return seatInfoDao.findDetail(goodsId);
    }

    public void save(SeatInfo seatInfo) {
        if (seatInfo.getId() != null) {
            seatInfoDao.updateByPrimaryKeySelective(seatInfo);
        } else {
            seatInfoDao.insertSelective(seatInfo);
        }
    }
}
