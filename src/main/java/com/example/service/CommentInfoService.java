package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.dao.CommentInfoDao;
import com.example.entity.Account;
import com.example.entity.CommentInfo;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class CommentInfoService {

    @Resource
    private CommentInfoDao commentInfoDao;
	@Resource
	private AdminInfoService adminInfoService;
	@Resource
	private UserInfoService userInfoService;


    public CommentInfo add(CommentInfo commentInfo) {
        commentInfo.setCreateTime(LocalDateTime.now());
//        commentInfo.setCreateTime(DateUtil.formatDateTime(new Date()));
        String content = commentInfo.getContent();
        if (content.length() > 255) {
            commentInfo.setContent(content.substring(0, 250));
        }
        commentInfoDao.insertSelective(commentInfo);
        return commentInfo;
    }

    public void delete(Long id) {
        commentInfoDao.deleteByPrimaryKey(id);
    }

    public void update(CommentInfo commentInfo) {
        String content = commentInfo.getContent();
        //内容过多，截取【0，255】范围的评论
        if (content.length() > 255) {
            commentInfo.setContent(content.substring(0, 250));
        }
        commentInfoDao.updateByPrimaryKeySelective(commentInfo);
    }

    public CommentInfo findById(Long id) {
        return commentInfoDao.selectByPrimaryKey(id);
    }

    public List<CommentInfo> findAll() {
        return commentInfoDao.selectAll();
    }

    public List<CommentInfo> findAll(Long goodsId) {
        List<CommentInfo> list = commentInfoDao.findByGoodsId(goodsId);
        if (!CollectionUtil.isEmpty(list)) {
            for (CommentInfo info : list) {
                Long userId = info.getUserId();
                Integer level = info.getLevel();
				if (level == 1) {
					info.setUserName(adminInfoService.findById(userId).getName());
				}
				if (level == 2) {
					info.setUserName(userInfoService.findById(userId).getName());
				}

            }
        }
        return list;
    }

    public PageInfo<CommentInfo> findPage(Integer pageNum, Integer pageSize, String name, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (account == null) {
            throw new CustomException(1001, "请先登录");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<CommentInfo> all = commentInfoDao.findByContent(name, account.getLevel());
        return PageInfo.of(all);
    }

}
