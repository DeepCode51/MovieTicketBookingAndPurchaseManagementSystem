package com.example.service;

import cn.hutool.json.JSONUtil;
import com.example.dao.AdvertiserInfoDao;
import org.springframework.stereotype.Service;
import com.example.entity.AdvertiserInfo;

import com.example.vo.AdvertiserInfoVo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Value;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class AdvertiserInfoService {

//    @Value("${authority.info}")
    private String authorityInfo="[{\"models\":[{\"modelId\":1,\"operation\":[1,2,3,4]},{\"modelId\":1000001,\"operation\":[1,2,3,4]},{\"modelId\":1000003,\"operation\":[1,2,3,4]},{\"modelId\":2,\"operation\":[1,2,3,4]},{\"modelId\":3,\"operation\":[1,2,3,4]},{\"modelId\":4,\"operation\":[1,2,3,4]},{\"modelId\":10000014,\"operation\":[1,2,3,4]},{\"modelId\":1000015,\"operation\":[1,2,3,4]},{\"modelId\":1000016,\"operation\":[1,2,3,4]},{\"modelId\":1000017,\"operation\":[1,2,3,4]},{\"modelId\":1000018,\"operation\":[1,2,3,4]}],\"level\":1,\"modelId\":1,\"name\":\"管理员\"},{\"models\":[{\"modelId\":1000001,\"operation\":[4]},{\"modelId\":1000003,\"operation\":[4]},{\"modelId\":2,\"operation\":[4]},{\"modelId\":4,\"operation\":[4]},{\"modelId\":10000014,\"operation\":[4]},{\"modelId\":1000015,\"operation\":[4]},{\"modelId\":1000016,\"operation\":[4]},{\"modelId\":1000017,\"operation\":[4]},{\"modelId\":1000018,\"operation\":[4]}],\"level\":2,\"modelId\":3,\"name\":\"用户\"}]";

    @Resource
    private AdvertiserInfoDao advertiserInfoDao;

    public AdvertiserInfo add(AdvertiserInfo advertiserInfo) {
        advertiserInfo.setTime(LocalDateTime.now());
//        advertiserInfo.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        advertiserInfoDao.insertSelective(advertiserInfo);
        return advertiserInfo;
    }

    public void delete(Long id) {
        advertiserInfoDao.deleteByPrimaryKey(id);
    }

    public void update(AdvertiserInfo advertiserInfo) {
        advertiserInfo.setTime(LocalDateTime.now());
//        advertiserInfo.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        advertiserInfoDao.updateByPrimaryKeySelective(advertiserInfo);
    }

    public AdvertiserInfo findById(Long id) {
        return advertiserInfoDao.selectByPrimaryKey(id);
    }

    public List<AdvertiserInfoVo> findAll() {
        return advertiserInfoDao.findByName("all");
    }

    public PageInfo<AdvertiserInfoVo> findPage(String name, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        List<AdvertiserInfoVo> all = findAllPage(request, name);
        return PageInfo.of(all);
    }

    public List<AdvertiserInfoVo> findAllPage(HttpServletRequest request, String name) {
		return advertiserInfoDao.findByName(name);
    }

}
