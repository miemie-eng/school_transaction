package com.example.web_transaction.mapper;

import com.example.web_transaction.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT c.*, u.uname as user_name FROM comments c LEFT JOIN user u ON c.user_id = u.uid WHERE c.question_id = #{questionId} ORDER BY c.time DESC")
    List<Comment> findByQuestionId(@Param("questionId") Long questionId);

    @Insert("INSERT INTO comments (question_id, user_id, content, time) " +
            "VALUES (#{question_id}, #{user_id}, #{content}, #{time})")
    void insert(Comment comment);
}