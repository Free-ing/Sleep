package freeing.sleep_service.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 응답에 포함되지 않음
public class ResponseSleepRoutineAdd {
    private Long sleepRoutineId;
    private String sleepRoutineName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean sunday;
    private String explanation;
    private Boolean status;
    private String url;
}
