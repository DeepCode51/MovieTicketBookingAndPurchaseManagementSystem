package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.example.dao.CommentInfoDao;
import com.example.dao.GoodsInfoDao;
import com.example.entity.CommentInfo;
import com.example.entity.GoodsInfo;
import com.example.entity.TypeInfo;
import com.example.entity.Account;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsInfoService {

    @Resource
    private GoodsInfoDao goodsInfoDao;
    @Resource
    private TypeInfoService typeInfoService;
	@Resource
	private AdminInfoService adminInfoService;
	@Resource
	private UserInfoService userInfoService;

    @Resource
    private NxSystemFileInfoService nxSystemFileInfoService;
    @Resource
    private CommentInfoDao commentInfoDao;

    public GoodsInfo add(GoodsInfo goodsInfo) {
        List<Long> fileList = goodsInfo.getFileList();
        if (!CollectionUtil.isEmpty(fileList)) {
            goodsInfo.setFileIds(fileList.toString());
        }
        goodsInfoDao.insertSelective(goodsInfo);
        return goodsInfo;
    }

    public void delete(Long id) {
        goodsInfoDao.deleteByPrimaryKey(id);
    }

    public void update(GoodsInfo goodsInfo) {
        List<Long> fileList = goodsInfo.getFileList();
        if (!CollectionUtil.isEmpty(fileList)) {
            goodsInfo.setFileIds(fileList.toString());
        }
        if (goodsInfo.getHot() != null) {
            GoodsInfo goods = findById(goodsInfo.getId());
            goodsInfo.setHot(goods.getHot() + goodsInfo.getHot());
        }
        goodsInfoDao.updateByPrimaryKeySelective(goodsInfo);
    }

    public GoodsInfo findById(Long id) {
        GoodsInfo goodsInfo = goodsInfoDao.selectByPrimaryKey(id);
        getRelInfo(goodsInfo);
        return goodsInfo;
    }

    public List<GoodsInfo> findAll() {

//        //查询到所有商品信息
        List<GoodsInfo> goodsInfos = goodsInfoDao.selectAll();
//        //遍历已失效的电影，即beginTime已超过本地现在时间
//        ArrayList<GoodsInfo> goods = new ArrayList<>();
//        for (GoodsInfo goodsInfo : goodsInfos) {
//            boolean after = LocalDateTime.now().isAfter(goodsInfo.getBeginTime());
//            if (!after){
//                goods.add(goodsInfo);
//            }
//        }
//        return goods;
        return goodsInfos;
    }

    public PageInfo<GoodsInfo> findPage(Integer pageNum, Integer pageSize, String name, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        Account user = (Account) request.getSession().getAttribute("user");
        List<GoodsInfo> all;
        if (user.getLevel() == 1) {
            all = goodsInfoDao.findByNameAndUserId(name, null, null);
        } else {
            all = goodsInfoDao.findByNameAndUserId(name, user.getId(), user.getLevel());
        }
        for (GoodsInfo goodsInfo : all) {
            getRelInfo(goodsInfo);
        }
        return PageInfo.of(all);
    }

    public List<GoodsInfo> findByType(Integer typeId) {

        List<GoodsInfo> list = goodsInfoDao.findByType(typeId);

        List<GoodsInfo> goodsInfos = new ArrayList<>();

        //遍历已失效的电影，即beginTime已超过本地现在时间
        for (GoodsInfo goodsInfo : list) {
            boolean after = LocalDateTime.now().isAfter(goodsInfo.getBeginTime());
            if (!after){
                goodsInfos.add(goodsInfo);
            }
        }
        return goodsInfos;
    }

    public PageInfo<GoodsInfo> findRecommendGoods(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GoodsInfo> list = goodsInfoDao.findRecommendGoods();

        List<GoodsInfo> goodsInfos = new ArrayList<>();

        //遍历已失效的电影，即beginTime已超过本地现在时间
        for (GoodsInfo goodsInfo : list) {
            boolean after = LocalDateTime.now().isAfter(goodsInfo.getBeginTime());
            if (!after){
                goodsInfos.add(goodsInfo);
            }
        }

        return PageInfo.of(goodsInfos);
    }

    public PageInfo<GoodsInfo> findHotSalesGoods(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GoodsInfo> list = goodsInfoDao.findHotSalesGoods();

        List<GoodsInfo> goodsInfos = new ArrayList<>();

        //遍历已失效的电影，即beginTime已超过本地现在时间
        for (GoodsInfo goodsInfo : list) {
            boolean after = LocalDateTime.now().isAfter(goodsInfo.getBeginTime());
            if (!after){
                goodsInfos.add(goodsInfo);
            }
        }

        return PageInfo.of(goodsInfos);
    }

    private void getRelInfo(GoodsInfo goodsInfo) {
        if (goodsInfo == null) {
            return;
        }
        TypeInfo typeInfo = typeInfoService.findById(goodsInfo.getTypeId());
        if (typeInfo != null) {
            goodsInfo.setTypeName(typeInfo.getName());
        }
        Account userInfo = getUserInfo(goodsInfo.getUserId(), goodsInfo.getLevel());
        if (userInfo != null) {
            goodsInfo.setUserName(userInfo.getName());
        }

//            NxSystemFileInfo fileInfo = nxSystemFileInfoService.findById(Long.parseLong(goodsInfo.getFileId()));
//            if (fileInfo != null) {
//                goodsInfo.setFileName(fileInfo.getOriginName());
//        }

    }

    /**
     * 查询用户买过的所有商品
     *
     * @param userId
     * @return
     */
    public List<GoodsInfo> getOrderGoods(Long userId, Integer level) {
        List<GoodsInfo> list = goodsInfoDao.getOrderGoods(userId, level);
        for (GoodsInfo goodsInfo : list) {
            List<CommentInfo> commentInfoList = commentInfoDao.findByGoodsIdAndUserId(goodsInfo.getId(), userId, level);
            if (CollUtil.isEmpty(commentInfoList)) {
                goodsInfo.setCommentStatus("未评价");
            } else {
                goodsInfo.setCommentStatus("已评价");
            }
        }
        return list;
    }

    public List<GoodsInfo> searchGoods(String text) {
        List<GoodsInfo> list = goodsInfoDao.findByText(text);

        List<GoodsInfo> goodsInfos = new ArrayList<>();

        //遍历已失效的电影，即beginTime已超过本地现在时间
        for (GoodsInfo goodsInfo : list) {
            boolean after = LocalDateTime.now().isAfter(goodsInfo.getBeginTime());
            if (!after){
                goodsInfos.add(goodsInfo);
            }
        }
        return goodsInfos;
    }

    private Account getUserInfo(Long userId, Integer level) {
        Account account = new Account();
		if (level == 1) {
			account = adminInfoService.findById(userId);
		}
		if (level == 2) {
			account = userInfoService.findById(userId);
		}

        return account;
    }


    public void upAndDownShow(Long id) {
        GoodsInfo goodsInfo = goodsInfoDao.selectByPrimaryKey(id);
        Integer isShow = goodsInfo.getIsShow();
        if (isShow == 1){
            goodsInfo.setIsShow(0);
        }else {
            goodsInfo.setIsShow(1);
        }
         goodsInfoDao.updateByPrimaryKey(goodsInfo);
    }
}
