package freeing.sleep_service.controller;

import freeing.sleep_service.dto.SleepRoutineAddDto;
import freeing.sleep_service.dto.SleepRoutineRecordDto;
import freeing.sleep_service.dto.SleepRoutineWithStatusDto;
import freeing.sleep_service.error.BadRequestException;
import freeing.sleep_service.repository.SleepRoutineEntity;
import freeing.sleep_service.repository.SleepRoutineRecordEntity;
import freeing.sleep_service.security.JwtTokenProvider;
import freeing.sleep_service.service.SleepRoutineService;
import freeing.sleep_service.service.SleepTimeRecordStatusService;
import freeing.sleep_service.vo.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sleep-service")
@Slf4j
public class SleepController {
    private  final Environment environment;

    private final JwtTokenProvider jwtTokenProvider;

    private final SleepRoutineService sleepRoutineService;
    private final SleepTimeRecordStatusService sleepTimeRecordStatusService;
    @Autowired
    public SleepController(Environment environment,
                           JwtTokenProvider jwtTokenProvider,
                           SleepRoutineService sleepRoutineService, SleepTimeRecordStatusService sleepTimeRecordStatusService) {
        this.environment = environment;
        this.jwtTokenProvider = jwtTokenProvider;
        this.sleepRoutineService = sleepRoutineService;
        this.sleepTimeRecordStatusService = sleepTimeRecordStatusService;
    }

    @GetMapping("/health_check")
    public String status(){
        return String.format("Sleep service is working fine on PORT %s"+" / 서버 시간은 "
                        + LocalDateTime.now(),
                environment.getProperty("local.server.port"));
    }

