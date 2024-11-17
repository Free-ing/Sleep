package freeing.sleep_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class SleepReportSchedulerService {

    private final SleepReportService sleepReportService;

    @Autowired
    public SleepReportSchedulerService(SleepReportService sleepReportService) {
        this.sleepReportService = sleepReportService;
    }

    @Scheduled(cron = "0 30 0 * * MON", zone = "Asia/Seoul")
    public void runWeeklySleepReportGeneration() {
        log.info("스케줄러가 실행되었습니다: Weekly Sleep Report 생성 시작");
        sleepReportService.generateWeeklySleepReport();
    }

    @Scheduled(cron = "0 0 01 * * MON", zone = "Asia/Seoul")
    public void updateAiFeedbackForReports() {
        log.info("스케줄러가 실행되었습니다: AI Feedback 생성 시작");
        sleepReportService.updateReportsWithMissingAiFeedback();
    }
}