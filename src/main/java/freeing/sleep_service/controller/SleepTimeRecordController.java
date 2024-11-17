package freeing.sleep_service.controller;


import freeing.sleep_service.repository.SleepTimeRecordStatusEntity;
import freeing.sleep_service.security.JwtTokenProvider;
import freeing.sleep_service.service.SleepTimeRecordService;
import freeing.sleep_service.service.SleepTimeRecordStatusService;
import freeing.sleep_service.vo.RequestSleepTimeRecord;
import freeing.sleep_service.vo.ResponseTimeRecordDay;
import freeing.sleep_service.vo.ResponseTimeRecordStatus;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/sleep-service")
@RequiredArgsConstructor
@Slf4j
public class SleepTimeRecordController {
    private final SleepTimeRecordService sleepTimeRecordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SleepTimeRecordStatusService sleepTimeRecordStatusService;

    @PostMapping("/sleep-time/record")
    public ResponseEntity<String> saveSleepRecord(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @Valid @RequestBody RequestSleepTimeRecord requestSleepTimeRecord,
            BindingResult bindingResult) {

        // 검증 실패 시
        if (bindingResult.hasErrors()) {
            // 모든 오류를 담을 StringBuilder 생성
            StringBuilder sb = new StringBuilder("유효성 검사 오류:\n");
            bindingResult.getFieldErrors().forEach(error -> {
                sb.append("필드: ").append(error.getField())  // 필드명
                        .append(", 오류 메시지: ").append(error.getDefaultMessage())  // 오류 메시지
                        .append("\n");
            });
            // 모든 에러 메시지를 한꺼번에 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
        }

        // 유효성 검사를 통과한 경우
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 수면 기록 저장
        sleepTimeRecordService.saveSleepTimeRecord(userId, requestSleepTimeRecord);

        log.info("수면 기록 완료");
        return ResponseEntity.status(HttpStatus.OK).body("수면 기록이 저장되었습니다.");
    }

    @PatchMapping("/time-record/status/on")
    public ResponseEntity<String>changeStatusOn(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        SleepTimeRecordStatusEntity sleepTimeRecordStatusEntity
                = sleepTimeRecordStatusService.changeStatusOn(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(sleepTimeRecordStatusEntity.getUserId()+"의 수면 시간 기록이 활성화 되었습니다.");
    }

    @PatchMapping("/time-record/status/off")
    public ResponseEntity<String>changeStatusOff(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        SleepTimeRecordStatusEntity sleepTimeRecordStatusEntity
                =sleepTimeRecordStatusService.changeStatusOff(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(sleepTimeRecordStatusEntity.getUserId()+"의 수면 시간 기록이 비활성화 되었습니다.");

    }

    @GetMapping("/time-record")
    public ResponseEntity<ResponseTimeRecordStatus> getStatus(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                                  String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        ResponseTimeRecordStatus responseTimeRecordStatus = new ResponseTimeRecordStatus();
        responseTimeRecordStatus.setStatus(sleepTimeRecordStatusService.getStatus(userId));

        return ResponseEntity.status(HttpStatus.OK).body(responseTimeRecordStatus);



    }

    @GetMapping("/time-record/day")
    public ResponseEntity<ResponseTimeRecordDay> getTimeRecordDay(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                              String authorizationHeader,
                                                                  @RequestParam LocalDate queryDate){

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        ResponseTimeRecordDay responseTimeRecordDay = new ResponseTimeRecordDay();
        responseTimeRecordDay.setStatus(sleepTimeRecordStatusService.getStatus(userId));
        responseTimeRecordDay.setCompleted(sleepTimeRecordService.isExistDay(userId, queryDate));

        return ResponseEntity.status(HttpStatus.OK).body(responseTimeRecordDay);



    }
}





