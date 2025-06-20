package com.example.web_transaction.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class ChatMessage {
    private int id;
    private int sender_id;
    private int receiver_id;
    private String content;
    private LocalDateTime send_time;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return sender_id;
    }

    public void setSenderId(int senderId) {
        this.sender_id = senderId;
    }

    public int getReceiverId() {
        return receiver_id;
    }

    public void setReceiverId(int receiverId) {
        this.receiver_id = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return send_time;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.send_time = sendTime;
    }


}