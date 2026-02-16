package demo.springai.model;

import lombok.*;
import org.checkerframework.checker.units.qual.N;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SourceInfo {
    private String documentTitle;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SourceInfo that = (SourceInfo) o;
        return Objects.equals(documentTitle, that.documentTitle) && Objects.equals(pageNumber, that.pageNumber) && Objects.equals(similarityScore, that.similarityScore) && Objects.equals(documentId, that.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentTitle, pageNumber, similarityScore, documentId);
    }

    private String pageNumber;
    private Double similarityScore;
    private String documentId;
    // constructor, getter

}