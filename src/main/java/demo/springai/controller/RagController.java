package demo.springai.controller;

import demo.springai.model.*;
import demo.springai.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RagController {

    private final RagService ragService;

    /**
     * Query cơ bản không filter
     */
    @PostMapping("/query")
    public ResponseEntity<QueryResponse> query(@Valid @RequestBody QueryRequest request) {
        log.info("Received query: {}", request.getQuestion());
        try {
            String answer = ragService.query(request.getQuestion());
            return ResponseEntity.ok(QueryResponse.builder()
                    .success(true)
                    .answer(answer)
                    .build());
        } catch (Exception e) {
            log.error("Error processing query", e);
            return ResponseEntity.status(500)
                    .body(QueryResponse.builder()
                            .success(false)
                            .error(e.getMessage())
                            .build());
        }
    }

    /**
     * Query với filter
     */
    @PostMapping("/query-filtered")
    public ResponseEntity<QueryResponse> queryFiltered(
            @Valid @RequestBody FilteredQueryRequest request) {
        log.info("Received filtered query: {} with filters: subject={}, grade={}, lesson={}",
                request.getQuestion(), request.getSubject(),
                request.getGrade(), request.getLessonNumber());
        try {
            QueryFilter filter = QueryFilter.builder()
                    .subject(request.getSubject())
                    .grade(request.getGrade())
                    .lessonNumber(request.getLessonNumber())
                    .educationLevel(request.getEducationLevel())
                    .chapterNumber(request.getChapterNumber())
                    .hasExercises(request.getHasExercises())
                    .hasActivities(request.getHasActivities())
                    .build();

            String answer = ragService.queryWithFilter(request.getQuestion(), filter);
            return ResponseEntity.ok(QueryResponse.builder()
                    .success(true)
                    .answer(answer)
                    .build());
        } catch (Exception e) {
            log.error("Error processing filtered query", e);
            return ResponseEntity.status(500)
                    .body(QueryResponse.builder()
                            .success(false)
                            .error(e.getMessage())
                            .build());
        }
    }

    /**
     * Query theo khoảng lớp
     */
    @PostMapping("/query-grade-range")
    public ResponseEntity<QueryResponse> queryGradeRange(
            @Valid @RequestBody GradeRangeRequest request) {
        log.info("Received grade range query: {} from grade {} to {}",
                request.getQuestion(), request.getMinGrade(), request.getMaxGrade());
        try {
            String answer = ragService.queryGradeRange(
                    request.getQuestion(),
                    request.getMinGrade(),
                    request.getMaxGrade(),
                    request.getSubject()
            );
            return ResponseEntity.ok(QueryResponse.builder()
                    .success(true)
                    .answer(answer)
                    .build());
        } catch (Exception e) {
            log.error("Error processing grade range query", e);
            return ResponseEntity.status(500)
                    .body(QueryResponse.builder()
                            .success(false)
                            .error(e.getMessage())
                            .build());
        }
    }

    /**
     * Query nhiều môn học
     */
    @PostMapping("/query-multi-subject")
    public ResponseEntity<QueryResponse> queryMultiSubject(
            @Valid @RequestBody MultiSubjectRequest request) {
        log.info("Received multi-subject query: {} for subjects: {}",
                request.getQuestion(), request.getSubjects());
        try {
            String answer = ragService.queryMultipleSubjects(
                    request.getQuestion(),
                    request.getSubjects(),
                    request.getGrade()
            );
            return ResponseEntity.ok(QueryResponse.builder()
                    .success(true)
                    .answer(answer)
                    .build());
        } catch (Exception e) {
            log.error("Error processing multi-subject query", e);
            return ResponseEntity.status(500)
                    .body(QueryResponse.builder()
                            .success(false)
                            .error(e.getMessage())
                            .build());
        }
    }
}