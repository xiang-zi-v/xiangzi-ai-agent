package com.xiangzi.xiangziimagesearchmcpserver.config;

import com.xiangzi.xiangziimagesearchmcpserver.service.ImageSearchTool;
import com.xiangzi.xiangziimagesearchmcpserver.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ImageSearchTool imageSearchToolService;

    @Bean
    public ToolCallbackProvider weatherTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService, imageSearchToolService)
                .build();
    }
}
