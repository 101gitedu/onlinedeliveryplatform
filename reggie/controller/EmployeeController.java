package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * HttpServletRequest：专门用于封装 HTTP 请求消息
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名查询数据库
        //创建查询条件构造器，用于多表条件查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //"实体类::查询字段", "条件值"，判断是否相同
        queryWrapper.eq(Employee :: getUsername, employee.getUsername());
        //getOne：是一个延迟加载方法，它并不立即访问数据库，而是返回一个代理对象，这个代理对象是对实体对象的引用，
        // 仅在使用代理对象访问对象属性时才会去真正访问数据库 ，如果找不到，则抛出EntityNotFoundException报错
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败
        if (emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果已禁用状态，则返回员工已禁用
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功
        //getSession()：获取一个会话
        //setAttribute：保持数据
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1、清理Session中的用户ID
        request.getSession().removeAttribute("employee");
        //2、返回结果
        return R.success("退出成功");
    }

    /**
     *添加员工
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息：{}", employee.toString());

        //设置初始密码123456，需进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建、修改时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获取当前登录用户的ID，用于查看谁添加该员工
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        //StringUtils.isNotEmpty(name)：判断传入的name是否为null，是则不执行过滤条件
        //否，则判断传入的name与数据库的数据作比较
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee :: getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee :: getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据ID修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        //修改员工
        employeeService.updateById(employee);
        return R.success("员工信息修改员工");
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息");
    }
}
