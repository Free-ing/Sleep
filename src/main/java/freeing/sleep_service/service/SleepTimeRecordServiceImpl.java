package freeing.sleep_service.service;

import freeing.sleep_service.repository.SleepTimeRecordEntity;
import freeing.sleep_service.repository.SleepTimeRecordRepository;
import freeing.sleep_service.vo.RequestSleepTimeRecord;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SleepTimeRecordServiceImpl implements SleepTimeRecordService {

    private final SleepTimeRecordRepository sleepRecordRepository;
    @Autowired
    public SleepTimeRecordServiceImpl(SleepTimeRecordRepository sleepRecordRepository) {
        this.sleepRecordRepository = sleepRecordRepository;
    }

    @Override
    public void saveSleepTimeRecord(Long userId, RequestSleepTimeRecord requestSleepTimeRecord) {
        // 해당 사용자의 기록 날짜에 이미 기록이 있는지 확인
        Optional<SleepTimeRecordEntity> existingRecord =
                sleepRecordRepository
                        .findByUserIdAndRecordDay(
                                userId,
                                requestSleepTimeRecord.getRecordDay());

        // 수면 시간 계산 (분 단위로)
        long sleepDurationInMinutes =
                calculateSleepDuration(requestSleepTimeRecord.getSleepTime(),
                        requestSleepTimeRecord.getWakeUpTime());

        if (existingRecord.isPresent()) {
            // 기존 기록이 있으면 업데이트
            SleepTimeRecordEntity sleepTimeRecordEntity = existingRecord.get();
            sleepTimeRecordEntity.setWakeUpTime(requestSleepTimeRecord.getWakeUpTime());
            sleepTimeRecordEntity.setSleepTime(requestSleepTimeRecord.getSleepTime());
            sleepTimeRecordEntity.setMemo(requestSleepTimeRecord.getMemo());
            sleepTimeRecordEntity.setSleepStatus(requestSleepTimeRecord.getSleepStatus());
            sleepTimeRecordEntity.setSleepDurationInMinutes(sleepDurationInMinutes);

            // 업데이트 된 데이터를 저장
            sleepRecordRepository.save(sleepTimeRecordEntity);
        } else {
            // 기존 기록이 없으면 새로 저장
            SleepTimeRecordEntity sleepTimeRecordEntity = new SleepTimeRecordEntity();
            sleepTimeRecordEntity.setUserId(userId);
            sleepTimeRecordEntity.setWakeUpTime(requestSleepTimeRecord.getWakeUpTime());
            sleepTimeRecordEntity.setSleepTime(requestSleepTimeRecord.getSleepTime());
            sleepTimeRecordEntity.setRecordDay(requestSleepTimeRecord.getRecordDay());
            sleepTimeRecordEntity.setMemo(requestSleepTimeRecord.getMemo());
            sleepTimeRecordEntity.setSleepStatus(requestSleepTimeRecord.getSleepStatus());
            sleepTimeRecordEntity.setSleepDurationInMinutes(sleepDurationInMinutes);

            // 새 데이터를 저장
            sleepRecordRepository.save(sleepTimeRecordEntity);
        }
    }

    @Override
    public void deleteAll(Long userId) {
        List<SleepTimeRecordEntity> list = sleepRecordRepository.findByUserId(userId);

        sleepRecordRepository.deleteAll(list);


    }

    @Override
    public boolean isExistDay(Long userId, LocalDate date) {
        return sleepRecordRepository.existsByUserIdAndRecordDay(userId,date);
    }

    // 수면 시간 계산 메서드
    private long calculateSleepDuration(LocalTime sleepTime, LocalTime wakeUpTime) {
        long result;
        long sleepTime_min = sleepTime.getMinute();
        long sleepTime_hour = sleepTime.getHour();
        long wakeUpTime_min = wakeUpTime.getMinute();
        long wakeUpTime_hour= wakeUpTime.getHour();
        if(sleepTime_hour>wakeUpTime_hour){
            if(sleepTime_min>=wakeUpTime_min){
                result = 1440 - (sleepTime_hour-wakeUpTime_hour)*60
                        - (sleepTime_min-wakeUpTime_min);

            }else{
                result = 1440 - (sleepTime_hour- wakeUpTime_hour-1)*60
                        - (sleepTime_min- wakeUpTime_min+60);
            }
        }else{
            if(sleepTime_min>=wakeUpTime_min){
                result = (wakeUpTime_hour - sleepTime_hour-1)*60
                        +(wakeUpTime_min-sleepTime_min+60);

            }else {
                result = (wakeUpTime_hour-sleepTime_hour)*60
                        + (wakeUpTime_min- sleepTime_min);
            }
        }
        return result;
    }


}
