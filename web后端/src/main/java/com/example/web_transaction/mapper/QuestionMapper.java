package com.example.web_transaction.mapper;

import com.example.web_transaction.entity.Question;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface QuestionMapper {
//    @Select("SELECT * FROM questions ORDER BY time DESC")
//    List<Question> findAll();
//
//    @Select("SELECT * FROM questions WHERE title LIKE CONCAT('%', #{keyword}, '%') ORDER BY time DESC")
//    List<Question> searchByTitle(@Param("keyword") String keyword);




    @Insert("INSERT INTO questions (title, content, user_id, time) " +
            "VALUES (#{title}, #{content}, #{user_id}, #{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Question question);

    @Select("SELECT * FROM questions WHERE id = #{id}")
    Question findById(@Param("id") int id);

//    @Select("SELECT * FROM questions ORDER BY time DESC LIMIT #{offset}, #{size}")
//    List<Question> findAllWithPagination(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM questions")
    long count();

//    @Select("SELECT * FROM questions WHERE title LIKE CONCAT('%', #{keyword}, '%') ORDER BY time DESC LIMIT #{offset}, #{size}")
//    List<Question> searchByTitleWithPagination(
//            @Param("keyword") String keyword,
//            @Param("offset") int offset,
//            @Param("size") int size);

    @Select("SELECT COUNT(*) FROM questions WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    long countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT q.*, u.uname as user_name FROM questions q LEFT JOIN user u ON q.user_id = u.uid ORDER BY q.time DESC LIMIT #{offset}, #{size}")
    List<Question> findAllWithPagination(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT q.*, u.uname as user_name FROM questions q LEFT JOIN user u ON q.user_id = u.uid WHERE q.title LIKE CONCAT('%', #{keyword}, '%') ORDER BY q.time DESC LIMIT #{offset}, #{size}")
    List<Question> searchByTitleWithPagination(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);
}
