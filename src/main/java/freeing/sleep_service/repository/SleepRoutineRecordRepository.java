package freeing.sleep_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
public interface SleepRoutineRecordRepository extends JpaRepository<SleepRoutineRecordEntity, Long> {
    void deleteByCompleteDayAndSleepRoutineEntity_SleepRoutineIdAndUserId(LocalDate completeDay, Long sleepRoutineId, Long userId);

    boolean existsBySleepRoutineEntityAndCompleteDay(SleepRoutineEntity sleepRoutine, LocalDate completeDay);

    void deleteBySleepRoutineEntity_SleepRoutineIdAndUserId(Long routineId, Long userId);

    void deleteByUserId(Long userId);

    // 새로운 exists 메서드 추가
    boolean existsByCompleteDayAndSleepRoutineEntity_SleepRoutineIdAndSleepRoutineEntity_UserId(
            LocalDate completeDay, Long sleepRoutineId, Long userId);


    List<SleepRoutineRecordEntity> findByUserIdAndCompleteDayBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<SleepRoutineRecordEntity> findByUserId(Long userId);
}
