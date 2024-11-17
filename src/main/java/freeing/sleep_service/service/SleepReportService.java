package freeing.sleep_service.service;

import freeing.sleep_service.error.NotFoundException;
import freeing.sleep_service.repository.SleepTimeRecordEntity;
import freeing.sleep_service.repository.SleepTimeRecordRepository;
import freeing.sleep_service.repository.SleepWeeklyReportEntity;
import freeing.sleep_service.repository.SleepWeeklyReportRepository;
import freeing.sleep_service.vo.SleepWeeklyReportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

@Service
@Transactional
@Slf4j
public class SleepReportService {

    private final SleepTimeRecordRepository sleepTimeRecordRepository;
    private final SleepWeeklyReportRepository sleepWeeklyReportRepository;
    private final AIService aiService;

    @Autowired
    public SleepReportService(SleepTimeRecordRepository sleepTimeRecordRepository,
                              SleepWeeklyReportRepository sleepWeeklyReportRepository,
                              AIService aiService) {
        this.sleepTimeRecordRepository = sleepTimeRecordRepository;
        this.sleepWeeklyReportRepository = sleepWeeklyReportRepository;
        this.aiService = aiService;
    }

    // 주간 리포트를 생성하는 메서드
    @Transactional
    public void generateWeeklySleepReport() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // 지난 일주일 간 기록이 있는 사용자 리스트 조회
        List<Long> userIds = sleepTimeRecordRepository.findDistinctUserIdsWithSleepRecords(startOfWeek, endOfWeek);

        log.info("Found {} users with sleep records between {} and {}", userIds.size(), startOfWeek, endOfWeek);

