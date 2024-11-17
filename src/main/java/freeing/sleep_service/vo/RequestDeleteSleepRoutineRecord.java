package freeing.sleep_service.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestDeleteSleepRoutineRecord {

    @NotNull(message = "삭제할 완료 날짜는 필수 입력 사항입니다.")
    private LocalDate completeDay;

    @NotNull(message = "수면 루틴 식별 번호는 필수 입력 사항입니다.")
    private Long sleepRoutineId;
}
