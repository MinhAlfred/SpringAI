package demo.springai.controller;

import demo.springai.model.*;
import demo.springai.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RagController {

    private final RagService ragService;

    /**
     * Query cơ bản không filter
     */
    @PostMapping("/query")
    public ResponseEntity<QueryResponse> query(@Valid @RequestBody QueryRequest request) {
        log.info("Received query: {}", request.getQuestion());
        try {
            ChatResult answer = ragService.query(request.getQuestion());
            return ResponseEntity.ok(QueryResponse.builder()
                    .success(true)
                    .answer(answer)
                    .build());
        } catch (Exception e) {
            log.error("Error processing query", e);
            return ResponseEntity.status(500)
                    .body(QueryResponse.builder()
                            .success(false)
                            .error(e.getMessage())
                            .build());
        }
    }
}