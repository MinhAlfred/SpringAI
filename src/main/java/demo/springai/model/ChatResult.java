package demo.springai.model;

import lombok.*;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatResult {
    private String answer;
    private List<SourceInfo> sources;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}