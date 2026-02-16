package demo.springai.service;

import demo.springai.model.ChatResult;
import demo.springai.model.SourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagService(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    /**
     * Query cơ bản với RAG
     */
    public ChatResult query(String question) {
        // 1. Tìm kiếm các documents liên quan
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(5)
                        .similarityThreshold(0.5) // Ngưỡng similarity
                        .build()
        );
        for(Document doc : similarDocs) {
            log.info("Found doc with similarity {} /n and Score {}", doc.getText(), doc.getScore());
            log.info("Metadata = {}", doc.getMetadata());
        }

        if (similarDocs.isEmpty()) {
            return ChatResult.builder().answer("Xin lỗi, tôi không tìm thấy thông tin liên quan để trả lời câu hỏi của bạn.")
                    .sources(List.of())
                    .promptTokens(0)
                    .completionTokens(0)
                    .totalTokens(0)
                    .build();
        }

        // 2. Tạo context từ các documents
        String context = similarDocs.stream()
                .map(doc -> {
                    String content = doc.getFormattedContent();
                    Map<String, Object> metadata = doc.getMetadata();

                    // Format context với metadata
                    return String.format(
                            "Môn: Tin | Lớp: %s | Tên sách: %s\nNội dung: %s",
                            metadata.getOrDefault("grade", ""),
                            metadata.getOrDefault("book_full_name", ""),
                            content
                    );
                })
                .collect(Collectors.joining("\n\n---\n\n"));

        List<SourceInfo> sources = similarDocs.stream()
                .map(doc -> {
                    Map<String, Object> outerMetadata = doc.getMetadata();

                    // Lấy metadata thật sự bên trong
                    Map<String, Object> metadata =
                            (Map<String, Object>) outerMetadata.get("metadata");

                    if (metadata == null) {
                        return null;
                    }

                    SourceInfo sourceInfo = new SourceInfo(
                            (String) metadata.get("book_full_name"),
                            (String) metadata.get("page_range"),
                            (String) metadata.get("document_id"),
                            doc.getScore()
                    );

                    log.info(sourceInfo.toString());
                    return sourceInfo;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();


        // 3. Tạo prompt với context
        String prompt = String.format("""
            Bạn là trợ lý giáo dục thông minh. Hãy trả lời câu hỏi dựa trên thông tin được cung cấp.
            
            THÔNG TIN TÀI LIỆU:
            %s
            
            CÂU HỎI: %s
            
            Hãy trả lời một cách chính xác, chi tiết và dễ hiểu. Nếu thông tin không đủ để trả lời, hãy nói rõ.
            """, context, question);

        // 4. Gọi LLM để sinh câu trả lời

        ChatResponse response = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();

        String answer = response.getResult().getOutput().getText();

        Usage usage = response.getMetadata().getUsage();

        Integer promptTokens = usage.getPromptTokens();
        log.info("Prompt tokens: {}", promptTokens);
        Integer completionTokens = usage.getCompletionTokens();
        log.info("Completion tokens: {}", completionTokens);
        Integer totalTokens = usage.getTotalTokens();
        log.info("Total tokens: {}", totalTokens);
        return new ChatResult(
                answer,
                sources,
                promptTokens,
                completionTokens,
                totalTokens
        );
    }

}