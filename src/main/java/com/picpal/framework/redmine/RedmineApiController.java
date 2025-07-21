package com.picpal.framework.redmine;

import com.picpal.framework.redmine.dto.RedmineIssueDTO;
import com.picpal.framework.redmine.service.RedmineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/redmine")
@RequiredArgsConstructor
@Tag(name = "Redmine API", description = "Redmine 이슈 연동 API")
public class RedmineApiController {
    private final RedmineService redmineService;

    @PostMapping("/issue")
    @Operation(summary = "Redmine 이슈 등록", description = "Redmine에 이슈를 등록합니다.")
    public ResponseEntity<Map<String, Object>> createRedmineIssue(@RequestBody RedmineIssueDTO issueDTO) {
        log.info("Redmine 이슈 등록 요청: {}", issueDTO);
        Integer issueId = redmineService.createIssue(issueDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("issueId", issueId);
        return ResponseEntity.ok(result);
    }
} 