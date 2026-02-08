package demo.springai.model;

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
public class QueryRequest {

    @NotBlank(message = "Câu hỏi không được để trống")
    @Size(min = 3, max = 500, message = "Câu hỏi phải từ 3 đến 500 ký tự")
    private String question;
}