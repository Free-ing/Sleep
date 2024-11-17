package freeing.sleep_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SleepRoutineRecordDto {
    private LocalDate completeDay;

    private Long sleepRoutineId;

    private Long userId;
}
