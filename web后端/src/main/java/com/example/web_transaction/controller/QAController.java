package com.example.web_transaction.controller;

import com.example.web_transaction.entity.Comment;
import com.example.web_transaction.entity.Question;
import com.example.web_transaction.entity.User;
import com.example.web_transaction.mapper.CommentMapper;
import com.example.web_transaction.mapper.QuestionMapper;
import com.example.web_transaction.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/qa")
public class QAController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

//    // 获取所有问题
//    @GetMapping("/questions")
//    public ResponseEntity<List<Question>> getAllQuestions() {
//        List<Question> questions = questionMapper.findAll();
//        return ResponseEntity.ok(questions);
//    }
//
//    // 搜索问题
//    @GetMapping("/questions/search")
//    public ResponseEntity<List<Question>> searchQuestions(@RequestParam String keyword) {
//        List<Question> questions = questionMapper.searchByTitle(keyword);
//        return ResponseEntity.ok(questions);
//    }


    // 获取所有问题
    @GetMapping("/questions")
    public ResponseEntity<Map<String, Object>> getAllQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 获取总记录数
        long total = questionMapper.count();

        // 获取分页数据
        List<Question> questions = questionMapper.findAllWithPagination(offset, size);

        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        response.put("currentPage", page);
        response.put("totalItems", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(response);
    }

    // 搜索问题
    @GetMapping("/questions/search")
    public ResponseEntity<Map<String, Object>> searchQuestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size) {
        // 计算偏移量
        int offset = (page - 1) * size;

        // 获取总记录数
        long total = questionMapper.countByKeyword(keyword);

        // 获取分页数据
        List<Question> questions = questionMapper.searchByTitleWithPagination(keyword, offset, size);

        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        response.put("currentPage", page);
        response.put("totalItems", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));

        return ResponseEntity.ok(response);
    }

    // 创建问题
    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        question.setTime(new Date());
        questionMapper.insert(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    // 获取问题的评论
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getCommentsByQuestion(@RequestParam Long questionId) {
        List<Comment> comments = commentMapper.findByQuestionId(questionId);
        return ResponseEntity.ok(comments);
    }

    // 创建评论
    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Question question = questionMapper.findById(comment.getQuestionId());
        Optional.ofNullable(question).orElseThrow(() -> new RuntimeException("没有关联的问题"));
        comment.setTime(new Date());
        commentMapper.insert(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // 获取用户信息
    @GetMapping("/user/{uid}")
    public ResponseEntity<User> getUserById(@PathVariable int uid) {
        User user = userMapper.findByUid(uid);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
}

