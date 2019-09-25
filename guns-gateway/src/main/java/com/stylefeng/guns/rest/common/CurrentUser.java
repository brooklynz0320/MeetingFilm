package com.stylefeng.guns.rest.common;

import com.stylefeng.guns.api.user.UserInfoModel;

public class CurrentUser {

    //线程绑定的存储空间
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    //将用户id放入存储空间
    public static void saveUserId(String userId){
        threadLocal.set(userId);
    }

    //将用户id取出
    public static String getCurrentUser(){
        return threadLocal.get();
    }


    /*//将用户信息放入存储空间
    public static void saveUserInfo(UserInfoModel userInfoModel){

        threadLocal.set(userInfoModel);
    }

    //将用户信息取出
    public static UserInfoModel userInfoModel(){
        return threadLocal.get();
    }*/

}
