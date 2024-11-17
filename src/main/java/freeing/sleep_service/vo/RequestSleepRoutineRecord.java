package freeing.sleep_service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestSleepRoutineRecord {

    @NotNull(message = "완료 날짜는 필수 입력 사항입니다.")
    private LocalDate completeDay;

    @NotNull(message = "수면 루틴 식별 번호는 필수 입력 항목 입니다")
    private Long sleepRoutineId;
}
