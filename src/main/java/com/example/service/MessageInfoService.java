package com.example.service;

import com.example.dao.MessageInfoDao;
import com.example.entity.MessageInfo;
import com.example.vo.MessageInfoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageInfoService {

    @Resource
    private MessageInfoDao messageInfoDao;

    public MessageInfo add(MessageInfo messageInfo) {
        //设置时间
        messageInfo.setTime(LocalDateTime.now());
//        System.out.println("加入了当前时间："+messageInfo);
        messageInfoDao.insertSelective(messageInfo);
        return messageInfo;
    }

    public void delete(Long id) {
        messageInfoDao.deleteByPrimaryKey(id);
    }

    public void update(MessageInfo messageInfo) {
        messageInfoDao.updateByPrimaryKeySelective(messageInfo);
    }

    public MessageInfo findById(Long id) {
        return messageInfoDao.selectByPrimaryKey(id);
    }

    public List<MessageInfoVo> findAll() {
        List<MessageInfoVo> all = messageInfoDao.findByParentId(0L);
        for (MessageInfoVo messageInfoVo : all) {
            Long id = messageInfoVo.getId();
            List<MessageInfoVo> children = new ArrayList<>(messageInfoDao.findByParentId(id));
            messageInfoVo.setChildren(children);
        }
        return all;
    }

    public PageInfo<MessageInfoVo> findPage(String name, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        List<MessageInfoVo> all = findAllPage(request, name);
        return PageInfo.of(all);
    }

    public List<MessageInfoVo> findAllPage(HttpServletRequest request, String name) {
		return messageInfoDao.findByName(name);
    }

    /**
     * 产出该评论下的所有评论
     * @param parentId
     * @return
     */
    public int findByParentId(Long parentId) {

        //根据parentId查询所有自评论集合
        List<MessageInfoVo> list = messageInfoDao.findByParentId(parentId);
        for (MessageInfoVo messageInfoVo : list) {
            System.out.println(messageInfoVo);
            System.out.println(messageInfoVo.getId());
        }

        int count=0;
        //删除所哟普子集评论
        for (MessageInfoVo messageInfoVo : list) {
            messageInfoDao.deleteByPrimaryKey(messageInfoVo.getId());
            count++;
        }
        //删除自己的第一级评论
        messageInfoDao.deleteByPrimaryKey(parentId);
//        System.out.println("删除了:"+list.size());
//        System.out.println("count:"+(++count));
        return list.size()+1;
    }
}
