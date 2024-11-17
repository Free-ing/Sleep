package freeing.sleep_service.service;

import freeing.sleep_service.dto.SleepRoutineAddDto;
import freeing.sleep_service.dto.SleepRoutineRecordDto;
import freeing.sleep_service.dto.SleepRoutineWithStatusDto;
import freeing.sleep_service.repository.SleepRoutineEntity;
import freeing.sleep_service.repository.SleepRoutineRecordEntity;
import freeing.sleep_service.vo.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface SleepRoutineService {
    SleepRoutineEntity addRoutine(SleepRoutineAddDto sleepRoutineAddDto);
    SleepRoutineRecordEntity recordRoutine(SleepRoutineRecordDto sleepRoutineRecordDto);
    void deleteSleepRoutineRecord(RequestDeleteSleepRoutineRecord request, Long userId);
    List<SleepRoutineWithStatusDto> getRoutinesForDayAndDate(Long userId, DayOfWeek dayOfWeek, LocalDate queryDate);
    List<SleepRoutineEntity> getAllRoutinesForUser(Long userId);

    String getRoutineExplanation(Long routineId, Long userId);

    void activateRoutine(Long userId, Long routineId) ;
    void deactivateRoutine(Long userId, Long routineId) ;
    void removeRoutine(Long userId, Long routineId);

    void deleteSleepRoutineRecordAll(Long userId);
    void updateRoutine(Long userId, Long routineId, RequestSleepRoutineUpdate updateDto);

//    List<SleepRoutineRecordResponse> getRoutineRecordsWithinDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    List<SleepRoutineEntity> addDefaultRoutine(Long userId);

    void deleteAll(Long userId);
    List<SleepRoutineRecordResponse> getRoutineRecords(Long userId, LocalDate startDate, LocalDate endDate);
    public List<LocalDate> getTimeRecords(Long userId, LocalDate startDate, LocalDate endDate);
    List<DateExistenceResponse> getDatesWithExistingRecords(Long userId, LocalDate startDate, LocalDate endDate);
    void addExceptionDate(Long sleepRoutineId, LocalDate exceptionDate);
    }
