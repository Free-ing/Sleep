package freeing.sleep_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SleepRoutineRepository extends JpaRepository<SleepRoutineEntity, Long> {

    // 특정 사용자와 요일에 맞는 루틴을 조회하는 쿼리
    // 특정 사용자와 요일에 맞고, 상태가 true인 루틴만 조회하는 쿼리
    @Query("SELECT s FROM SleepRoutineEntity s WHERE s.userId = :userId AND" +
            "(:dayOfWeek = 'MONDAY' AND s.monday = true OR " +
            " :dayOfWeek = 'TUESDAY' AND s.tuesday = true OR " +
            " :dayOfWeek = 'WEDNESDAY' AND s.wednesday = true OR " +
            " :dayOfWeek = 'THURSDAY' AND s.thursday = true OR " +
            " :dayOfWeek = 'FRIDAY' AND s.friday = true OR " +
            " :dayOfWeek = 'SATURDAY' AND s.saturday = true OR " +
            " :dayOfWeek = 'SUNDAY' AND s.sunday = true)")
    List<SleepRoutineEntity> findRoutinesByDayOfWeek(@Param("userId") Long userId,
                                                     @Param("dayOfWeek") String dayOfWeek);

    List<SleepRoutineEntity> findAllByUserId(Long userId);

    Optional<SleepRoutineEntity> findByUserIdAndSleepRoutineId(Long userId, Long routineId);

    void deleteBySleepRoutineIdAndUserId(Long routineId, Long userId);

    void deleteByUserId(Long userId);




}
