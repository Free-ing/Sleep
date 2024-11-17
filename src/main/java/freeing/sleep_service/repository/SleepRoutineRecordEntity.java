package freeing.sleep_service.repository;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sleep_routine_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class    SleepRoutineRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_routine_record_id", nullable = false)
    private Long sleepRoutineRecordId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "complete_day", nullable = false)
    private LocalDate completeDay;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sleep_routine_id", nullable = false)
    private SleepRoutineEntity sleepRoutineEntity;
}
