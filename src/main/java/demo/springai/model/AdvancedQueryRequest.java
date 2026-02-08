package demo.springai.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedQueryRequest {

    @NotBlank(message = "Câu hỏi không được để trống")
    @Size(min = 3, max = 500, message = "Câu hỏi phải từ 3 đến 500 ký tự")
    private String question;

    // Filter parameters
    private String subject;
    private Integer grade;
    private Integer lessonNumber;
    private String educationLevel;
    private String chapterNumber;
    private Boolean hasExercises;
    private Boolean hasActivities;
    private Boolean hasQuestions;
    private Boolean hasCode;

    // Search parameters
    @Min(value = 1, message = "Số lượng kết quả tối thiểu là 1")
    @Max(value = 20, message = "Số lượng kết quả tối đa là 20")
    @Builder.Default
    private Integer topK = 5;

    @DecimalMin(value = "0.0", message = "Ngưỡng similarity phải từ 0.0 đến 1.0")
    @DecimalMax(value = "1.0", message = "Ngưỡng similarity phải từ 0.0 đến 1.0")
    @Builder.Default
    private Double similarityThreshold = 0.7;

    // Response options
    @Builder.Default
    private Boolean includeMetadata = true;

    @Builder.Default
    private Boolean includeSources = true;
}