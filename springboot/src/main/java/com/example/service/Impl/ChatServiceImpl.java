package com.example.service.Impl;

import com.example.config.AliyunAIConfig;
import com.example.service.ChatService;
import com.example.service.StudentService;
import com.example.entity.Student;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private static final String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Autowired
    private AliyunAIConfig aiConfig;

    @Autowired
    private StudentService studentService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String ask(String message, Integer studentId) {
        String studentInfo = getStudentContext(studentId);
        String systemPrompt = buildSystemPrompt(studentInfo);

        try {
            String requestBody = buildRequestBody(systemPrompt, message);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + aiConfig.getApiKey())
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("AI API call failed: {}", response);
                    throw new RuntimeException("AI API call failed: " + response);
                }

                String responseBody = response.body().string();
                return parseAIResponse(responseBody);
            }
        } catch (IOException e) {
            log.error("AI API request failed", e);
            throw new RuntimeException("AI 服务请求失败，请稍后重试");
        }
    }

    private String getStudentContext(Integer studentId) {
        if (studentId == null) {
            return "未知学生";
        }
        try {
            Student student = studentService.selectById(studentId);
            if (student != null) {
                return String.format("姓名：%s", student.getName());
            }
        } catch (Exception e) {
            log.warn("Failed to get student info", e);
        }
        return "未知学生";
    }

    private String buildSystemPrompt(String studentInfo) {
        return String.format("【系统提示】\n你是一个课程咨询助手。当前学生信息：\n- %s\n\n请根据以上信息回答学生的问题。", studentInfo);
    }

    private String buildRequestBody(String systemPrompt, String userMessage) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"model\": \"").append(aiConfig.getModel()).append("\",");
        sb.append("\"messages\": [");
        sb.append("{\"role\": \"system\", \"content\": \"").append(escapeJson(systemPrompt)).append("\"},");
        sb.append("{\"role\": \"user\", \"content\": \"").append(escapeJson(userMessage)).append("\"}");
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private String parseAIResponse(String responseBody) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode choicesNode = rootNode.path("choices");
        if (choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode messageNode = choicesNode.get(0).path("message");
            return messageNode.path("content").asText();
        }
        throw new RuntimeException("Invalid AI response format");
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
