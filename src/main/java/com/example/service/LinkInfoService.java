package com.example.service;

import cn.hutool.json.JSONUtil;
import com.example.dao.LinkInfoDao;
import org.springframework.stereotype.Service;
import com.example.entity.LinkInfo;
import com.example.entity.AuthorityInfo;
import com.example.entity.Account;
import com.example.vo.LinkInfoVo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class LinkInfoService {

    @Resource
    private LinkInfoDao linkInfoDao;

    public LinkInfo add(LinkInfo linkInfo) {
        linkInfoDao.insertSelective(linkInfo);
        return linkInfo;
    }

    public void delete(Long id) {
        linkInfoDao.deleteByPrimaryKey(id);
    }

    public void update(LinkInfo linkInfo) {
        linkInfoDao.updateByPrimaryKeySelective(linkInfo);
    }

    public LinkInfo findById(Long id) {
        return linkInfoDao.selectByPrimaryKey(id);
    }

    public List<LinkInfoVo> findAll() {
        return linkInfoDao.findByName("all");
    }

    public PageInfo<LinkInfoVo> findPage(String name, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        List<LinkInfoVo> all = findAllPage(request, name);
        return PageInfo.of(all);
    }

    public List<LinkInfoVo> findAllPage(HttpServletRequest request, String name) {
		return linkInfoDao.findByName(name);
    }

}
