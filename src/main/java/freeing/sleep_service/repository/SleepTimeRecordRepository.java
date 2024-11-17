package freeing.sleep_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SleepTimeRecordRepository extends JpaRepository<SleepTimeRecordEntity, Long> {

    Optional<SleepTimeRecordEntity> findByUserIdAndRecordDay(Long userId, LocalDate recordDay);

    List<SleepTimeRecordEntity> findByUserIdAndRecordDayBetween(Long userId, LocalDate startOfWeek, LocalDate endOfWeek);

    // 지난 일주일 동안 수면 기록이 있는 사용자 리스트 조회
    @Query("SELECT DISTINCT str.userId FROM SleepTimeRecordEntity str WHERE str.recordDay BETWEEN :startDate AND :endDate")
    List<Long> findDistinctUserIdsWithSleepRecords(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    List<SleepTimeRecordEntity> findByUserId(Long userId);

    boolean existsByUserIdAndRecordDay(Long userId, LocalDate recordDay);
}
