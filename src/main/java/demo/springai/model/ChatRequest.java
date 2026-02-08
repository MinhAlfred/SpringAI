package demo.springai.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Tin nhắn không được để trống")
    @Size(min = 1, max = 1000, message = "Tin nhắn phải từ 1 đến 1000 ký tự")
    private String message;

    private String conversationId;  // ID của cuộc hội thoại

    private List<ChatMessage> history;  // Lịch sử chat

    // Filter parameters (optional)
    private String subject;
    private Integer grade;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;     // "user" hoặc "assistant"
        private String content;
        private Long timestamp;
    }
}