package com.itheima.reggie.common;

/**
 * //ThreadLocal：是Thread的局部变量，当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的
 *              变量副本，所以每个线程都可以独立地改变自已的副本，而不会影响其它线程所对应的副本。TreadLocal为每个
 *              线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取对应的值，线程外则不能访问
 *
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
