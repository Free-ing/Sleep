package freeing.sleep_service.repository;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "disabled_period")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisabledPeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sleep_routine_id", nullable = false)
    @JsonBackReference // 자식에서 부모 참조
    private SleepRoutineEntity sleepRoutine;

    @Column(name = "disabled_start_date", nullable = false)
    private LocalDateTime disabledStartDate;

    @Column(name = "disabled_end_date", nullable = true)
    private LocalDateTime disabledEndDate;
}
