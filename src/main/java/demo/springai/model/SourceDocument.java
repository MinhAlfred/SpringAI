package demo.springai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceDocument {

    private String content;

    private Double similarity;

    private DocumentMetadata metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentMetadata {
        private String subject;
        private String subjectKey;
        private Integer grade;
        private String educationLevel;
        private Integer lessonNumber;
        private String lessonTitle;
        private String chapter;
        private Integer chapterNumber;
        private String chapterTitle;
        private String sectionType;
        private Boolean hasQuestions;
        private Boolean hasActivities;
        private Boolean hasExercises;
        private Boolean hasCode;
    }
}