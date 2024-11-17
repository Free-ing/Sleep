package freeing.sleep_service.service;

import freeing.sleep_service.repository.SleepTimeRecordEntity;
import freeing.sleep_service.repository.SleepTimeRecordStatusEntity;
import freeing.sleep_service.repository.SleepTimeRecordStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SleepTimeRecordStatusServiceImpl implements SleepTimeRecordStatusService{
    private final SleepTimeRecordStatusRepository sleepTimeRecordStatusRepository;
    @Override
    public SleepTimeRecordStatusEntity addSleepTimeRecordStatus(Long userId) {
        SleepTimeRecordStatusEntity sleepTimeRecordStatusEntity = new SleepTimeRecordStatusEntity();
        sleepTimeRecordStatusEntity.setStatus(false);
        sleepTimeRecordStatusEntity.setUserId(userId);
        sleepTimeRecordStatusRepository.save(sleepTimeRecordStatusEntity);
      log.info("사용자의 수면 기록 상태 생성 완료");

        return sleepTimeRecordStatusEntity;
    }

    @Override
    public SleepTimeRecordStatusEntity changeStatusOn(Long userId) {
        SleepTimeRecordStatusEntity sleepTimeRecordStatusEntity
                = sleepTimeRecordStatusRepository.findByUserId(userId);

        sleepTimeRecordStatusEntity.setStatus(true);

        return sleepTimeRecordStatusRepository.save(sleepTimeRecordStatusEntity);


    }

    @Override
    public SleepTimeRecordStatusEntity changeStatusOff(Long userId) {
        SleepTimeRecordStatusEntity sleepTimeRecordStatusEntity
                = sleepTimeRecordStatusRepository.findByUserId(userId);

        sleepTimeRecordStatusEntity.setStatus(false);

        return sleepTimeRecordStatusRepository.save(sleepTimeRecordStatusEntity);

    }

    @Override
    public void deleteTimeRecordStatus(Long userId) {
        sleepTimeRecordStatusRepository.deleteByUserId(userId);
    }

    @Override
    public boolean getStatus(Long userId) {
        return sleepTimeRecordStatusRepository.findByUserId(userId).getStatus();
    }
}