    @PostMapping("/routine/add/default/{userId}")
    public ResponseEntity<String> addDefaultSleepRoutine(@PathVariable Long userId){
        log.debug("addDefaultSleepRoutine 호출됨, userId: {}", userId); // 요청이 들어왔을 때 로그

        // 서비스 호출 전 로그
        log.debug("sleepRoutineService.addDefaultRoutine 호출 시작");
        sleepRoutineService.addDefaultRoutine(userId);
        log.debug("sleepRoutineService.addDefaultRoutine 호출 완료"); // 서비스 호출 완료 후 로그

        log.info("기본 수면 루틴이 성공적으로 추가되었습니다. UserId: {}", userId);

        sleepTimeRecordStatusService.addSleepTimeRecordStatus(userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("sleep-service의 기본 기능이 성공적으로 추가되었습니다.");
    }


    @PostMapping("/routine/add")
    public ResponseEntity<ResponseSleepRoutineAdd> addSleepRoutine
            (@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
             @RequestBody @Valid RequestSleepRoutineAdd requestSleepRoutineAdd){

        // 모든 요일 값이 false인지 확인하는 유효성 검사
        if (areAllDaysFalse(requestSleepRoutineAdd)) {
            String errorMessage = "적어도 하나의 요일은 true여야 합니다.";
            log.warn("유효성 검사 실패: {}", errorMessage);
            throw new BadRequestException(errorMessage);

        }
        String token = authorizationHeader.substring(7);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        SleepRoutineAddDto sleepRoutineAddDto =  mapper.map(requestSleepRoutineAdd, SleepRoutineAddDto.class);
        sleepRoutineAddDto.setUserId(jwtTokenProvider.getUserIdFromToken(token));

       ResponseSleepRoutineAdd responseSleepRoutineAdd = mapper
               .map(sleepRoutineService.addRoutine(sleepRoutineAddDto),
               ResponseSleepRoutineAdd.class);

       log.info(requestSleepRoutineAdd.getSleepRoutineName()+" 추가 성공");
       return ResponseEntity.status(HttpStatus.CREATED).body(responseSleepRoutineAdd);



    }

    // 모든 요일 값이 false인지 확인하는 메서드
    private boolean areAllDaysFalse(RequestSleepRoutineAdd request) {
        return Boolean.FALSE.equals(request.getMonday()) &&
                Boolean.FALSE.equals(request.getTuesday()) &&
                Boolean.FALSE.equals(request.getWednesday()) &&
                Boolean.FALSE.equals(request.getThursday()) &&
                Boolean.FALSE.equals(request.getFriday()) &&
                Boolean.FALSE.equals(request.getSaturday()) &&
                Boolean.FALSE.equals(request.getSunday());
    }

    @PostMapping("/routine/record")
    public ResponseEntity<ResponseSleepRoutineRecord> recordSleepRoutine(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                                             String authorizationHeader,
                                                                         @RequestBody @Valid
                                                   RequestSleepRoutineRecord requestSleepRoutineRecord){
        String token = authorizationHeader.substring(7);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        SleepRoutineRecordDto sleepRoutineRecordDto =
                mapper.map(requestSleepRoutineRecord, SleepRoutineRecordDto.class);
        sleepRoutineRecordDto.setUserId(jwtTokenProvider.getUserIdFromToken(token));

        SleepRoutineRecordEntity sleepRoutineRecordEntity = sleepRoutineService.recordRoutine(sleepRoutineRecordDto);
        ResponseSleepRoutineRecord responseSleepRoutineRecord=
                mapper.map(sleepRoutineRecordEntity,ResponseSleepRoutineRecord.class);

        responseSleepRoutineRecord.setSleepRoutineId(sleepRoutineRecordDto.getSleepRoutineId());


        log.info("루틴 기록 완료 / 루틴 id:"+sleepRoutineRecordDto.getSleepRoutineId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseSleepRoutineRecord);
    }

    @DeleteMapping("/routine/record")
    public ResponseEntity<String> deleteSleepRoutineRecord(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                               String authorizationHeader,
                                                           @RequestBody @Valid
                                                           RequestDeleteSleepRoutineRecord
                                                                   requestDeleteSleepRoutineRecord) {
        String token = authorizationHeader.substring(7);
        Long userId=jwtTokenProvider.getUserIdFromToken(token);
        sleepRoutineService.deleteSleepRoutineRecord(requestDeleteSleepRoutineRecord,userId);

        log.info("삭제 완료");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("삭제가 완료되었습니다.");
    }

    @GetMapping("/routine/day")
    public ResponseEntity<List<SleepRoutineWithStatusDto>> getRoutinesForDay(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorizationHeader,
            @RequestParam String dayOfWeek,
            @RequestParam LocalDate queryDate){
        String token = authorizationHeader.substring(7);
        Long userId=jwtTokenProvider.getUserIdFromToken(token);
        // 문자열을 DayOfWeek로 변환
        DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        List<SleepRoutineWithStatusDto> routines =
                sleepRoutineService.getRoutinesForDayAndDate(userId, day, queryDate);

        // 조회 결과가 없으면 204 상태 코드 반환
        if (routines.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(routines);
    }


    // 회원별 모든 루틴 조회
    @GetMapping("/routine/all")
    public ResponseEntity<List<SleepRoutineEntity>> getAllRoutinesForUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 해당 사용자의 모든 루틴 조회
        List<SleepRoutineEntity> routines = sleepRoutineService.getAllRoutinesForUser(userId);

        if (routines.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(routines);
    }

    // 루틴 ID로 특정 루틴의 설명 조회
    @GetMapping("/routine/{routineId}/explanation")
    public ResponseEntity<String> getRoutineExplanation(@PathVariable Long routineId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                        String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        // 해당 루틴의 설명 조회
        String explanation = sleepRoutineService.getRoutineExplanation(routineId, userId);

        // 설명이 없을 경우 204 No Content 반환
        if (explanation == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 설명이 있을 경우 200 OK와 함께 설명 반환
        return ResponseEntity.status(HttpStatus.OK).body(explanation);
    }


    // 루틴을 활성화하는 엔드포인트 (status = true)
    @PatchMapping("/routine/{routineId}/activate")
    public ResponseEntity<String> activateRoutine(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable Long routineId) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        sleepRoutineService.activateRoutine(userId, routineId);
        log.info(routineId+": 활성화 완료");

        return ResponseEntity.status(HttpStatus.OK).body("루틴이 활성화되었습니다.");
    }

    // 루틴을 비활성화하는 엔드포인트 (status = false)
    @PatchMapping("/routine/{routineId}/deactivate")
    public ResponseEntity<String> deactivateRoutine(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable Long routineId) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        sleepRoutineService.deactivateRoutine(userId, routineId);
        log.info(routineId+": 비활성화 완료");

        return ResponseEntity.status(HttpStatus.OK).body("루틴이 비활성화되었습니다.");
    }

    @DeleteMapping("/routine/remove/{routineId}")
    public ResponseEntity<String> removeSleepRoutine(@PathVariable Long routineId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION)String authorizationHeader){
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        sleepRoutineService.removeRoutine(userId, routineId);

        return ResponseEntity.status(HttpStatus.OK).body("루틴과 루틴 기록이 삭제되었습니다.");
    }

