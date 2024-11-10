package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.common.ResultCode;
import com.example.dao.OrderGoodsRelDao;
import com.example.dao.OrderInfoDao;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderInfoService {

    @Resource
    private OrderInfoDao orderInfoDao;
    @Resource
    private GoodsInfoService goodsInfoService;
	@Resource
	private AdminInfoService adminInfoService;
	@Resource
	private UserInfoService userInfoService;

    @Resource
    private OrderGoodsRelDao orderGoodsRelDao;
    @Resource
    private CartInfoService cartInfoService;


    /**
     * 根据id查询订单信息
     */
    public OrderInfo findById(Long id) {
        OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(id);
        packOrder(orderInfo);
        return orderInfo;
    }

    public List<OrderInfo> findAll(Long userId, Integer level) {
        List<OrderInfo> orderInfos = orderInfoDao.findByUserId(userId, level);
        for (OrderInfo orderInfo : orderInfos) {
            packOrder(orderInfo);
        }
        return orderInfos;
    }

    /**
     * 包装订单的用户和商品信息
     */
    private void packOrder(OrderInfo orderInfo) {
        Long orderId = orderInfo.getId();
        List<OrderGoodsRel> rels = orderGoodsRelDao.findByOrderId(orderId);
        orderInfo.setUserInfo(getUserInfo(orderInfo.getUserId(), orderInfo.getLevel()));
        List<GoodsInfo> goodsList = CollUtil.newArrayList();
        orderInfo.setGoodsList(goodsList);
        for (OrderGoodsRel rel : rels) {
            GoodsInfo goodsDetailInfo = goodsInfoService.findById(rel.getGoodsId());
            if (goodsDetailInfo != null) {
                // 注意这里返回的count是用户加入商品的数量，而不是商品的库存
                goodsDetailInfo.setNum(rel.getCount());
                goodsList.add(goodsDetailInfo);
            }
        }
    }

    /**
     * 分页查询订单信息
     */
    public PageInfo<OrderInfo> findPages(Long userId, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomException(1001, "session已失效，请重新登录");
        }
        Integer level = user.getLevel();
        PageHelper.startPage(pageNum, pageSize);
        List<OrderInfo> orderInfos;
        if (1 == level) {
            orderInfos = orderInfoDao.selectAll();
        } else if (userId != null){
            orderInfos = orderInfoDao.findByEndUserId(userId, null, level);
        } else {
            orderInfos = new ArrayList<>();
        }
        for (OrderInfo orderInfo : orderInfos) {
            packOrder(orderInfo);
        }
        return PageInfo.of(orderInfos);
    }

    public PageInfo<OrderInfo> findFrontPages(Long userId, Integer level, Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderInfo> orderInfos;
        if (userId != null){
            orderInfos = orderInfoDao.findByEndUserId(userId, status, level);
        } else {
            orderInfos = new ArrayList<>();
        }
        for (OrderInfo orderInfo : orderInfos) {
            packOrder(orderInfo);
        }
        return PageInfo.of(orderInfos);
    }

    /**
     * 分页查询订单信息
     */
    public PageInfo<OrderInfo> findPages(Long userId, Integer level, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<OrderInfo> orderInfos;
        if (userId != null) {
            orderInfos = orderInfoDao.findByUserId(userId, level);
        } else {
            orderInfos = orderInfoDao.selectAll();
        }
        for (OrderInfo orderInfo : orderInfos) {
            packOrder(orderInfo);
        }
        return PageInfo.of(orderInfos);
    }

    /**
     * 下单
     */
    @Transactional
    public OrderInfo add(OrderInfo orderInfo) {
        Long userId = orderInfo.getUserId();
        Integer level = orderInfo.getLevel();
        // 订单id：用户id + 当前年月日时分 + 4位流水号
        String orderId = userId + DateUtil.format(new Date(), "yyyyMMddHHmm") + RandomUtil.randomNumbers(4);
        orderInfo.setOrderId(orderId);

        Account userInfo = getUserInfo(userId, level);
        orderInfo.setLinkAddress(userInfo.getAddress());
        orderInfo.setLinkMan(userInfo.getNickName());
        orderInfo.setLinkPhone(userInfo.getPhone());
        orderInfo.setCreateTime(LocalDateTime.now());
//        orderInfo.setCreateTime(DateUtil.formatDateTime(new Date()));
        // 生成订单
        orderInfoDao.insertSelective(orderInfo);

        List<GoodsInfo> goodsList = orderInfo.getGoodsList();
        for (GoodsInfo orderGoodsVO : goodsList) {
            Long goodsId = orderGoodsVO.getId();
            // 查询商品信息
            GoodsInfo goodsDetail = goodsInfoService.findById(goodsId);
            if (goodsDetail != null) {
                Integer orderCount = orderInfo.getTotal();
                // 销量 +count
                int sales = goodsDetail.getSales() == null ? 0 : goodsDetail.getSales();
                goodsDetail.setSales(sales + orderCount);
                goodsInfoService.update(goodsDetail);

                // 建立关系
                OrderGoodsRel orderGoodsRel = new OrderGoodsRel();
                orderGoodsRel.setGoodsId(goodsId);
                orderGoodsRel.setOrderId(orderInfo.getId());
                orderGoodsRel.setCount(orderCount);
                orderGoodsRelDao.insertSelective(orderGoodsRel);
            }
        }
        // 更新订单信息
        orderInfoDao.updateByPrimaryKeySelective(orderInfo);

        // 下单 清空购物车
        cartInfoService.empty(userId, level);
        return orderInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, Integer level, List<GoodsInfo> goodsList) {

        Account userInfo = getUserInfo(userId, level);

        for (GoodsInfo orderGoodsVO : goodsList) {
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setLinkAddress(userInfo.getAddress());
            orderInfo.setLinkMan(userInfo.getNickName());
            orderInfo.setLinkPhone(userInfo.getPhone());
            orderInfo.setCreateTime(LocalDateTime.now());
//            orderInfo.setCreateTime(DateUtil.formatDateTime(new Date()));
            orderInfo.setUserId(userId);
            orderInfo.setStatus(1);

            // 订单id：用户id + 当前年月日时分 + 4位流水号
            String orderId = userId + DateUtil.format(new Date(), "yyyyMMddHHmm") + RandomUtil.randomNumbers(4);
            orderInfo.setOrderId(orderId);

            Long goodsId = orderGoodsVO.getId();
            // 查询商品信息
            GoodsInfo goodsDetail = goodsInfoService.findById(goodsId);
            if (goodsDetail != null) {
                Integer orderCount = orderInfo.getTotal();
                // 销量 +count
                int sales = goodsDetail.getSales() == null ? 0 : goodsDetail.getSales();
                goodsDetail.setSales(sales + orderCount);
                goodsInfoService.update(goodsDetail);

                // 建立关系
                OrderGoodsRel orderGoodsRel = new OrderGoodsRel();
                orderGoodsRel.setGoodsId(goodsId);
                orderGoodsRel.setOrderId(orderInfo.getId());
                orderGoodsRel.setCount(orderCount);
                orderGoodsRelDao.insertSelective(orderGoodsRel);
            }
        }

        // 下单 清空购物车
        cartInfoService.empty(userId, level);
    }

    @Transactional
    public void delete(Long id) {
        orderInfoDao.deleteById(id);
        orderGoodsRelDao.deleteByOrderId(id);
    }

    public void deleteGoods(Long goodsId, Long orderId) {
        orderGoodsRelDao.deleteByGoodsIdAndOrderId(goodsId, orderId);
    }

    public OrderInfo findByOrderId(Long orderId) {
        return orderInfoDao.findById(orderId);
    }

    public void changeStatus(Long id, Integer status) {
        OrderInfo order = orderInfoDao.findById(id);
        Long userId = order.getUserId();
        Integer level = order.getLevel();
		if (level == 1) {
			AdminInfo user = adminInfoService.findById(userId);
			if (status == 2) {
			    //获取用户余额
				Double account = user.getAccount();
				//订单总价
				Double totalPrice = order.getTotalPrice();
				if(account < totalPrice) {
					throw new CustomException(-1, "账户余额不足");
				}
				//支付成功,更新账户余额
				user.setAccount(user.getAccount() - order.getTotalPrice());
				adminInfoService.update(user);
			}
//			if ("已退款".equals(status) || "已退货".equals(status)) {
            if (status == 5 || status == 6) {
				user.setAccount(user.getAccount() + order.getTotalPrice());
				adminInfoService.update(user);
			}
			orderInfoDao.updateStatus(id, status);
		}
		if (level == 2) {
			UserInfo user = userInfoService.findById(userId);
			if (status.equals(2)) {
				Double account = user.getAccount();
				Double totalPrice = order.getTotalPrice();
				if(account < totalPrice) {
					throw new CustomException(-1, "账户余额不足");
				}
				user.setAccount(user.getAccount() - order.getTotalPrice());
				userInfoService.update(user);
			}
//			if ("已退款".equals(status) || "已退货".equals(status)) {
            if (status == 5 || status == 6) {
				user.setAccount(user.getAccount() + order.getTotalPrice());
				userInfoService.update(user);
			}
			orderInfoDao.updateStatus(id, status);
		}

    }

    //查询成员信息
    public Account getUserInfo(Long userId, Integer level) {
        Account account = new Account();
        //管理员
		if (level == 1) {
			account = adminInfoService.findById(userId);
		}
		//用户
		if (level == 2) {
			account = userInfoService.findById(userId);
		}

        return account;
    }
}
