package demo.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.UUID;

@Component
@SessionScope
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String chat(String prompt,String ChatID) {
        return chatClient.prompt()
                .user(userMessage -> userMessage.text(prompt))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, ChatID))
                .call()
                .content();
    }
}
