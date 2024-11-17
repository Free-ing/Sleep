package freeing.sleep_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepTimeRecordStatusRepository extends JpaRepository<SleepTimeRecordStatusEntity, Long> {
    SleepTimeRecordStatusEntity findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
