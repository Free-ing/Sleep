package freeing.sleep_service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DateExistenceResponse {
    private LocalDate date;
}
