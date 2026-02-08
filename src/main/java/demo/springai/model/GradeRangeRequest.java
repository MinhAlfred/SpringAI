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
public class GradeRangeRequest {

    @NotBlank(message = "Câu hỏi không được để trống")
    @Size(min = 3, max = 500, message = "Câu hỏi phải từ 3 đến 500 ký tự")
    private String question;

    @NotNull(message = "Lớp tối thiểu không được để trống")
    @Min(value = 1, message = "Lớp tối thiểu phải từ 1 đến 12")
    @Max(value = 12, message = "Lớp tối thiểu phải từ 1 đến 12")
    private Integer minGrade;

    @NotNull(message = "Lớp tối đa không được để trống")
    @Min(value = 1, message = "Lớp tối đa phải từ 1 đến 12")
    @Max(value = 12, message = "Lớp tối đa phải từ 1 đến 12")
    private Integer maxGrade;

    private String subject;  // Optional: "tin_hoc", "toan", etc.
}