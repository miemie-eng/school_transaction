package com.example.web_transaction.entity;



import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Question {
    private int id;
    private String title;
    private String content;
    @JsonProperty("user_id")
    private int user_id;
    private Date time;
    private String user_name;
    private int comment_count;
    private int views; // 浏览数字段

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getUserId() { return user_id; }
    public void setUserId(int userId) { this.user_id = userId; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public String getUserName() { return user_name; }
    public void setUserName(String userName) { this.user_name = userName; }
    public int getCommentCount() { return comment_count; }
    public void setCommentCount(int commentCount) { this.comment_count = commentCount; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public Question() {
        this.time=new Date();
    }
}