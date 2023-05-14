package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;

//IService：是对BaseMapper的扩展，使能够调用BaseMapper中的方法

public interface EmployeeService extends IService<Employee> {

}
