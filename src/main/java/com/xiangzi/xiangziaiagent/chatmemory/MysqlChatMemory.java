package com.xiangzi.xiangziaiagent.chatmemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Stream;

public class MysqlChatMemory implements ChatMemory {


    private static final int DEFAULT_MAX_MESSAGES = 20;
    private final ChatMemoryRepository chatMemoryRepository;
    private final int maxMessages;

    private MysqlChatMemory(ChatMemoryRepository chatMemoryRepository, int maxMessages) {
        Assert.notNull(chatMemoryRepository, "chatMemoryRepository cannot be null");
        Assert.isTrue(maxMessages > 0, "maxMessages must be greater than 0");
        this.chatMemoryRepository = chatMemoryRepository;
        this.maxMessages = maxMessages;
    }

    public void add(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        Assert.notNull(messages, "messages cannot be null");
        Assert.noNullElements(messages, "messages cannot contain null elements");
        List<Message> memoryMessages = this.chatMemoryRepository.findByConversationId(conversationId);
        List<Message> processedMessages = this.process(memoryMessages, messages);
        this.chatMemoryRepository.saveAll(conversationId, processedMessages);
    }

    @Override
    public List<Message> get(String conversationId) {
        return this.chatMemoryRepository.findByConversationId(conversationId);
    }


    public void clear(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");
        this.chatMemoryRepository.deleteByConversationId(conversationId);
    }

    private List<Message> process(List<Message> memoryMessages, List<Message> newMessages) {
        List<Message> processedMessages = new ArrayList();
        Set<Message> memoryMessagesSet = new HashSet(memoryMessages);
        Stream var10000 = newMessages.stream();
        Objects.requireNonNull(SystemMessage.class);
        boolean hasNewSystemMessage = var10000.filter(SystemMessage.class::isInstance).anyMatch((messagex) -> !memoryMessagesSet.contains(messagex));
        List<Message> list = memoryMessages.stream().filter((messagex) -> !hasNewSystemMessage || !(messagex instanceof SystemMessage)).toList();
        Objects.requireNonNull(processedMessages);

        list.forEach(processedMessages::add);

        processedMessages.addAll(newMessages);
        if (processedMessages.size() <= this.maxMessages) {
            return processedMessages;
        } else {
            int messagesToRemove = processedMessages.size() - this.maxMessages;
            List<Message> trimmedMessages = new ArrayList();
            int removed = 0;

            for (Message message : processedMessages) {
                if (!(message instanceof SystemMessage) && removed < messagesToRemove) {
                    ++removed;
                } else {
                    trimmedMessages.add(message);
                }
            }

            return trimmedMessages;
        }
    }

    public static MysqlChatMemory.Builder builder() {
        return new MysqlChatMemory.Builder();
    }

    public static final class Builder {
        private ChatMemoryRepository chatMemoryRepository;
        private int maxMessages = 20;

        private Builder() {
        }

        public MysqlChatMemory.Builder chatMemoryRepository(ChatMemoryRepository chatMemoryRepository) {
            this.chatMemoryRepository = chatMemoryRepository;
            return this;
        }

        public MysqlChatMemory.Builder maxMessages(int maxMessages) {
            this.maxMessages = maxMessages;
            return this;
        }

        public MysqlChatMemory build() {
            if (this.chatMemoryRepository == null) {
                this.chatMemoryRepository = new InMemoryChatMemoryRepository();
            }

            return new MysqlChatMemory(this.chatMemoryRepository, this.maxMessages);
        }
    }

}