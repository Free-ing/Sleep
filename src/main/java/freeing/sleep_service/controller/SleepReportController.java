package freeing.sleep_service.controller;


import freeing.sleep_service.repository.SleepWeeklyReportEntity;
import freeing.sleep_service.repository.SleepTimeRecordEntity;
import freeing.sleep_service.security.JwtTokenProvider;
import freeing.sleep_service.service.SleepReportService;
import freeing.sleep_service.vo.SleepWeeklyReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sleep-service")
@RequiredArgsConstructor
@Slf4j
public class SleepReportController {

    private final SleepReportService sleepReportService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/weekly-report")
    public ResponseEntity<SleepWeeklyReportResponse> getWeeklyReport(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        // 1. AccessToken에서 userId 추출
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 2. 주간 리포트 및 일일 기록 조회
        SleepWeeklyReportResponse response = sleepReportService.getWeeklyReport(userId, startDate, endDate);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
