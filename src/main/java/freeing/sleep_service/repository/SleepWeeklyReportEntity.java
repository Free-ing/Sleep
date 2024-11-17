package freeing.sleep_service.repository;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sleep_weekly_report")
@Data
@NoArgsConstructor
public class SleepWeeklyReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sleepWeeklyReportId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "avg_sleep_time", nullable = false)
    private LocalTime avgSleepTime;

    @Column(name = "avg_wake_up_time", nullable = false)
    private LocalTime avgWakeUpTime;

    @Column(name = "avg_sleep_duration", nullable = false)
    private Long avgSleepDurationInMinutes;

    @Column(name = "report_start_date", nullable = false)
    private LocalDate reportStartDate;

    @Column(name = "report_end_date", nullable = false)
    private LocalDate reportEndDate;

    @Column(name = "ai_feedback", nullable = true, columnDefinition = "TEXT")
    private String aiFeedback;



}