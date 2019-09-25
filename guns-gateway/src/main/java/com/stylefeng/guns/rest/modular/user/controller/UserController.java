package com.stylefeng.guns.rest.modular.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference(interfaceClass = UserAPI.class)
    private UserAPI userAPI;

    @RequestMapping(value = "register",method = RequestMethod.POST)
    //@RequestMapping("register")
    public ResponseVO register(UserModel userModel){
        if (userModel.getUsername() == null || userModel.getUsername().trim().length() == 0){
            return ResponseVO.serviceFail("用户名不能为空");
        }
        if (userModel.getPassword() == null || userModel.getPassword().trim().length() == 0){
            return ResponseVO.serviceFail("密码不能为空");
        }
        if (userModel.getUsername().equals(userAPI.checkUsername(userModel.getUsername()))){
            return ResponseVO.serviceFail("用户名已存在，请换一个");
        }
        boolean isSuccess = userAPI.register(userModel);
        if (isSuccess){
            return ResponseVO.success("注册成功");
        }else {
            return ResponseVO.serviceFail("注册失败");
        }
    }

    //@RequestMapping(value = "check",method = RequestMethod.POST)
    @RequestMapping("check")
    public ResponseVO check(String username){
        if (username == null || username.trim().length() == 0){
            return ResponseVO.serviceFail("用户名不得为空");
        }
        boolean result = userAPI.checkUsername(username);
        if (result){
            return ResponseVO.success("验证成功");
        }else {
            return ResponseVO.serviceFail("用户已存在");
        }
    }

    @RequestMapping(value = "logout",method = RequestMethod.GET)
    public ResponseVO logout(){
        return ResponseVO.success("成功退出");
    }

    @RequestMapping(value = "getUserInfo",method = RequestMethod.GET)
    public ResponseVO getUserInfo(){
        //获取当前用户的id 进入数据库查询数据
        String userId = CurrentUser.getCurrentUser();
        if (userId != null && userId.trim().length() >0){
            UserInfoModel userInfo = userAPI.getUserInfo(Integer.parseInt(userId));
            if (userInfo != null){
                return ResponseVO.success(userInfo);
            }else {
                return ResponseVO.appFail("系统出现异常，请联系管理员");
            }
        }else {
            return ResponseVO.serviceFail("查询失败，用户尚未登陆");
        }
    }

    @RequestMapping(value = "updateUserInfo",method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoModel userInfoModel){
        //获取当前用户的id
        String userId = CurrentUser.getCurrentUser();
        if (userId != null && userId.trim().length() >0){
            //判断当前登录人员的id与修改的结果id是否一致
            if (Integer.parseInt(userId) == userInfoModel.getUuid()){
                //更新信息
                UserInfoModel newUserInfo = userAPI.updateUserInfo(userInfoModel);
                if (newUserInfo != null){
                    return ResponseVO.success(newUserInfo);
                }else {
                    return ResponseVO.serviceFail("用户信息修改失败");
                }
            }else {
                return ResponseVO.serviceFail("请修改您的个人信息");
            }
        }else {
            return ResponseVO.serviceFail("查询失败，用户尚未登陆");
        }
    }
}
