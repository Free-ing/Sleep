package freeing.sleep_service.controller;

import freeing.sleep_service.service.SleepReportService;
import freeing.sleep_service.service.SleepRoutineService;
import freeing.sleep_service.service.SleepTimeRecordService;
import freeing.sleep_service.service.SleepTimeRecordStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sleep-service")
public class SleepDeleteController {
    private final SleepReportService sleepReportService;
    private final SleepRoutineService sleepRoutineService;
    private final SleepTimeRecordService sleepTimeRecordService;
    private final SleepTimeRecordStatusService sleepTimeRecordStatusService;


    @DeleteMapping("/delete/all/{userId}")
    public ResponseEntity<String>deleteAll(@PathVariable Long userId){

        sleepReportService.deleteAll(userId);
        log.info("리포트 삭제: "+userId);
        sleepRoutineService.deleteAll(userId);
        log.info("수면 루틴 기록 삭제: "+userId);
        sleepTimeRecordService.deleteAll(userId);
        log.info("수면 시간 기록 삭제: "+userId);
        sleepTimeRecordStatusService.deleteTimeRecordStatus(userId);
        log.info("수면 시간 기록 유무 삭제: "+userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("성공적으로 삭제되었습니다.");

    }

    @DeleteMapping("/reset/all/{userId}")
    public ResponseEntity<String>resetAll(@PathVariable Long userId){
        sleepReportService.deleteAll(userId);
        log.info("리포트 삭제: "+userId);
        sleepRoutineService.deleteAll(userId);
        log.info("수면 루틴 기록 삭제: "+userId);
        sleepTimeRecordService.deleteAll(userId);
        log.info("수면 시간 기록 삭제: "+userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("성공적으로 초기화 되었습니다.");
    }


}
