package freeing.sleep_service.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalTime;

@Data
public class RequestSleepRoutineAdd {


    @NotBlank(message = "수면 루틴 이름은 필수 입력 항목 입니다")
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
