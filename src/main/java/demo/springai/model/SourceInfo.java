package demo.springai.model;

import lombok.*;
import org.checkerframework.checker.units.qual.N;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SourceInfo {

    private String documentTitle;
    private String pageNumber;
    private String documentId;
    private Double similarityScore;

    // constructor, getter
}