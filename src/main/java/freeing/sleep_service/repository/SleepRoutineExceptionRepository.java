package freeing.sleep_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SleepRoutineExceptionRepository extends JpaRepository<SleepRoutineExceptionEntity, Long> {
    List<SleepRoutineExceptionEntity> findBySleepRoutineAndExceptionDate(SleepRoutineEntity sleepRoutine, LocalDate exceptionDate);
    boolean existsBySleepRoutineAndExceptionDate(SleepRoutineEntity sleepRoutine, LocalDate exceptionDate);

}
