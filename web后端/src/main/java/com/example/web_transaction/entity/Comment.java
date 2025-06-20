package com.example.web_transaction.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Comment {
    private int id;
    @JsonProperty("question_id")
    private int question_id;
    @JsonProperty("user_id")
    private int user_id;
    private String content;
    private Date time;
    private String user_name;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuestionId() { return question_id; }
    public void setQuestionId(int questionId) { this.question_id = questionId; }
    public int getUserId() { return user_id; }
    public void setUserId(int userId) { this.user_id = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public String getUserName() { return user_name; }
    public void setUserName(String userName) { this.user_name = userName; }
    public Comment() {
        this.time=new Date();
    }
}