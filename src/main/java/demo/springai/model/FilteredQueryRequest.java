package demo.springai.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilteredQueryRequest {

    @NotBlank(message = "Câu hỏi không được để trống")
    @Size(min = 3, max = 500, message = "Câu hỏi phải từ 3 đến 500 ký tự")
    private String question;

    // Filter fields
    private String subject;           // "tin_hoc", "toan", "van", "ly", "hoa", etc.

    @Min(value = 1, message = "Lớp phải từ 1 đến 12")
    @Max(value = 12, message = "Lớp phải từ 1 đến 12")
    private Integer grade;

    @Min(value = 1, message = "Số bài học phải lớn hơn 0")
    private Integer lessonNumber;

    private String educationLevel;    // "THCS", "THPT"

    private String chapterNumber;

    private Boolean hasExercises;

    private Boolean hasActivities;

    private Boolean hasCode;
}