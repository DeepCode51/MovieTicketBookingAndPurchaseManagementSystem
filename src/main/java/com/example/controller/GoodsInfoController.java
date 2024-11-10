package com.example.controller;

import com.example.common.Result;
import com.example.entity.GoodsInfo;
import com.example.entity.Account;
import com.example.service.GoodsInfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 电影类
 */
@RestController
@RequestMapping(value = "/goodsInfo")
public class GoodsInfoController {
    @Resource
    private GoodsInfoService goodsInfoService;

    //添加商品
    @PostMapping
    public Result<GoodsInfo> add(@RequestBody GoodsInfo goodsInfo, HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute("user");
        //设置当前电影上架者（管理员）
        goodsInfo.setUserId(user.getId());
        //用户没权限，则直接设置1
        goodsInfo.setLevel(1);
//        goodsInfo.setLevel(user.getLevel());
        goodsInfoService.add(goodsInfo);
        return Result.success(goodsInfo);
    }

    //根据电影id删除某电影
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        goodsInfoService.delete(id);
        return Result.success();
    }

    //更新电影信息
    @PutMapping
    public Result update(@RequestBody GoodsInfo goodsInfo) {
        goodsInfoService.update(goodsInfo);
        return Result.success();
    }

    //根基id获取电影详细信息
    @GetMapping("/{id}")
    public Result<GoodsInfo> detail(@PathVariable Long id) {
        GoodsInfo goodsInfo = goodsInfoService.findById(id);
        return Result.success(goodsInfo);
    }

    //后台获取所有电影
    @GetMapping
    public Result<List<GoodsInfo>> all() {
        return Result.success(goodsInfoService.findAll());
    }

    //分页展示，根据电影名搜索电影
    @GetMapping("/page/{name}")
    public Result<PageInfo<GoodsInfo>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @PathVariable String name,
                                            HttpServletRequest request) {
        return Result.success(goodsInfoService.findPage(pageNum, pageSize, name, request));
    }

    //根据分类id查询相关电影
    @GetMapping("/findByType/{typeId}")
    public Result<List<GoodsInfo>> findByType(@PathVariable Integer typeId) {
        return Result.success(goodsInfoService.findByType(typeId));
    }

    //查询推荐内容
    @GetMapping("/recommend")
    public Result<PageInfo<GoodsInfo>> recommendGoods(@RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "6") Integer pageSize) {
        return Result.success(goodsInfoService.findRecommendGoods(pageNum, pageSize));
    }

    //查看热映电影（销售量高的）
    @GetMapping("/sales")
    public Result<PageInfo<GoodsInfo>> sales(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "6") Integer pageSize) {
        return Result.success(goodsInfoService.findHotSalesGoods(pageNum, pageSize));
    }

    /**
     * 上下架电影
     */
    @GetMapping("/upAndDownShow/{id}")
    public Result upAndDownShow(@PathVariable Long id){
        goodsInfoService.upAndDownShow(id);
        return Result.success("上架或下架电影成功");
    }

    /**
     * 查询用户买到过的所有商品
     * @return
     */
    @GetMapping("/comment/{userId}/{level}")
    public Result<List<GoodsInfo>> orderGoods(@PathVariable("userId") Long userId,
                                              @PathVariable("level") Integer level) {
        return Result.success(goodsInfoService.getOrderGoods(userId, level));
    }

    //根据电影名查询电影
    @GetMapping("/searchGoods")
    public Result<List<GoodsInfo>> searchGoods(@RequestParam String text) {
        return Result.success(goodsInfoService.searchGoods(text));
    }


}
