package com.example.web_transaction.mapper;

import com.example.web_transaction.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("select * from user where uid=#{uid} and upwd=#{upwd}")
    User Login(User user);
    @Update("update user set uname=#{uname},upwd=#{upwd},uphone=#{uphone} where uid=#{uid}")
    int UpdateUser(User user);
    @Select("select * from user where uid=#{uid}")
    User findByUid(int uid);

    @Select("select * from user where uname=#{uname}")
    User findByUname(String uname);

    @Insert("insert into user(uid, uname, upwd, uphone) values(#{uid}, #{uname}, #{upwd}, #{uphone})")
    @Options(useGeneratedKeys = true, keyProperty = "uid")
    int insertUser(User user);
    @Update("<script>" +
            "UPDATE user " +
            "<set>" +
            "   <if test='uname != null'>uname = #{uname},</if>" +
            "   <if test='upwd != null'>upwd = #{upwd},</if>" +
            "   <if test='uphone != null'>uphone = #{uphone},</if>" +
            "</set>" +
            "WHERE uid = #{uid}" +
            "</script>")
    int UpdateUserSelective(User user);

    @Select("SELECT COUNT(*) FROM user WHERE uname = #{uname} AND uid != #{uid}")
    int countByUnameExcludingCurrent(@Param("uname") String uname, @Param("uid") int uid);
    @Select("SELECT uname FROM user WHERE uid = #{uid}")
    String findNameById(@Param("uid") int uid);
    @Select("SELECT uphone FROM user WHERE uid = #{uid}")
    String findPhoneById(@Param("uid") int uid);
}