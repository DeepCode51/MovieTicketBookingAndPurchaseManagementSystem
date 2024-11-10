package com.example.controller;

import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.MessageInfo;
import com.example.service.MessageInfoService;
import com.example.vo.MessageInfoVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 交流区
 */
@RestController
@RequestMapping(value = "/messageInfo")
public class MessageInfoController {
    @Resource
    private MessageInfoService messageInfoService;

    //添加交流内容
    @PostMapping
    public Result<MessageInfo> add(@RequestBody MessageInfoVo messageInfo) {
//        System.out.println("信息："+messageInfo);
        messageInfoService.add(messageInfo);
        return Result.success(messageInfo);
    }

    /**
     * 删除子评论 或者 是后台删除某一评论
     * @param id
     * @return
     */
    //根据id删除交流内容
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        messageInfoService.delete(id);
        return Result.success();
    }

    /**
     * 用户自己发表的第一级评论,并且该评论下的所有评论
     */
    @GetMapping("/deleteParentMessage/{id}")
    public Result deleteMyMessage(@PathVariable Long id){
        //根据id查询内容
//        MessageInfo messageInfo = messageInfoService.findById(id);
//        //查询parentId是否为0
//        Long parentId = messageInfo.getParentId();
        //根据parentId查询所有子评论
        int count = messageInfoService.findByParentId(id);
        if (count>0){
            return Result.success("删除成功");
        }
        return Result.error(ResultCode.DELETE_MESSAGE_ERROR.code,ResultCode.DELETE_MESSAGE_ERROR.msg);
    }

    //更新交流内容
    @PutMapping
    public Result update(@RequestBody MessageInfoVo messageInfo) {
        messageInfoService.update(messageInfo);
        return Result.success();
    }


    @GetMapping("/{id}")
    public Result<MessageInfo> detail(@PathVariable Long id) {
        MessageInfo messageInfo = messageInfoService.findById(id);
        return Result.success(messageInfo);
    }

    //获取所有交流内容
    @GetMapping
    public Result<List<MessageInfoVo>> all() {
        return Result.success(messageInfoService.findAll());
    }

    //分页展示某用户下的交流内容
    @GetMapping("/page/{name}")
    public Result<PageInfo<MessageInfoVo>> page(@PathVariable String name,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                HttpServletRequest request) {
        return Result.success(messageInfoService.findPage(name, pageNum, pageSize, request));
    }



}
