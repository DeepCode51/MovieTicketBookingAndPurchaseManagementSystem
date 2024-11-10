package com.example.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.Account;
import com.example.entity.AuthorityInfo;
import com.example.exception.CustomException;
import com.example.entity.AdminInfo;
import com.example.entity.UserInfo;

import com.example.service.AdminInfoService;
import com.example.service.MailService;
import com.example.service.UserInfoService;

import com.example.vo.AdminInfoVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import cn.hutool.json.JSONUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountController {

//    @Value("${authority.info}")
    private String authorityStr="[{\"models\":[{\"modelId\":1,\"operation\":[1,2,3,4]},{\"modelId\":1000001,\"operation\":[1,2,3,4]},{\"modelId\":1000003,\"operation\":[1,2,3,4]},{\"modelId\":2,\"operation\":[1,2,3,4]},{\"modelId\":3,\"operation\":[1,2,3,4]},{\"modelId\":4,\"operation\":[1,2,3,4]},{\"modelId\":10000014,\"operation\":[1,2,3,4]},{\"modelId\":1000015,\"operation\":[1,2,3,4]},{\"modelId\":1000016,\"operation\":[1,2,3,4]},{\"modelId\":1000017,\"operation\":[1,2,3,4]},{\"modelId\":1000018,\"operation\":[1,2,3,4]}],\"level\":1,\"modelId\":1,\"name\":\"管理员\"},{\"models\":[{\"modelId\":1000001,\"operation\":[4]},{\"modelId\":1000003,\"operation\":[4]},{\"modelId\":2,\"operation\":[4]},{\"modelId\":4,\"operation\":[4]},{\"modelId\":10000014,\"operation\":[4]},{\"modelId\":1000015,\"operation\":[4]},{\"modelId\":1000016,\"operation\":[4]},{\"modelId\":1000017,\"operation\":[4]},{\"modelId\":1000018,\"operation\":[4]}],\"level\":2,\"modelId\":3,\"name\":\"用户\"}]";

    @Resource
    private AdminInfoService adminInfoService;
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private MailService mailService;

    /**
     * 登录
     *
     * @param account
     * @param request
     * @return 由于登录时有两个角色，用来区分(1为管理员，2为用户)
     */
    @PostMapping("/login")
    public Result<Account> login(@RequestBody Account account, HttpServletRequest request) {
        if (StrUtil.isBlank(account.getName()) || StrUtil.isBlank(account.getPassword()) || account.getLevel() == null) {
            throw new CustomException(ResultCode.PARAM_LOST_ERROR);
        }
        Integer level = account.getLevel();
        Account login = new Account();

        if (1 == level) {
            //调用管理员的登录方法
            login = adminInfoService.login(account.getName(), account.getPassword());
        }
        //调用用户的登录方法
        if (2 == level) {
            login = userInfoService.login(account.getName(), account.getPassword());
        }

        //将用户信息存储到session
        request.getSession().setAttribute("user", login);
//        System.out.println("???"+request.getSession().getAttribute("user"));
        return Result.success(login);
    }

//    //注册
    @PostMapping("/register")
    public Result register(@RequestBody Account account, HttpServletRequest request) {
        //获取用户等级(管理员还是用户)
        Integer level = account.getLevel();
        //先判断邮箱是否被注册
        String email = account.getEmail();
        //用户名
        String name = account.getName();

        AdminInfo a = new AdminInfo();
        UserInfo u = new UserInfo();

        //生成激活确认码
        String confirmCode = IdUtil.getSnowflake(1, 1).nextIdStr();
        //设置过期时间
        LocalDateTime ldt = LocalDateTime.now().plusDays(1);
        if (1 == level) {
            //管理员
            AdminInfo adminInfo = adminInfoService.findByEmail(email);
            if (adminInfo != null) {
                //该邮箱在数据库中已存储,先查看该账号是否被激活
                Integer isValid = adminInfo.getIsValid();
                if (isValid == 1){
                    //账号已激活，提示账号已被注册
                    throw new CustomException(ResultCode.USER_EXIST_EMAIL_ERROR);
                }else {
                    //该邮箱在数据库中存储，但未激活，查看链接失效时间
                    boolean after = LocalDateTime.now().isAfter(adminInfo.getActivationTime());
                    if (!after) {
                        //账号注册过，但未激活，请前往邮箱查看
                        throw new CustomException(ResultCode.EXIST_NOT_VALID_ERROR);
                    }else {
                        //激活链接失效，可以重新注册
                        AdminInfo info = new AdminInfo();
                        BeanUtils.copyProperties(account, info);
                        info.setConfirmCode(confirmCode);
                        info.setActivationTime(ldt);
                        info.setIsValid(0);

                        a = adminInfoService.addOldOne(info);
                        if (a != null ) {
                            String activationUrl = "http://localhost:8888/adminInfo/activation?confirmCode=" + confirmCode;
                            mailService.sendMailForActivaitionAccount(activationUrl, email);

                            return Result.success("注册成功，请前往邮箱进行账号激活");
                        }
                    }
                }
            } else {
                //邮箱没被注册，判断用户名是否备用
//                AdminInfoVo adminName = adminInfoService.findByUserName(name);
//                if (adminName==null){
//                    throw new CustomException(ResultCode.USER_EXIST_ERROR);
//                }
                //可以注册,发哦送短信
                AdminInfo info = new AdminInfo();
                BeanUtils.copyProperties(account, info);
                info.setConfirmCode(confirmCode);
                info.setActivationTime(ldt);
                info.setIsValid(0);

                a = adminInfoService.add(info);
                if (a != null ) {
                    String activationUrl = "http://localhost:8888/adminInfo/activation?confirmCode=" + confirmCode;
                    mailService.sendMailForActivaitionAccount(activationUrl, email);

                    return Result.success("注册成功，请前往邮箱进行账号激活");
                }
            }

        }
        if (2 == level) {
            //用户
            UserInfo userInfo = userInfoService.findByEmail(email);
            if (userInfo != null) {
                //该邮箱在数据库中已存储,先查看该账号是否被激活
                Integer isValid = userInfo.getIsValid();
                if (isValid == 1){
                    //账号已激活，提示账号已被注册
                    throw new CustomException(ResultCode.USER_EXIST_EMAIL_ERROR);
                }else {
                    //该邮箱在数据库中存储，但未激活，查看链接失效时间
                    boolean after = LocalDateTime.now().isAfter(userInfo.getActivationTime());
                    if (!after) {
                        //账号注册过，但未激活，请前往邮箱查看
                        throw new CustomException(ResultCode.EXIST_NOT_VALID_ERROR);
                    }else {
                        //激活链接失效，可以重新注册
                        UserInfo info = new UserInfo();
                        BeanUtils.copyProperties(account, info);
                        info.setConfirmCode(confirmCode);
                        info.setActivationTime(ldt);
                        info.setIsValid(0);

                        u = userInfoService.addOldOne(info);
                        if (u != null ) {
                            String activationUrl = "http://localhost:8888/userInfo/activation?confirmCode=" + confirmCode;
                            mailService.sendMailForActivaitionAccount(activationUrl, email);

                            return Result.success("注册成功，请前往邮箱进行账号激活");
                        }
                    }
                }
            } else {
                //邮箱没被注册，判断用户名是否备用
//                AdminInfoVo adminName = adminInfoService.findByUserName(name);
//                if (adminName==null){
//                    throw new CustomException(ResultCode.USER_EXIST_ERROR);
//                }
                //可以注册,发哦送短信
                UserInfo info = new UserInfo();
                BeanUtils.copyProperties(account, info);
                info.setConfirmCode(confirmCode);
                info.setActivationTime(ldt);
                info.setIsValid(0);

                u = userInfoService.add(info);
                if (u != null ) {
                    String activationUrl = "http://localhost:8888/userInfo/activation?confirmCode=" + confirmCode;
                    mailService.sendMailForActivaitionAccount(activationUrl, email);

                    return Result.success("注册成功，请前往邮箱进行账号激活");
                }
            }
//
//            if (userInfo != null) {
//                throw new CustomException(ResultCode.USER_EXIST_EMAIL_ERROR);
//            } else {
//                //邮箱没被注册，判断用户名是否备用
////                UserInfo userName = userInfoService.findByUserName(name);
////                if (userName==null){
////                    throw new CustomException(ResultCode.USER_EXIST_ERROR);
////                }
//                //可以注册,发送短信
//                UserInfo info = new UserInfo();
//                info.setConfirmCode(confirmCode);
//                info.setActivationTime(ldt);
//                info.setIsValid(0);
//                BeanUtils.copyProperties(account, info);
//                u = userInfoService.add(info);
//                if (u != null) {
//                    String activationUrl = "http://localhost:8888/userInfo/activation?confirmCode=" + confirmCode;
//                    mailService.sendMailForActivaitionAccount(activationUrl, email);
//
//                    return Result.success("注册成功，请前往邮箱进行账号激活");
//                }
//            }
        }

        return Result.error(1009,"注册失败");
    }



    @GetMapping("/logout")
    public Result logout(HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        return Result.success();
    }

    @GetMapping("/auth")
    public Result getAuth(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        if(user == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }

    @GetMapping("/getAccountInfo")
    public Result<Object> getAccountInfo(HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (account == null) {
            return Result.success(new Object());
        }
        Integer level = account.getLevel();
		if (1 == level) {
			return Result.success(adminInfoService.findById(account.getId()));
		}
		if (2 == level) {
			return Result.success(userInfoService.findById(account.getId()));
		}

        return Result.success(new Object());
    }

    @GetMapping("/getSession")
    public Result<Map<String, String>> getSession(HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (account == null) {
            return Result.success(new HashMap<>(1));
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("username", account.getName());
        return Result.success(map);
    }

    @GetMapping("/getAuthority")
    public Result<List<AuthorityInfo>> getAuthorityInfo() {
        List<AuthorityInfo> authorityInfoList = JSONUtil.toList(JSONUtil.parseArray(authorityStr), AuthorityInfo.class);
        return Result.success(authorityInfoList);
    }

    /**
    * 获取当前用户所能看到的模块信息
    * @param request
    * @return
    */
    @GetMapping("/authority")
    public Result<List<Integer>> getAuthorityInfo(HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute("user");
        if (user == null) {
            return Result.success(new ArrayList<>());
        }
        JSONArray objects = JSONUtil.parseArray(authorityStr);
        for (Object object : objects) {
            JSONObject jsonObject = (JSONObject) object;
            if (user.getLevel().equals(jsonObject.getInt("level"))) {
                JSONArray array = JSONUtil.parseArray(jsonObject.getStr("models"));
                List<Integer> modelIdList = array.stream().map((o -> {
                    JSONObject obj = (JSONObject) o;
                    return obj.getInt("modelId");
                    })).collect(Collectors.toList());
                return Result.success(modelIdList);
            }
        }
        return Result.success(new ArrayList<>());
    }

    /**
     * 获取用户权限
     * @param modelId
     * @param request
     * @return
     */
    @GetMapping("/permission/{modelId}")
    public Result<List<Integer>> getPermission(@PathVariable Integer modelId, HttpServletRequest request) {
        List<AuthorityInfo> authorityInfoList = JSONUtil.toList(JSONUtil.parseArray(authorityStr), AuthorityInfo.class);
        Account user = (Account) request.getSession().getAttribute("user");
        if (user == null) {
            return Result.success(new ArrayList<>());
        }
        Optional<AuthorityInfo> optional = authorityInfoList.stream().filter(x -> x.getLevel().equals(user.getLevel())).findFirst();
        if (optional.isPresent()) {
            Optional<AuthorityInfo.Model> firstOption = optional.get().getModels().stream().filter(x -> x.getModelId().equals(modelId)).findFirst();
            if (firstOption.isPresent()) {
                List<Integer> info = firstOption.get().getOperation();
                return Result.success(info);
            }
        }
        return Result.success(new ArrayList<>());
    }

    /**
     * 更改密码
     * @param info
     * @param request
     * @return
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account info, HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (account == null) {
            return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
        }
        String oldPassword = SecureUtil.md5(info.getPassword());
        if (!oldPassword.equals(account.getPassword())) {
            return Result.error(ResultCode.PARAM_PASSWORD_ERROR.code, ResultCode.PARAM_PASSWORD_ERROR.msg);
        }
        info.setPassword(SecureUtil.md5(info.getNewPassword()));
        Integer level = account.getLevel();
		if (1 == level) {
			AdminInfo adminInfo = new AdminInfo();
			BeanUtils.copyProperties(info, adminInfo);
			adminInfoService.update(adminInfo);
		}
		if (2 == level) {
			UserInfo userInfo = new UserInfo();
			BeanUtils.copyProperties(info, userInfo);
			userInfoService.update(userInfo);
		}

        info.setLevel(level);
        info.setName(account.getName());
        // 清空session，让用户重新登录
        request.getSession().setAttribute("user", null);
        return Result.success();
    }

//    @PostMapping("/resetPassword")
//    public Result resetPassword(@RequestBody Account account) {
//        Integer level = account.getLevel();
//		if (1 == level) {
//			AdminInfo info = adminInfoService.findByUserName(account.getName());
//			if (info == null) {
//				return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
//			}
//			info.setPassword(SecureUtil.md5("123456"));
//			adminInfoService.update(info);
//		}
//		if (2 == level) {
//			UserInfo info = userInfoService.findByUserName(account.getName());
//			if (info == null) {
//				return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
//			}
//			info.setPassword(SecureUtil.md5("123456"));
//			userInfoService.update(info);
//		}
//
//        return Result.success();
//    }



    public void resetPassword(Account account) {
        Integer level = account.getLevel();
        if (1 == level) {
            AdminInfo info = adminInfoService.findByUserName(account.getName());
            info.setPassword(SecureUtil.md5("123456"));
            adminInfoService.update(info);
        }
        if (2 == level) {
            UserInfo info = userInfoService.findByUserName(account.getName());
            info.setPassword(SecureUtil.md5("123456"));
            userInfoService.update(info);
        }
    }

    @PostMapping("/findMyPassword")
    public Result findPassword(@RequestBody Account account){
        //获取是管理员还是用户
        Integer level = account.getLevel();
        //管理员
        if (1==level){
            AdminInfo adminInfo = adminInfoService.findPassword(account.getName(),account.getEmail(),account.getLevel());
            if (adminInfo==null){
                return Result.error(ResultCode.ACCOUNT_INFO_ERROR.code, ResultCode.ACCOUNT_INFO_ERROR.msg);
            }
//            return Result.success("信息正确");

        }
        //用户
        if (2==level){
            UserInfo userInfo = userInfoService.findPassword(account.getName(),account.getEmail(),account.getLevel());
            if (userInfo==null){
                return Result.error(ResultCode.ACCOUNT_INFO_ERROR.code, ResultCode.ACCOUNT_INFO_ERROR.msg);
            }

        }
        //重置密码
        resetPassword(account);
        return Result.success("重置密码成功!");


    }
}
