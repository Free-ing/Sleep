package freeing.sleep_service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
@AllArgsConstructor
public class SleepTimeRecordResponse {
    private Long sleepTimeRecordId;
    private LocalDate recordDay;

}