    @DeleteMapping("/routine/remove/all")
    public ResponseEntity<String> removeSleepRoutineAll(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                            String authorizationHeader){

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        sleepRoutineService.deleteSleepRoutineRecordAll(userId);

        return ResponseEntity.status(HttpStatus.OK).body("회원의 전체 수면 루틴 삭제 완료");
    }
    // 루틴 정보 부분 업데이트 (PATCH)
    @PatchMapping("/routine/update/{routineId}")
    public ResponseEntity<String> updateRoutine(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable Long routineId,
            @RequestBody RequestSleepRoutineUpdate updateDto) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 루틴 정보 부분 수정
        sleepRoutineService.updateRoutine(userId, routineId, updateDto);

        return ResponseEntity.status(HttpStatus.OK).body("루틴 정보가 수정되었습니다.");
    }


//    @GetMapping("/routine/tracker")
//    public ResponseEntity<List<SleepRoutineRecordResponse>> getRoutineRecordsForDateRange(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
//            @RequestParam("startDate") LocalDate startDate,
//            @RequestParam("endDate") LocalDate endDate) {
//
//        String token = authorizationHeader.substring(7);
//        Long userId = jwtTokenProvider.getUserIdFromToken(token);
//
//        List<SleepRoutineRecordResponse> routineRecords = sleepRoutineService.getRoutineRecordsWithinDateRange(userId, startDate, endDate);
//        if(routineRecords.isEmpty()){
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(routineRecords);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(routineRecords);
//    }

    @GetMapping("/routine/tracker")
    public ResponseEntity<ResponseRoutineTracker> getSeparateRoutineAndTimeRecords(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<SleepRoutineRecordResponse> routineRecords = sleepRoutineService.getRoutineRecords(userId, startDate, endDate);
        List<LocalDate> timeRecords = sleepRoutineService.getTimeRecords(userId, startDate, endDate);

        ResponseRoutineTracker response = new ResponseRoutineTracker(routineRecords, timeRecords);

        if (routineRecords.isEmpty() && timeRecords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/routine/home/record-week")
    public ResponseEntity<List<DateExistenceResponse>> getExistingDatesForUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<DateExistenceResponse> existingDates = sleepRoutineService.getDatesWithExistingRecords(userId, startDate, endDate);

        if (existingDates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(existingDates);
        }
        return ResponseEntity.status(HttpStatus.OK).body(existingDates);
    }



    @PostMapping("/routine/exception-date")
    public ResponseEntity<String> addExceptionDate(
            @RequestBody @Valid RequestAddExceptionDate requestAddExceptionDate
    ){

        sleepRoutineService.addExceptionDate(
                requestAddExceptionDate.getSleepRoutineId(),
                requestAddExceptionDate.getExceptionDate());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        requestAddExceptionDate.getSleepRoutineId()+
                                "의 오늘 쉬어가기 날짜: "
                                +requestAddExceptionDate.getExceptionDate());
    }


}
