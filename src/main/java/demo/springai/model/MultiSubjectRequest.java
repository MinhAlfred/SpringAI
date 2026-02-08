package demo.springai.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiSubjectRequest {

    @NotBlank(message = "Câu hỏi không được để trống")
    @Size(min = 3, max = 500, message = "Câu hỏi phải từ 3 đến 500 ký tự")
    private String question;

    @NotEmpty(message = "Danh sách môn học không được để trống")
    @Size(min = 1, max = 10, message = "Số lượng môn học từ 1 đến 10")
    private List<String> subjects;  // ["tin_hoc", "toan", "van"]

    @Min(value = 1, message = "Lớp phải từ 1 đến 12")
    @Max(value = 12, message = "Lớp phải từ 1 đến 12")
    private Integer grade;  // Optional
}