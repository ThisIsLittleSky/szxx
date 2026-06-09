package com.szxx.controller.client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.common.Result;
import com.szxx.dto.request.LearningReportRequest;
import com.szxx.dto.response.LearningRankItemResponse;
import com.szxx.dto.response.LearningRecordResponse;
import com.szxx.dto.response.LearningStatsResponse;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.LearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/report")
    public Result<Void> report(@Valid @RequestBody LearningReportRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        learningService.report(request.getMaterialId(), userId,
                request.getDuration(), request.getCompleted());
        return Result.success();
    }

    @GetMapping("/records")
    public Result<IPage<LearningRecordResponse>> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(learningService.getRecords(userId, page, size));
    }

    @GetMapping("/stats")
    public Result<LearningStatsResponse> getStats() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(learningService.getStats(userId));
    }

    @GetMapping("/ranking")
    public Result<List<LearningRankItemResponse>> getRanking(
            @RequestParam(defaultValue = "20") int limit) {
        return Result.success(learningService.getRanking(limit));
    }
}
