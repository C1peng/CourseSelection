package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.ai")
public class AliyunAIConfig {
    private String apiKey;
    private String model = "qwen-turbo";
    private Integer timeout = 30000;
}
