package demo.springai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppliedFilter {

    private String subject;

    private Integer grade;

    private Integer lessonNumber;

    private String educationLevel;

    private String chapterNumber;

    private Boolean hasExercises;

    private Boolean hasActivities;
}