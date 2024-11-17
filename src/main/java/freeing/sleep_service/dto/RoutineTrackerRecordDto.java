package freeing.sleep_service.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RoutineTrackerRecordDto {
    private Long routineId;
    private String routineName;
    private String url;
}
