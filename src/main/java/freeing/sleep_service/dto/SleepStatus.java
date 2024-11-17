package freeing.sleep_service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SleepStatus {
    REFRESHED("개운해요"),
    STIFF("뻐근해요"),
    UNRESTED("잔 것 같지 않아요");

    private final String description;

    SleepStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonValue
    public String getName() {
        return name();
    }

    @JsonCreator
    public static SleepStatus from(String value) {
        for (SleepStatus status : SleepStatus.values()) {
            if (status.name().equalsIgnoreCase(value) || status.getDescription().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("잘못된 수면 상태 값입니다: " + value + ". 올바른 값은 [REFRESHED, STIFF, UNRESTED] 입니다.");
    }
}