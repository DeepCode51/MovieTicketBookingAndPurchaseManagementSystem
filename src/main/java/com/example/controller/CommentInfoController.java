package com.example.controller;

import com.example.common.Result;
import com.example.entity.CommentInfo;
import com.example.entity.Account;
import com.example.service.CommentInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/commentInfo")
public class CommentInfoController {
    @Resource
    private CommentInfoService commentInfoService;

    /*
        添加评论
     */
    @PostMapping
    public Result<CommentInfo> add(@RequestBody CommentInfo commentInfo, HttpServletRequest request) {
       //获取用户
        Account user = (Account) request.getSession().getAttribute("user");
        commentInfo.setUserId(user.getId());
        commentInfoService.add(commentInfo);
        return Result.success(commentInfo);
    }

    //根据评论id删除某评论
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        commentInfoService.delete(id);
        return Result.success();
    }

    //更改评论内容
    @PutMapping
    public Result update(@RequestBody CommentInfo commentInfo) {
        commentInfoService.update(commentInfo);
        return Result.success();
    }

    //查看评论内容
    @GetMapping("/{id}")
    public Result<CommentInfo> detail(@PathVariable Long id) {
        CommentInfo commentInfo = commentInfoService.findById(id);
        return Result.success(commentInfo);
    }

    //查询所有评论
    @GetMapping
    public Result<List<CommentInfo>> all() {
        return Result.success(commentInfoService.findAll());
    }

    //查询某商品的所有评论
    @GetMapping("/all/{goodsId}")
    public Result<List<CommentInfo>> all(@PathVariable("goodsId") Long goodsId) {
        return Result.success(commentInfoService.findAll(goodsId));
    }

    //系统后台管理员查询某个用户名下的所有评论内容
    @GetMapping("/page/{name}")
    public Result<PageInfo<CommentInfo>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @PathVariable String name,
                                              HttpServletRequest request) {
        return Result.success(commentInfoService.findPage(pageNum, pageSize, name, request));
    }
}
