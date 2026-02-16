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

import java.util.*;
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
                SearchRequest.builder().query(question).topK(20)
                        .similarityThreshold(0.5) // Ngưỡng similarity
                        .build()
        );
        for(Document doc : similarDocs) {
            log.info("Found doc with similarity {} /n and Score {}", doc.getText(), doc.getScore());
            log.info("Metadata = {}", doc.getMetadata());
        }

        if (similarDocs.isEmpty()) {
            return ChatResult.builder().answer("Xin lỗi, tôi không tìm thấy thông tin liên quan để trả lời câu hỏi của bạn.")
                    .sources(Map.of())
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

        Map<String, String> sources = similarDocs.stream()
                .map(document -> {
                    Map<String, Object> outer = document.getMetadata();
                    Map<String, Object> metadata = (Map<String, Object>) outer.get("metadata");
                    if (metadata == null) return null;

                    return new SourceInfo(
                            (String) metadata.get("book_full_name"),
                            (String) metadata.get("page_range"),
                            document.getScore(),
                            (String) metadata.get("document_id")
                    );
                        }
                )
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(SourceInfo::getSimilarityScore).reversed())
                .collect(Collectors.groupingBy(
                        SourceInfo::getDocumentTitle,
                        Collectors.mapping(
                                SourceInfo::getPageNumber,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        pages -> {
                                            // 1. Làm sạch: bỏ chữ "Trang" dư thừa
                                            List<String> cleanPages = pages.stream()
                                                    .map(p -> p.replaceAll("(?i)Trang\\s*", "").trim())
                                                    .distinct()
                                                    .toList();

                                            // 2. Logic gộp đơn giản: Nếu "1" nằm trong "1-3" thì loại "1"
                                            List<String> finalPages = new ArrayList<>();
                                            for (String p : cleanPages) {
                                                boolean isRedundant = cleanPages.stream().anyMatch(other -> {
                                                    if (p.equals(other)) return false;
                                                    // Kiểm tra xem p có phải là tập con của other không (ví dụ "1" trong "1-3")
                                                    return isPageIncluded(p, other);
                                                });
                                                if (!isRedundant) finalPages.add(p);
                                            }

                                            return "Trang: " + String.join(", ", finalPages);
                                        }
                                )
                        )
                ));


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

    private boolean isPageIncluded(String target, String container) {
        if (!container.contains("-")) return false; // Thằng chứa không phải là một khoảng

        try {
            String[] cParts = container.split("-");
            int cStart = Integer.parseInt(cParts[0]);
            int cEnd = Integer.parseInt(cParts[1]);

            if (target.contains("-")) {
                String[] tParts = target.split("-");
                return Integer.parseInt(tParts[0]) >= cStart && Integer.parseInt(tParts[1]) <= cEnd;
            } else {
                int tVal = Integer.parseInt(target);
                return tVal >= cStart && tVal <= cEnd;
            }
        } catch (Exception e) { return false; }
    }

}