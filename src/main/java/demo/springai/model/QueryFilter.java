package demo.springai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilter {
    private String subject;        // "tin_hoc", "toan", "van", etc.
    private Integer grade;         // 6, 7, 8, 9, 10, 11, 12
    private Integer lessonNumber;  // Số bài học
    private String educationLevel; // "THCS", "THPT"
    private String chapterNumber;  // Số chương
    private Boolean hasExercises;  // Có bài tập hay không
    private Boolean hasActivities; // Có hoạt động hay không
}