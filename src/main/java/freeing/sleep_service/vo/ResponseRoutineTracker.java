package freeing.sleep_service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ResponseRoutineTracker {
    private List<SleepRoutineRecordResponse> routineRecords;
    private List<LocalDate> timeRecords; // List<SleepTimeRecordResponse> -> List<LocalDate>로 수정
}
