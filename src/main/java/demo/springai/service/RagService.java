package demo.springai.service;

import demo.springai.model.QueryFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public String query(String question) {
        // 1. Tìm kiếm các documents liên quan
        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question).topK(10)
//                        .similarityThreshold(0.7) // Ngưỡng similarity
                        .build()
        );

        if (similarDocs.isEmpty()) {
            return "Xin lỗi, tôi không tìm thấy thông tin liên quan đến câu hỏi của bạn.";
        }

        // 2. Tạo context từ các documents
        String context = similarDocs.stream()
                .map(doc -> {
                    String content = doc.getFormattedContent();
                    Map<String, Object> metadata = doc.getMetadata();

                    // Format context với metadata
                    return String.format(
                            "Môn: %s | Lớp: %s | Bài: %s\nNội dung: %s",
                            metadata.getOrDefault("subject", ""),
                            metadata.getOrDefault("grade", ""),
                            metadata.getOrDefault("lesson_title", ""),
                            content
                    );
                })
                .collect(Collectors.joining("\n\n---\n\n"));

        // 3. Tạo prompt với context
        String prompt = String.format("""
            Bạn là trợ lý giáo dục thông minh. Hãy trả lời câu hỏi dựa trên thông tin được cung cấp.
            
            THÔNG TIN TÀI LIỆU:
            %s
            
            CÂU HỎI: %s
            
            Hãy trả lời một cách chính xác, chi tiết và dễ hiểu. Nếu thông tin không đủ để trả lời, hãy nói rõ.
            """, context, question);

        // 4. Gọi LLM để sinh câu trả lời
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
    public String queryWithFilter(String question, QueryFilter filter) {
        // Build filter expression
        Filter.Expression filterExpression = buildFilterExpression(filter);

        SearchRequest.Builder searchRequestBuilder = SearchRequest.builder().query(question)
                .topK(5)
                .similarityThreshold(0.7);

        // Thêm filter nếu có
        if (filterExpression != null) {
            searchRequestBuilder.filterExpression(filterExpression);
        }

        List<Document> similarDocs = vectorStore.similaritySearch(
                searchRequestBuilder.build()
        );

        return generateResponse(question, similarDocs, filter);
    }

    /**
     * Build filter expression từ QueryFilter
     */
    private Filter.Expression buildFilterExpression(QueryFilter filter) {
        if (filter == null) {
            return null;
        }

        List<Filter.Expression> conditions = new ArrayList<>();

        // Filter theo môn học
        if (filter.getSubject() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("subject_key"),
                    new Filter.Value(filter.getSubject())
            ));
        }

        // Filter theo lớp
        if (filter.getGrade() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("grade"),
                    new Filter.Value(filter.getGrade())
            ));
        }

        // Filter theo số bài học
        if (filter.getLessonNumber() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("lesson_number"),
                    new Filter.Value(filter.getLessonNumber())
            ));
        }

        // Filter theo cấp học
        if (filter.getEducationLevel() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("education_level"),
                    new Filter.Value(filter.getEducationLevel())
            ));
        }

        // Filter theo chapter number
        if (filter.getChapterNumber() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("chapter_number"),
                    new Filter.Value(filter.getChapterNumber())
            ));
        }

        // Filter có bài tập
        if (filter.getHasExercises() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("has_exercises"),
                    new Filter.Value(filter.getHasExercises())
            ));
        }

        // Filter có hoạt động
        if (filter.getHasActivities() != null) {
            conditions.add(new Filter.Expression(
                    Filter.ExpressionType.EQ,
                    new Filter.Key("has_activities"),
                    new Filter.Value(filter.getHasActivities())
            ));
        }

        // Kết hợp các điều kiện với AND
        if (conditions.isEmpty()) {
            return null;
        }

        if (conditions.size() == 1) {
            return conditions.get(0);
        }

        // Combine multiple conditions với AND
        Filter.Expression result = conditions.get(0);
        for (int i = 1; i < conditions.size(); i++) {
            result = new Filter.Expression(
                    Filter.ExpressionType.AND,
                    result,
                    conditions.get(i)
            );
        }

        return result;
    }

    /**
     * Ví dụ filter nâng cao: Tìm bài học có bài tập HOẶC hoạt động
     */
    public String queryWithAdvancedFilter(String question, QueryFilter filter) {
        Filter.Expression mainFilter = buildFilterExpression(filter);

        // Thêm điều kiện OR: has_exercises == true OR has_activities == true
        Filter.Expression hasContent = new Filter.Expression(
                Filter.ExpressionType.OR,
                new Filter.Expression(
                        Filter.ExpressionType.EQ,
                        new Filter.Key("has_exercises"),
                        new Filter.Value(true)
                ),
                new Filter.Expression(
                        Filter.ExpressionType.EQ,
                        new Filter.Key("has_activities"),
                        new Filter.Value(true)
                )
        );

        // Kết hợp với filter chính
        Filter.Expression combinedFilter = mainFilter != null
                ? new Filter.Expression(Filter.ExpressionType.AND, mainFilter, hasContent)
                : hasContent;

        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question)
                        .topK(5)
                        .similarityThreshold(0.7)
                        .filterExpression(combinedFilter).build()
        );

        return generateResponse(question, similarDocs, filter);
    }

    /**
     * Ví dụ filter range: Tìm từ lớp 6 đến lớp 9
     */
    public String queryGradeRange(String question, int minGrade, int maxGrade, String subject) {
        // grade >= minGrade AND grade <= maxGrade
        Filter.Expression gradeFilter = new Filter.Expression(
                Filter.ExpressionType.AND,
                new Filter.Expression(
                        Filter.ExpressionType.GTE,
                        new Filter.Key("grade"),
                        new Filter.Value(minGrade)
                ),
                new Filter.Expression(
                        Filter.ExpressionType.LTE,
                        new Filter.Key("grade"),
                        new Filter.Value(maxGrade)
                )
        );

        // Thêm filter môn học nếu có
        if (subject != null) {
            gradeFilter = new Filter.Expression(
                    Filter.ExpressionType.AND,
                    gradeFilter,
                    new Filter.Expression(
                            Filter.ExpressionType.EQ,
                            new Filter.Key("subject_key"),
                            new Filter.Value(subject)
                    )
            );
        }

        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question)
                        .topK(5)
                        .similarityThreshold(0.7)
                        .filterExpression(gradeFilter).build()
        );

        return generateResponse(question, similarDocs, null);
    }

    public String queryMultipleSubjects(String question, List<String> subjects, Integer grade) {
        // subject_key IN ['tin_hoc', 'toan', 'van']
        Filter.Expression subjectFilter = new Filter.Expression(
                Filter.ExpressionType.IN,
                new Filter.Key("subject_key"),
                new Filter.Value(subjects)
        );
        if (grade != null) {
            subjectFilter = new Filter.Expression(
                    Filter.ExpressionType.AND,
                    subjectFilter,
                    new Filter.Expression(
                            Filter.ExpressionType.EQ,
                            new Filter.Key("grade"),
                            new Filter.Value(grade)
                    )
            );
        }

        List<Document> similarDocs = vectorStore.similaritySearch(
                SearchRequest.builder().query(question)
                        .topK(5)
                        .similarityThreshold(0.7)
                        .filterExpression(subjectFilter).build()
        );

        return generateResponse(question, similarDocs, null);
    }

    /**
     * Query nâng cao với filter theo metadata
     */


    private String generateResponse(String question, List<Document> docs, QueryFilter filter) {
        if (docs.isEmpty()) {
            return String.format(
                    "Không tìm thấy thông tin về \"%s\" trong %s lớp %s.",
                    question,
                    filter.getSubject() != null ? "môn " + filter.getSubject() : "tài liệu",
                    filter.getGrade() != null ? filter.getGrade() : ""
            );
        }

        String context = docs.stream()
                .map(doc -> formatDocumentWithMetadata(doc))
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = buildPrompt(question, context, filter);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String formatDocumentWithMetadata(Document doc) {
        Map<String, Object> meta = doc.getMetadata();
        return String.format("""
            📚 Môn: %s - Lớp %s
            📖 Bài %s: %s
            📝 %s
            
            %s
            """,
                meta.getOrDefault("subject", ""),
                meta.getOrDefault("grade", ""),
                meta.getOrDefault("lesson_number", ""),
                meta.getOrDefault("lesson_title", ""),
                meta.getOrDefault("chapter_title", ""),
                doc.getContentFormatter()
        );
    }

    private String buildPrompt(String question, String context, QueryFilter filter) {
        String filterInfo = "";
        if (filter != null) {
            filterInfo = String.format(
                    "Lọc theo: %s%s%s",
                    filter.getSubject() != null ? "Môn " + filter.getSubject() + ", " : "",
                    filter.getGrade() != null ? "Lớp " + filter.getGrade() + ", " : "",
                    filter.getLessonNumber() != null ? "Bài " + filter.getLessonNumber() : ""
            );
        }

        return String.format("""
            Bạn là trợ lý giáo dục cho học sinh Việt Nam. Hãy trả lời câu hỏi dựa trên tài liệu giáo khoa.
            
            %s
            
            TÀI LIỆU THAM KHẢO:
            %s
            
            CÂU HỎI: %s
            
            YÊU CẦU:
            - Trả lời bằng tiếng Việt
            - Giải thích rõ ràng, dễ hiểu
            - Trích dẫn thông tin từ tài liệu khi cần
            - Nếu câu hỏi không liên quan đến tài liệu, hãy thông báo rõ ràng
            """,
                filterInfo.isEmpty() ? "" : "BỐI CẢNH: " + filterInfo,
                context,
                question
        );
    }
}