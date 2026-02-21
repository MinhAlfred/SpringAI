package demo.springai.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {
    @NotNull
    private String prompt;
    @NotNull
    private String id;

    // getter and setter
}