        for (Long userId : userIds) {
            List<SleepTimeRecordEntity> sleepRecords = sleepTimeRecordRepository.findByUserIdAndRecordDayBetween(userId, startOfWeek, endOfWeek);

            // 평균 계산 및 저장
            if (!sleepRecords.isEmpty()) {
                calculateAndSaveSleepReport(userId, sleepRecords, startOfWeek, endOfWeek);
            } else {
                // 수면 기록이 없는 경우
                log.warn("사용자 {}의 지난 주 수면 기록이 없습니다.", userId);
            }
        }
    }

    private void calculateAndSaveSleepReport(Long userId,
                                             List<SleepTimeRecordEntity> sleepRecords,
                                             LocalDate startOfWeek,
                                             LocalDate endOfWeek) {
        // 수면 시간 평균 계산
        LocalTime avgSleepTime = calculateAverageTime(sleepRecords, SleepTimeRecordEntity::getSleepTime);
        LocalTime avgWakeUpTime = calculateAverageTime(sleepRecords, SleepTimeRecordEntity::getWakeUpTime);

        // 총 수면 시간을 분 단위로 합산하여 평균 계산
        long totalSleepDuration = sleepRecords.stream()
                .mapToLong(SleepTimeRecordEntity::getSleepDurationInMinutes)
                .sum();
        long avgSleepDuration = totalSleepDuration / sleepRecords.size();

        // 주간 리포트 저장
        saveWeeklyReport(userId, avgSleepTime, avgWakeUpTime, avgSleepDuration, startOfWeek, endOfWeek);
    }

    // 각도 기반 평균 시간 계산 메서드
    private LocalTime calculateAverageTime(List<SleepTimeRecordEntity> sleepRecords,
                                           Function<SleepTimeRecordEntity, LocalTime> timeExtractor) {
        double totalX = 0.0;
        double totalY = 0.0;

        // 각 수면 기록에 대해 각도로 변환하여 Cartesian 좌표계로 변환
        for (SleepTimeRecordEntity record : sleepRecords) {
            LocalTime time = timeExtractor.apply(record);
            int minutes = time.getHour() * 60 + time.getMinute();
            double angle = Math.toRadians((minutes / (24.0 * 60)) * 360); // 각도로 변환

            // x, y 좌표 누적
            totalX += Math.cos(angle);
            totalY += Math.sin(angle);
        }

        // 평균 각도 계산
        double avgAngle = Math.atan2(totalY, totalX);
        double avgMinutes = (Math.toDegrees(avgAngle) / 360.0) * (24 * 60);
        if (avgMinutes < 0) { // 각도가 음수인 경우 양수로 조정
            avgMinutes += 24 * 60;
        }

        // 분을 시간 형식으로 변환하여 반환
        int avgHour = (int) (avgMinutes / 60);
        int avgMinute = (int) (avgMinutes % 60);
        return LocalTime.of(avgHour, avgMinute);
    }

    private void saveWeeklyReport(Long userId, LocalTime avgSleepTime, LocalTime avgWakeUpTime,
                                  long avgSleepDuration, LocalDate startOfWeek, LocalDate endOfWeek) {

        SleepWeeklyReportEntity sleepWeeklyReportEntity = new SleepWeeklyReportEntity();
        sleepWeeklyReportEntity.setReportEndDate(endOfWeek);
        sleepWeeklyReportEntity.setReportStartDate(startOfWeek);
        sleepWeeklyReportEntity.setAvgSleepTime(avgSleepTime);
        sleepWeeklyReportEntity.setUserId(userId);
        sleepWeeklyReportEntity.setAvgSleepDurationInMinutes(avgSleepDuration);
        sleepWeeklyReportEntity.setAvgWakeUpTime(avgWakeUpTime);

        try {
            sleepWeeklyReportRepository.save(sleepWeeklyReportEntity);
        } catch (Exception e) {
            log.error("Error saving weekly sleep report for user {}: {}", userId, e.getMessage());
        }
    }

    // 주간 리포트 및 일일 기록 조회
    public SleepWeeklyReportResponse getWeeklyReport(Long userId, LocalDate startDate, LocalDate endDate) {

        // 1. 주간 리포트 조회
        SleepWeeklyReportEntity weeklyReport = sleepWeeklyReportRepository
                .findByUserIdAndReportStartDateAndReportEndDate(userId, startDate, endDate)
                .orElseThrow(() -> new NotFoundException("주간 리포트를 찾을 수 없습니다."));

        // 2. 일주일 간 수면 기록 조회
        List<SleepTimeRecordEntity> sleepRecords = sleepTimeRecordRepository
                .findByUserIdAndRecordDayBetween(userId, startDate, endDate);

        // 3. 주간 리포트와 일일 수면 기록을 함께 반환
        return new SleepWeeklyReportResponse(weeklyReport, sleepRecords);
    }

    // AI 피드백이 없는 주간 리포트에 대해 AI 피드백을 생성하고 저장
    public void updateReportsWithMissingAiFeedback() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);

        List<SleepWeeklyReportEntity> reportsWithoutFeedback =
                sleepWeeklyReportRepository.findReportsWithoutAiFeedback(startDate, endDate);

        log.info("AI 피드백이 없는 리포트 수: {}", reportsWithoutFeedback.size());

        for (SleepWeeklyReportEntity report : reportsWithoutFeedback) {
            try {
                // 사용자의 일주일간 수면 기록 조회
                List<SleepTimeRecordEntity> sleepRecords = sleepTimeRecordRepository
                        .findByUserIdAndRecordDayBetween(report.getUserId(), report.getReportStartDate(), report.getReportEndDate());

                if (sleepRecords.isEmpty()) {
                    log.warn("사용자 {}의 수면 기록이 없습니다.", report.getUserId());
                    continue;
                }

                String aiFeedback = aiService.generateFeedback(
                        report.getAvgSleepTime(),
                        report.getAvgWakeUpTime(),
                        report.getAvgSleepDurationInMinutes(),
                        sleepRecords);

                report.setAiFeedback(aiFeedback);
                log.info(aiFeedback);
                sleepWeeklyReportRepository.save(report);
                log.info("사용자 {}의 AI 피드백이 업데이트되었습니다.", report.getUserId());

            } catch (Exception e) {
                log.error("사용자 {}의 AI 피드백 생성 중 오류 발생: {}", report.getUserId(), e.getMessage(), e);
            }
        }
    }

    public void deleteAll(Long userId){
        List<SleepWeeklyReportEntity> list = sleepWeeklyReportRepository.findByUserId(userId);
        sleepWeeklyReportRepository.deleteAll(list);
    }


}