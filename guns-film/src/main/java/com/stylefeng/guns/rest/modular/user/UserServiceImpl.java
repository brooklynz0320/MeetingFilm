package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.api.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.UserMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import com.stylefeng.guns.rest.common.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Service(interfaceClass = UserAPI.class,loadbalance = "roundrobin")
public class UserServiceImpl implements UserAPI {

    @Autowired
    MoocUserTMapper moocUserTMapper;

    /*//相当于是provider 需要在yml里用dubbo协议暴露服务
    @Override
    public int login(String username, String password) {
        System.out.println("this is user service!! " + username + "," + password);
        return false;

    }*/

    @Override
    public int login(String username, String password) {
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(username);
        MoocUserT result = moocUserTMapper.selectOne(moocUserT);
        if (result != null && result.getUuid() > 0){
            //与加密以后的密码做匹配
            if (result.getUserPwd().equals(MD5Util.encrypt(password))){
                return result.getUuid();
            }
        }
        return 0 ;
    }

    @Override
    public boolean register(UserModel userModel) {
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());
        //md5数据加密
        moocUserT.setUserPwd(MD5Util.encrypt(userModel.getPassword()));
        moocUserT.setEmail(userModel.getEmail());
        moocUserT.setUserPhone(userModel.getPhone());
        moocUserT.setAddress(userModel.getAddress());
        Integer insert = moocUserTMapper.insert(moocUserT);
        if (insert > 0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean checkUsername(String username) {
        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name",username);
        Integer integer = moocUserTMapper.selectCount(entityWrapper);
        if (integer >= 1 && integer != null){
            return false;
        }else {
            return true;
        }
    }

    private UserInfoModel doToUserInfo(MoocUserT moocUserT){
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setAddress(moocUserT.getAddress());
        userInfoModel.setBiography(moocUserT.getBiography());
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setHeadAddress(moocUserT.getHeadUrl());
        userInfoModel.setLifeState(moocUserT.getLifeState());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setCreateTime(moocUserT.getBeginTime().getTime());
        userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        return userInfoModel;
    }

    @Override
    public UserInfoModel getUserInfo(int uuid) {
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        UserInfoModel userInfoModel = doToUserInfo(moocUserT);
        return userInfoModel;
    }

    private Date longToDate(long l){
        Date date = new Date(l);
        return date;
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUuid(userInfoModel.getUuid());
        moocUserT.setUserSex(userInfoModel.getSex());
        moocUserT.setUpdateTime(longToDate(System.currentTimeMillis()));
        moocUserT.setNickName(userInfoModel.getNickname());
        moocUserT.setLifeState(userInfoModel.getLifeState());
        moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
        moocUserT.setBirthday(userInfoModel.getBirthday());
        moocUserT.setBiography(userInfoModel.getBiography());
        moocUserT.setEmail(userInfoModel.getEmail());
        moocUserT.setUserPhone(userInfoModel.getPhone());
        moocUserT.setAddress(userInfoModel.getAddress());
        //存入数据库
        Integer integer = moocUserTMapper.updateById(moocUserT);
        if (integer > 0){
            //按照id获取用户信息
            UserInfoModel userInfo = getUserInfo(moocUserT.getUuid());
            return userInfo;
        }else {
            return userInfoModel;
        }
    }
}
