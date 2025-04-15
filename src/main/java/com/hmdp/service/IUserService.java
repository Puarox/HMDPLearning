package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {



    public Result sendCode(String phone, HttpSession session);

    /**
     * 用户登录流
     * @return
     */
    public Result login(LoginFormDTO loginFormDTO,HttpSession session);
    Result sign();

    Result signCount();
}
