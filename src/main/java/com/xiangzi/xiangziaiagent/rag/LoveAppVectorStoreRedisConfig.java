package com.xiangzi.xiangziaiagent.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

//@Configuration
public class LoveAppVectorStoreRedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    JedisPooled jedisPooled() {
        return new JedisPooled(redisHost, redisPort);
    }

    /**
     * 创建LoveApp向量存储Bean
     * 该方法构建一个SimpleVectorStore，加载文档并将其向量化存储
     *
     * @return 已加载文档的VectorStore实例
     */
    @Bean
    public VectorStore loveAppVectorStoreRedis(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("custom-index")                // 可选：默认为 "spring-ai-index"
                .prefix("custom-prefix")                  // 可选：默认为 "embedding:"
                .metadataFields(                         // 可选：定义用于过滤的元数据字段
                        RedisVectorStore.MetadataField.tag("country"),
                        RedisVectorStore.MetadataField.numeric("year"))
                .initializeSchema(true)                   // 可选：默认为 false
                .batchingStrategy(new TokenCountBatchingStrategy()) // 可选：默认为 TokenCountBatchingStrategy
                .build();
    }

}
