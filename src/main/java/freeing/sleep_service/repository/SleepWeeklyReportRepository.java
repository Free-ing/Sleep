package freeing.sleep_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SleepWeeklyReportRepository extends JpaRepository<SleepWeeklyReportEntity,Long> {

    Optional<SleepWeeklyReportEntity> findByUserIdAndReportStartDateAndReportEndDate(
            Long userId,
            LocalDate startDate,
            LocalDate endDate);

    // reportStartDate가 7일 전이고 reportEndDate가 1일 전이며 aiFeedback이 null인 리포트 조회
    @Query("SELECT r FROM SleepWeeklyReportEntity r " +
            "WHERE r.reportStartDate = :startDate " +
            "AND r.reportEndDate = :endDate " +
            "AND r.aiFeedback IS NULL")
    List<SleepWeeklyReportEntity> findReportsWithoutAiFeedback(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<SleepWeeklyReportEntity> findByUserId(Long userId);

}