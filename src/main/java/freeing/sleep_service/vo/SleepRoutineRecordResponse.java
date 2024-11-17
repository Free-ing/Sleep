package freeing.sleep_service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class SleepRoutineRecordResponse {
    private Long routineId;
    private String routineName;
    private List<LocalDate> completeDay; // LocalDate를 List<LocalDate>로 변경
    private String url;
}
