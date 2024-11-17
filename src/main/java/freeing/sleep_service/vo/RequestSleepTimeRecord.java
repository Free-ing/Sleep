package freeing.sleep_service.vo;

import freeing.sleep_service.dto.SleepStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSleepTimeRecord {
    @NotNull(message = "기상 시간(wakeUpTime)은 필수입니다.")
    private LocalTime wakeUpTime;

    @NotNull(message = "수면 시간(sleepTime)은 필수입니다.")
    private LocalTime sleepTime;

    @NotNull(message = "기록 날짜(recordDay)는 필수입니다.")
    private LocalDate recordDay;

    @Size(max = 50, message = "메모(memo)는 최대 50자까지 가능합니다.")
    private String memo;

    @NotNull(message = "수면 상태(sleepStatus)는 필수입니다.")
    private SleepStatus sleepStatus;  // Enum 값으로 변경
}
