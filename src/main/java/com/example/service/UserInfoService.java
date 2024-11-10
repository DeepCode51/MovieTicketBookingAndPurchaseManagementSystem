package com.example.service;

import com.example.common.Result;
import com.example.dao.UserInfoDao;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.entity.UserInfo;
import com.example.exception.CustomException;
import com.example.common.ResultCode;
import com.example.vo.UserInfoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.crypto.SecureUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserInfoService {

    @Resource
    private UserInfoDao userInfoDao;

    public UserInfo add(UserInfo userInfo) {
        // 唯一校验
        int count = userInfoDao.checkRepeat("name", userInfo.getName(), null);
        if (count > 0) {
            throw new CustomException(1001, "用户名\"" + userInfo.getName() + "\"已存在");
        }
        if (StringUtils.isEmpty(userInfo.getPassword())) {
            // 默认密码123456
            userInfo.setPassword(SecureUtil.md5("123456"));
        } else {
            userInfo.setPassword(SecureUtil.md5(userInfo.getPassword()));
        }
        userInfoDao.insertSelective(userInfo);
        return userInfo;
    }

    public void delete(Long id) {
        userInfoDao.deleteByPrimaryKey(id);
    }

    public void update(UserInfo userInfo) {
        userInfoDao.updateByPrimaryKeySelective(userInfo);
    }

    public UserInfo findById(Long id) {
        return userInfoDao.selectByPrimaryKey(id);
    }

    public List<UserInfoVo> findAll() {
        return userInfoDao.findByName("all");
    }

    public PageInfo<UserInfoVo> findPage(String name, Integer pageNum, Integer pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserInfoVo> all = userInfoDao.findByName(name);
        return PageInfo.of(all);
    }

    public UserInfoVo findByUserName(String name) {
        return userInfoDao.findByUsername(name);
    }

    public UserInfo login(String username, String password) {
        UserInfo userInfo = userInfoDao.findByUsername(username);

        Integer isBan = userInfo.getIsBan();
//        System.out.println("isBan:"+isBan);
        if (isBan==1){

            throw new CustomException(ResultCode.USER_IS_BAN_LOGINERROR);
        }
        if (userInfo == null) {
                throw new CustomException(ResultCode.USER_ACCOUNT_ERROR);
            }
        if (!SecureUtil.md5(password).equalsIgnoreCase(userInfo.getPassword())) {
            throw new CustomException(ResultCode.USER_ACCOUNT_ERROR);
        }
        return userInfo;

    }

    public UserInfo findByEmail(String email) {
        UserInfo userInfo = userInfoDao.findByEmail(email);
        return userInfo;
    }

    public Result activationAccount(String confirmCode) {
        UserInfo user = userInfoDao.findByConfirmCode(confirmCode);
        System.out.println(user);
        //判断链接是否失效
        boolean after = LocalDateTime.now().isAfter(user.getActivationTime());
        //验证码失效
        if (after){
            return Result.error(ResultCode.COMFIRM_CODE_ERROR.code,ResultCode.COMFIRM_CODE_ERROR.msg);
        }

        //成功，更新激活状态
        user.setIsValid(1);
        int result = userInfoDao.updateByPrimaryKey(user);
        return Result.success("恭喜你，账号激活成功!");
    }

    public UserInfo addOldOne(UserInfo info) {
        //删除旧数据的没激活过的数据
        int delete = userInfoDao.deleteOldUser(info.getEmail());
        System.out.println(delete);
        //添加新的数据
        int i = userInfoDao.insertSelective(info);
        if (i>0){
            return info;
        }
        return null;
    }

    /**
     * 更改用户封禁状态
     * @param id
     * @return
     */
    public UserInfo updateBanStatus(Long id) {
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(id);
        Integer isBan = userInfo.getIsBan();
        if (isBan == 1){
            userInfo.setIsBan(0);
        }else {
            userInfo.setIsBan(1);
        }
        int i = userInfoDao.updateByPrimaryKey(userInfo);
        if (i >=0){
            return userInfo;
        }
        return null;
    }

    public UserInfo findPassword(String name, String email, Integer level) {
        return userInfoDao.findPassword(name, email, level);
    }
}
