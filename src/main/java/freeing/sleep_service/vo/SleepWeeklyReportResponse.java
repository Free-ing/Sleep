package freeing.sleep_service.vo;


import freeing.sleep_service.repository.SleepTimeRecordEntity;
import freeing.sleep_service.repository.SleepWeeklyReportEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SleepWeeklyReportResponse {
    private SleepWeeklyReportEntity weeklyReport;
    private List<SleepTimeRecordEntity> sleepRecords;
}
