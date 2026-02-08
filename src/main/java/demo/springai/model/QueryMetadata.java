package demo.springai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMetadata {

    private Integer documentsFound;

    private Integer documentsUsed;

    private Double averageSimilarity;

    private Long processingTimeMs;

    private AppliedFilter appliedFilter;
}