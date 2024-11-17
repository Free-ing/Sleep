package freeing.sleep_service.service;

import freeing.sleep_service.vo.RequestSleepTimeRecord;

import java.time.LocalDate;

public interface SleepTimeRecordService {
    public void saveSleepTimeRecord(Long userId, RequestSleepTimeRecord requestSleepTimeRecord);
    public void deleteAll(Long userId);
    boolean isExistDay(Long userId, LocalDate date);

}
