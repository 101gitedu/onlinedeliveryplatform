package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//BaseMapper主要是集成了一些基本的CRUD的方法，不用再写mapper.xml文件来进行对数据库操作

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
