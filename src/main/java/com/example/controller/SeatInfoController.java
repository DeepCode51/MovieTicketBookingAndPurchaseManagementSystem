package com.example.controller;

import com.example.common.Result;
import com.example.entity.SeatInfo;
import com.example.service.SeatInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 座位
 */
@RestController
@RequestMapping("/seatInfo")
public class SeatInfoController {

    @Resource
    private SeatInfoService seatInfoService;

    //选中座位，购票成功添加座位到数据库
    @PostMapping()
    public Result add(@RequestBody SeatInfo seatInfo) {
        seatInfoService.save(seatInfo);
        return Result.success();
    }

    //获取座位的详细信息，空闲和已出售的作为
    @GetMapping("/detail")
    public Result<SeatInfo> findByUserId(@RequestParam Long goodsId) {
        return Result.success(seatInfoService.findDetail(goodsId));
    }
}
