package demo.springai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    private boolean success;

    private ChatResult answer;

    private String error;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}