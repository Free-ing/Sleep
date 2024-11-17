package freeing.sleep_service.dto;

import freeing.sleep_service.repository.SleepRoutineEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SleepRoutineWithStatusDto {
    private SleepRoutineEntity sleepRoutine;
    private boolean isCompleted;
}
