package com.example.web_transaction.mapper;

import com.example.web_transaction.entity.ChatMessage;
import com.example.web_transaction.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("INSERT INTO chat_message (sender_id, receiver_id, content,send_time) " +
            "VALUES (#{senderId}, #{receiverId}, #{content},#{sendTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMessage(ChatMessage message);

    @Select("SELECT * FROM chat_message " +
            "WHERE (sender_id = #{userId} AND receiver_id = #{contactId}) " +
            "OR (sender_id = #{contactId} AND receiver_id = #{userId}) " +
            "ORDER BY send_time ASC")
    List<ChatMessage> getMessagesBetweenUsers(@Param("userId") int userId,
                                              @Param("contactId") int contactId);

    @Select("SELECT DISTINCT u.uid, u.uname " +
            "FROM user u " +
            "WHERE u.uid IN (" +
            "    SELECT sender_id FROM chat_message WHERE receiver_id = #{userId} " +
            "    UNION " +
            "    SELECT receiver_id FROM chat_message WHERE sender_id = #{userId}" +
            ")")
    List<User> getContactsByUserId(@Param("userId") int userId);
}