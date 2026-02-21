package demo.springai.controller;

import demo.springai.dtos.ChatRequest;
import demo.springai.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody @Valid ChatRequest request) {
        String response = chatService.chat(request.getPrompt(), request.getId());
        return ResponseEntity.ok(response);
    }
}