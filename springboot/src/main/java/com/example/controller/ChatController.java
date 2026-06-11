package com.example.controller;

import com.example.common.Result;
import com.example.service.ChatService;
import com.example.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/ask")
    public Result ask(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("请输入您的问题");
        }

        Integer studentId = UserUtil.getCurrentUserId();

        try {
            String reply = chatService.ask(message.trim(), studentId);
            return Result.success(reply);
        } catch (Exception e) {
            log.error("AI chat error", e);
            return Result.error(e.getMessage());
        }
    }
}
