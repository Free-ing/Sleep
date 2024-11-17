package freeing.sleep_service.service;

import freeing.sleep_service.dto.RoutineTrackerRecordDto;
import freeing.sleep_service.dto.SleepRoutineAddDto;
import freeing.sleep_service.dto.SleepRoutineRecordDto;
import freeing.sleep_service.dto.SleepRoutineWithStatusDto;
import freeing.sleep_service.error.EntityNotFoundException;
import freeing.sleep_service.error.ForbiddenException;
import freeing.sleep_service.error.NotFoundException;
import freeing.sleep_service.repository.*;
import freeing.sleep_service.vo.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class SleepRoutineServiceImpl implements  SleepRoutineService{
    private final SleepRoutineRepository sleepRoutineRepository;
    private final SleepRoutineRecordRepository sleepRoutineRecordRepository;

    private final SleepTimeRecordRepository sleepTimeRecordRepository;

    private final SleepRoutineExceptionRepository sleepRoutineExceptionRepository;
    @Autowired
    public SleepRoutineServiceImpl(SleepRoutineRepository sleepRoutineRepository,
                                   SleepRoutineRecordRepository sleepRoutineRecordRepository,
                                   SleepTimeRecordRepository sleepTimeRecordRepository,
                                   SleepRoutineExceptionRepository sleepRoutineExceptionRepository) {
        this.sleepRoutineRepository = sleepRoutineRepository;
        this.sleepRoutineRecordRepository = sleepRoutineRecordRepository;
        this.sleepTimeRecordRepository = sleepTimeRecordRepository;
        this.sleepRoutineExceptionRepository = sleepRoutineExceptionRepository;
    }

    @Override
    public SleepRoutineEntity addRoutine(SleepRoutineAddDto sleepRoutineAddDto) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        SleepRoutineEntity sleepRoutineEntity = mapper.map(sleepRoutineAddDto, SleepRoutineEntity.class);

        sleepRoutineEntity.setCreateDate(LocalDateTime.now());
//        sleepRoutineEntity.setOnDate(LocalDateTime.now());
        SleepRoutineEntity returnSleepRoutineEntity = sleepRoutineRepository.save(sleepRoutineEntity);


        return returnSleepRoutineEntity;


    }

    @Override
    public SleepRoutineRecordEntity recordRoutine(SleepRoutineRecordDto sleepRoutineRecordDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // sleep_routine_id로 SleepRoutineEntity를 조회
        SleepRoutineEntity sleepRoutineEntity = sleepRoutineRepository.findById(sleepRoutineRecordDto.getSleepRoutineId())
                .orElseThrow(() -> new NotFoundException("수면 루틴을 찾을 수 없습니다."));

        // sleepRoutineEntity의 userId가 현재 요청한 사용자의 userId와 일치하는지 검증
        if (!sleepRoutineEntity.getUserId().equals(sleepRoutineRecordDto.getUserId())) {
            throw new ForbiddenException("해당 루틴은 요청한 사용자에게 속하지 않습니다.");
        }

        // SleepRoutineRecordEntity 생성 및 매핑
        SleepRoutineRecordEntity sleepRoutineRecordEntity
                = mapper.map(sleepRoutineRecordDto, SleepRoutineRecordEntity.class);
        sleepRoutineRecordEntity.setSleepRoutineEntity(sleepRoutineEntity); // SleepRoutineEntity 설정

        return sleepRoutineRecordRepository.save(sleepRoutineRecordEntity);
    }


    @Override
    public void deleteSleepRoutineRecord(RequestDeleteSleepRoutineRecord request, Long userId) {
        // 삭제하려는 루틴 기록이 존재하는지 확인
        boolean exists = sleepRoutineRecordRepository
                .existsByCompleteDayAndSleepRoutineEntity_SleepRoutineIdAndSleepRoutineEntity_UserId(
                        request.getCompleteDay(),
                        request.getSleepRoutineId(),
                        userId
                );

        if (!exists) {
            throw new NotFoundException("삭제할 루틴 기록이 존재하지 않습니다.");        }

        // 기록이 존재하면 삭제
        sleepRoutineRecordRepository.deleteByCompleteDayAndSleepRoutineEntity_SleepRoutineIdAndUserId(
                request.getCompleteDay(), request.getSleepRoutineId(), userId);
    }


    @Override
    public List<SleepRoutineWithStatusDto> getRoutinesForDayAndDate(Long userId, DayOfWeek dayOfWeek, LocalDate queryDate) {
        // 특정 요일에 해당하는 사용자의 루틴 조회
        List<SleepRoutineEntity> routines = sleepRoutineRepository.findRoutinesByDayOfWeek(userId, dayOfWeek.name());

        // 해당 날짜에 대한 루틴 기록 확인
        List<SleepRoutineWithStatusDto> result = new ArrayList<>();
        for (SleepRoutineEntity routine : routines) {
            // 예외 날짜에 해당하는지 확인
            boolean isExceptionDate = sleepRoutineExceptionRepository
                    .existsBySleepRoutineAndExceptionDate(routine, queryDate);

            // 예외 날짜가 아니면 루틴을 추가
            if (!isExceptionDate) {
                boolean isCompleted = sleepRoutineRecordRepository
                        .existsBySleepRoutineEntityAndCompleteDay(routine, queryDate);
                result.add(new SleepRoutineWithStatusDto(routine, isCompleted));
            }
        }

        return result;
    }

    @Override
    public List<SleepRoutineEntity> getAllRoutinesForUser(Long userId) {
        // 특정 사용자의 모든 루틴 조회
        return sleepRoutineRepository.findAllByUserId(userId);
    }

    @Override
    public String getRoutineExplanation(Long routineId, Long userId) {
        // 루틴 조회
        SleepRoutineEntity routine = sleepRoutineRepository.findById(routineId)
                .orElseThrow(() -> new NotFoundException("해당 루틴을 찾을 수 없습니다."));
        if (!routine.getUserId().equals(userId)) {
            throw new ForbiddenException("해당 루틴은 요청한 사용자에게 속하지 않습니다.");
        }
        // 설명이 null인 경우 null 반환
        return routine.getExplanation();
    }

    // 루틴을 활성화하는 메서드 (status를 true로 변경)
    @Override
    public void activateRoutine(Long userId, Long routineId) {
        SleepRoutineEntity routine = sleepRoutineRepository.findByUserIdAndSleepRoutineId(userId, routineId)
                .orElseThrow(() -> new NotFoundException("해당 루틴을 찾을 수 없습니다."));

        routine.setStatus(true);
//        routine.setOnDate(LocalDateTime.now());


        // 가장 최근의 비활성화 기간 종료
        DisabledPeriodEntity period = routine.getDisabledPeriods()
                .stream()
                .filter(p -> p.getDisabledEndDate() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active disabled period"));

        period.setDisabledEndDate(LocalDateTime.now());
        sleepRoutineRepository.save(routine);
    }

    // 루틴을 비활성화하는 메서드 (status를 false로 변경)
    @Override
    public void deactivateRoutine(Long userId, Long routineId) {
        SleepRoutineEntity routine = sleepRoutineRepository.findByUserIdAndSleepRoutineId(userId, routineId)
                .orElseThrow(() -> new NotFoundException("해당 루틴을 찾을 수 없습니다."));

        routine.setStatus(false);

        // 비활성화 시작
        DisabledPeriodEntity period = new DisabledPeriodEntity();
        period.setSleepRoutine(routine);
        period.setDisabledStartDate(LocalDateTime.now());
        routine.getDisabledPeriods().add(period);

//        routine.setOffDate(LocalDateTime.now());
        sleepRoutineRepository.save(routine);
    }

    @Override
    public void removeRoutine(Long userId, Long routineId) {
        sleepRoutineRepository.findByUserIdAndSleepRoutineId(userId, routineId)
                .orElseThrow(()-> new NotFoundException("해당 루틴을 찾을 수 없습니다."));
        // 루틴 기록 삭제
        sleepRoutineRecordRepository
                .deleteBySleepRoutineEntity_SleepRoutineIdAndUserId(routineId, userId);

        // 루틴 삭제
        sleepRoutineRepository.deleteBySleepRoutineIdAndUserId(routineId, userId);
    }

    @Override
    public void deleteSleepRoutineRecordAll(Long userId) {


        sleepRoutineRecordRepository.deleteByUserId(userId);

        sleepRoutineRepository.deleteByUserId(userId);
    }


    // 루틴 정보 수정 (부분 업데이트, PATCH)
    @Override
    public void updateRoutine(Long userId, Long routineId, RequestSleepRoutineUpdate updateDto) {
        SleepRoutineEntity routine = sleepRoutineRepository.findByUserIdAndSleepRoutineId(userId, routineId)
                .orElseThrow(() -> new NotFoundException("해당 루틴을 찾을 수 없습니다."));

        // null이 아닌 필드만 업데이트
        if (updateDto.getSleepRoutineName() != null) {
            routine.setSleepRoutineName(updateDto.getSleepRoutineName());
        }
        if (updateDto.getStartTime() != null) {
            routine.setStartTime(updateDto.getStartTime());
        }
        if (updateDto.getEndTime() != null) {
            routine.setEndTime(updateDto.getEndTime());
        }
        if (updateDto.getMonday() != null) {
            routine.setMonday(updateDto.getMonday());
        }
        if (updateDto.getTuesday() != null) {
            routine.setTuesday(updateDto.getTuesday());
        }
        if (updateDto.getWednesday() != null) {
            routine.setWednesday(updateDto.getWednesday());
        }
        if (updateDto.getThursday() != null) {
            routine.setThursday(updateDto.getThursday());
        }
        if (updateDto.getFriday() != null) {
            routine.setFriday(updateDto.getFriday());
        }
        if (updateDto.getSaturday() != null) {
            routine.setSaturday(updateDto.getSaturday());
        }
        if (updateDto.getSunday() != null) {
            routine.setSunday(updateDto.getSunday());
        }
        if (updateDto.getExplanation() != null) {
            routine.setExplanation(updateDto.getExplanation());
        }
        if (updateDto.getStatus() != null) {
            routine.setStatus(updateDto.getStatus());
        }

        if(updateDto.getUrl() != null){
            routine.setUrl(updateDto.getUrl());
        }

        // 변경 사항 저장
        sleepRoutineRepository.save(routine);
    }

//    @Override
//    public List<SleepRoutineRecordResponse> getRoutineRecordsWithinDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
//        List<SleepRoutineRecordEntity> records
//                = sleepRoutineRecordRepository
//                .findByUserIdAndCompleteDayBetween(userId, startDate, endDate);
//
//
//
//        return records.stream()
//                .map(record -> new SleepRoutineRecordResponse(
//                        record.getSleepRoutineEntity().getSleepRoutineId(),
//                        record.getSleepRoutineEntity().getSleepRoutineName(),
//                        record.getCompleteDay(),
//                        record.getSleepRoutineEntity().getUrl()
//                ))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<SleepRoutineEntity> addDefaultRoutine(Long userId) {

        List<SleepRoutineEntity> sleepDefaultService = new ArrayList<>();

        sleepDefaultService.add(createSleepRoutineEntity(
                "따뜻한 물 마시기",
                "https://freeingimage.s3.ap-northeast-2.amazonaws.com/water.png",
                "따뜻한 물을 마시는 것은 양질의 수면을 돕는 여러 가지 이유가 있습니다. 우선, 따뜻한 물을 마시면 체온이 약간 상승하고...",
                userId
        ));

        sleepDefaultService.add(createSleepRoutineEntity(
                "6시간 전 커피 금지",
                "https://freeingimage.s3.ap-northeast-2.amazonaws.com/coffee.png",
                "취침 6시간 전 커피를 마시지 않는 것은 양질의 수면에 도움이 됩니다. 커피에 포함된 카페인은 각성 효과가 있어...",
                userId
        ));

        sleepDefaultService.add(createSleepRoutineEntity(
                "30분 전 폰 금지",
                "https://freeingimage.s3.ap-northeast-2.amazonaws.com/phone.png",
                "취침 30분 전 핸드폰 사용을 피하는 것은 양질의 수면을 돕습니다. 핸드폰에서 나오는 블루라이트는 멜라토닌 분비를 억제하여...",
                userId
        ));

        sleepDefaultService.add(createSleepRoutineEntity(
                "짐 미리 챙겨두기",
                "https://freeingimage.s3.ap-northeast-2.amazonaws.com/bag.png",
                "짐을 미리 챙겨두면 양질의 수면에 도움이 됩니다. 잠자기 전에 다음 날을 위한 준비가 끝나 있으면 아침에 해야 할 일을...",
                userId
        ));

        return sleepRoutineRepository.saveAll(sleepDefaultService);
    }

    @Override
    public void deleteAll(Long userId) {


        List<SleepRoutineRecordEntity> list2 = sleepRoutineRecordRepository.findByUserId(userId);

        sleepRoutineRecordRepository.deleteAll(list2);

        List<SleepRoutineEntity> list1 = sleepRoutineRepository.findAllByUserId(userId);

        sleepRoutineRepository.deleteAll(list1);
    }

    @Override
    public List<SleepRoutineRecordResponse> getRoutineRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        return sleepRoutineRecordRepository.findByUserIdAndCompleteDayBetween(userId, startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                        record -> new RoutineTrackerRecordDto(
                                record.getSleepRoutineEntity().getSleepRoutineId(),
                                record.getSleepRoutineEntity().getSleepRoutineName(),
                                record.getSleepRoutineEntity().getUrl()
                        ),
                        Collectors.mapping(record -> record.getCompleteDay(), Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(entry -> new SleepRoutineRecordResponse(
                        entry.getKey().getRoutineId(),
                        entry.getKey().getRoutineName(),
                        entry.getValue(), // completeDay 리스트
                        entry.getKey().getUrl()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<LocalDate> getTimeRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        return sleepTimeRecordRepository.findByUserIdAndRecordDayBetween(userId, startDate, endDate)
                .stream()
                .map(record -> record.getRecordDay()) // recordDay만 수집
                .collect(Collectors.toList());
    }


    // Helper methods
    private void setAllDays(SleepRoutineEntity entity) {
        entity.setMonday(true);
        entity.setTuesday(true);
        entity.setWednesday(true);
        entity.setThursday(true);
        entity.setFriday(true);
        entity.setSaturday(true);
        entity.setSunday(true);
    }

    private SleepRoutineEntity createSleepRoutineEntity(String name, String url, String explanation, Long userId) {
        // 비활성화 시작


        SleepRoutineEntity entity = new SleepRoutineEntity();
        entity.setSleepRoutineName(name);
        entity.setUrl(url);
        entity.setExplanation(explanation);
        entity.setUserId(userId);
        entity.setStatus(false);
//        entity.setOffDate(LocalDateTime.now());
        entity.setCreateDate(LocalDateTime.now());
        setAllDays(entity);

        DisabledPeriodEntity period = new DisabledPeriodEntity();
        period.setSleepRoutine(entity);
        period.setDisabledStartDate(LocalDateTime.now());
        entity.getDisabledPeriods().add(period);

        return entity;
    }

    @Override
    public List<DateExistenceResponse> getDatesWithExistingRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        // SleepRoutineRecord 날짜 조회
        List<LocalDate> routineDates = sleepRoutineRecordRepository.findByUserIdAndCompleteDayBetween(userId, startDate, endDate)
                .stream()
                .map(record -> record.getCompleteDay())
                .collect(Collectors.toList());

        // SleepTimeRecord 날짜 조회
        List<LocalDate> timeRecordDates = sleepTimeRecordRepository.findByUserIdAndRecordDayBetween(userId, startDate, endDate)
                .stream()
                .map(record -> record.getRecordDay())
                .collect(Collectors.toList());

        // 두 리스트를 합쳐 중복 제거
        Set<LocalDate> combinedDates = Stream.concat(routineDates.stream(), timeRecordDates.stream())
                .collect(Collectors.toSet());

        // 날짜 리스트를 DateExistenceResponse 객체로 변환
        return combinedDates.stream()
                .map(DateExistenceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void addExceptionDate(Long sleepRoutineId, LocalDate exceptionDate) {
        SleepRoutineEntity routine = sleepRoutineRepository.findById(sleepRoutineId)
                .orElseThrow(() -> new EntityNotFoundException("Routine not found"));

        SleepRoutineExceptionEntity exception = new SleepRoutineExceptionEntity();
        exception.setSleepRoutine(routine);
        exception.setExceptionDate(exceptionDate);

        sleepRoutineExceptionRepository.save(exception);
    }

}
