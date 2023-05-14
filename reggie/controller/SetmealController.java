package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *套餐
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行分页查询
        setmealService.page(pageInfo, queryWrapper);

        /*用于显示套餐分类信息*/
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        //获取存储Setmeal对象的集合
        List<Setmeal> records = pageInfo.getRecords();
        //遍历集合得到每一个Setmeal对象
        List<SetmealDto> list = records.stream().map(item -> {
            //创建Setmeal对象
            SetmealDto setmealDto = new SetmealDto();
            //将Setmeal对象中的数据拷贝到SetmealDto对象中
            BeanUtils.copyProperties(item, setmealDto);

            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                String categoryName = category.getName();
                //将套餐分类名称赋值给SetmealDto中的CategoryName属性中
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //将赋值的list集合重新赋值给SetmealDto的Page的Records集合中
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 根据id删除套餐信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("根据id删除套餐信息：{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

//    /**
//     * 根据id回显套餐信息
//     * @param id
//     * @return
//     */
//    @GetMapping("/{id}")
//    public R<String> getById(@PathVariable Long id){
//        log.info("根据id修改套餐信息：{}", id);
//        //SetmealDto setmealDto = setmealService.getByIdWithDish(id);
//        //return R.success(setmealDto);
//        return R.success("id.......");
//    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 根据id修改(批量停启)售卖状态
     * @param id
     * @param ids
     * @return
     */
    @PostMapping("/status/{id}")
    public R<String> status(@PathVariable Integer id, @RequestParam List<Long> ids) {
        for (Long aLong : ids) {
            Setmeal setmeal = setmealService.getById(aLong);

            if (id != 1) {
                setmeal.setStatus(0);
            } else {
                setmeal.setStatus(1);
            }

            setmealService.updateById(setmeal);
        }

        return R.success("售卖状态修改成功");
    }
}
