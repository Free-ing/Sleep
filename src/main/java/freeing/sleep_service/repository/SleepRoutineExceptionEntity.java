package freeing.sleep_service.repository;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sleep_routine_exception")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleepRoutineExceptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exception_id")
    private Long exceptionId;

    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;

    // SleepRoutineExceptionEntity.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sleep_routine_id", nullable = false)
    @JsonBackReference
    private SleepRoutineEntity sleepRoutine;

}
