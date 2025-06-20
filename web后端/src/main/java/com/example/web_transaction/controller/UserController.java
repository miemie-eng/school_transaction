package com.example.web_transaction.controller;

import com.example.web_transaction.entity.User;
import com.example.web_transaction.mapper.ProductMapper;
import com.example.web_transaction.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin

public class UserController {
    @Autowired
    UserMapper userMapper;

    @RequestMapping("/login")
    //登录
    public User Login(@RequestBody User user){
        return userMapper.Login(user);
    }
    @RequestMapping("/updater")
    public Map<String, Object> updateStu(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        // 只有当用户名被修改时才检查唯一性
        if (user.getUname() != null) {
            // 检查用户名是否已被其他用户使用（排除当前用户）
            int count = userMapper.countByUnameExcludingCurrent(user.getUname(), user.getUid());
            if (count > 0) {
                response.put("success", false);
                response.put("message", "该用户名已被其他用户使用");
                return response;
            }
        }

        // 执行更新 - 只更新提供的字段
        int result = userMapper.UpdateUserSelective(user);
        if (result > 0) {

            response.put("success", true);
            response.put("message", "更新成功");
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return response;
    }
    // 后端添加接口
    @GetMapping("/user/name/{uid}")
    public Map<String, String> getUserName(@PathVariable int uid) {
        Map<String, String> result = new HashMap<>();
        String name = userMapper.findNameById(uid);
        result.put("name", name != null ? name : "未知用户");
        return result;
    }
    @GetMapping("/user/phone/{uid}")
    public Map<String, String> getUserPhone(@PathVariable int uid) {
        Map<String, String> result = new HashMap<>();
        String phone = userMapper.findPhoneById(uid);
        result.put("phone", phone != null ? phone : "未知用户");
        return result;
    }
    @RequestMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        // 检查用户名是否已存在
        if (userMapper.findByUname(user.getUname()) != null) {
            response.put("success", false);
            response.put("message", "该用户名已被注册");
            return response;
        }
        // 检查学号是否已存在
        if (userMapper.findByUid(user.getUid()) != null) {
            response.put("success", false);
            response.put("message", "该学号已被注册");
            return response;
        }

        // 插入新用户
        int result = userMapper.insertUser(user);
        if (result > 0) {
            response.put("success", true);
            response.put("message", "注册成功");
        } else {
            response.put("success", false);
            response.put("message", "注册失败，请重试");
        }

        return response;
    }
}