package freeing.sleep_service.repository;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sleep_routine")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleepRoutineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가를 위한 설정
    @Column(name = "sleep_routine_id")
    private Long sleepRoutineId;

    @Column(name = "sleep_routine_name", nullable = false)
    private String sleepRoutineName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalTime endTime;

    @Column(name = "monday", nullable = true)
    private Boolean monday;

    @Column(name = "tuesday", nullable = true)
    private Boolean tuesday;

    @Column(name = "wednesday", nullable = true)
    private Boolean wednesday;

    @Column(name = "thursday", nullable = true)
    private Boolean thursday;

    @Column(name = "friday", nullable = true)
    private Boolean friday;

    @Column(name = "saturday", nullable = true)
    private Boolean saturday;

    @Column(name = "sunday", nullable = true)
    private Boolean sunday;

    @Column(name = "explanation", columnDefinition = "TEXT", nullable = true)
    private String explanation;

    @Column(name = "status", nullable = true)
    private Boolean status;

    @Column(name="url", nullable = true)
    private String url;

    @Column(name="createDate", nullable = true)
    private LocalDateTime createDate;

//    @Column(name = "onDate", nullable = true)
//    private LocalDateTime onDate;
//
//    @Column(name = "offDate", nullable = true)
//    private LocalDateTime offDate;

    // SleepRoutineEntity.java
    @OneToMany(mappedBy = "sleepRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SleepRoutineExceptionEntity> exceptions;

    // 비활성화 기간 추가
    @OneToMany(mappedBy = "sleepRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DisabledPeriodEntity> disabledPeriods = new ArrayList<>();





}
