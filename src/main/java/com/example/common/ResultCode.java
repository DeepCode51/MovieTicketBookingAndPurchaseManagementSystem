package com.example.common;

/**
 * 返回的状态码及状态信息
 */
public enum ResultCode {
    SUCCESS(0, "成功"),
    ERROR(-1, "系统异常"),
    PARAM_ERROR(1001, "参数异常"),
    USER_EXIST_ERROR(2001, "用户已存在"),
    USER_EXIST_EMAIL_ERROR(2008,"邮箱已被注册"),
    USER_ACCOUNT_ERROR(2002, "账号或密码错误"),
    USER_NOT_EXIST_ERROR(2003, "未找到用户"),
    USER_IS_BAN_LOGINERROR(2009,"用户被禁用，请与管理员联系"),
    COMFIRM_CODE_ERROR(2007, "连接已失效，请重新注册!"),
    ORDER_PAY_ERROR(3001, "库存不足，下单失败"),
    PARAM_LOST_ERROR(2004, "参数缺失"),
    PARAM_PASSWORD_ERROR(2005, "原密码输入错误"),
    EXIST_NOT_VALID_ERROR(2006, "账号已注册，但还未激活，请前往邮箱查看!"),
    DELETE_MESSAGE_ERROR(2009,"删除评论失败"),
    ACCOUNT_INFO_ERROR(2010,"输入的信息不正确,请重新输入或前往与管理员联系!"),
//    GLOABEL_ERROE(2020,"")
    ;

    public Integer code;
    public String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
