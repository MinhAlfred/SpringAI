package demo.springai.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatResult {
    private String answer;
    private Map<String,String> sources;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}