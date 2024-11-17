package freeing.sleep_service.service;

import freeing.sleep_service.repository.SleepTimeRecordStatusEntity;

public interface SleepTimeRecordStatusService {
    SleepTimeRecordStatusEntity addSleepTimeRecordStatus(Long userId);
    SleepTimeRecordStatusEntity changeStatusOn(Long userId);
    SleepTimeRecordStatusEntity changeStatusOff(Long userId);
    void deleteTimeRecordStatus(Long userId);
    boolean getStatus(Long userId);
}
