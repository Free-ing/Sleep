package freeing.sleep_service.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SleepTimeRecordStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가를 위한 설정
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, name = "status")
    private Boolean status;


}
