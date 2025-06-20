package com.example.web_transaction.controller;

import com.example.web_transaction.entity.ChatMessage;
import com.example.web_transaction.entity.User;
import com.example.web_transaction.mapper.ChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody ChatMessage message) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 设置发送时间为当前时间
            message.setSendTime(LocalDateTime.now());
            chatMessageMapper.insertMessage(message);
            response.put("success", true);
            response.put("message", "消息发送成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "消息发送失败: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(@RequestParam("userId") int userId,
                                            @RequestParam("contactId") int contactId) {
        return chatMessageMapper.getMessagesBetweenUsers(userId, contactId);
    }

    @GetMapping("/contacts")
    public List<User> getContacts(@RequestParam("userId") int userId) {
        return chatMessageMapper.getContactsByUserId(userId);
    }
}