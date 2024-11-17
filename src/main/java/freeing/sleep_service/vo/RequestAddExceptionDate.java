package freeing.sleep_service.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestAddExceptionDate {
    private LocalDate exceptionDate;
    private Long sleepRoutineId;
}
