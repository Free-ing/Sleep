package freeing.sleep_service.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SleepRoutineAddDto {

    private String sleepRoutineName;
    private Long userId;
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
