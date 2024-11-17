package freeing.sleep_service.repository;

import freeing.sleep_service.dto.SleepStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sleep_time_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleepTimeRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가를 위한 설정
    @Column(name = "sleep_time_record_id")
    private Long sleepTimeRecordId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "wake_up_time", nullable = false)
    private LocalTime wakeUpTime;

    @Column(name = "sleep_time", nullable = false)
    private LocalTime sleepTime;

    @Column(name = "record_day", nullable = false)
    private LocalDate recordDay;

    @Column(name = "memo", nullable = true)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "sleep_status", nullable = false)
    private SleepStatus sleepStatus;  // Enum 값으로 변경

    @Column(name = "sleep_duration", nullable = false)
    private Long sleepDurationInMinutes;  // 수면 시간 (분 단위로 저장)



}
