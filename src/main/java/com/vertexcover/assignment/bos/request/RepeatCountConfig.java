package com.vertexcover.assignment.bos.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class RepeatCountConfig {
    @Min(value = 0, message = "Global total repeat count must be greater than or equal to 0")
    private int globalTotalRepeatCount;
    @Min(value = 0, message = "User total repeat count must be greater than or equal to 0")
    private int userTotalRepeatCount;
    @Min(value = 0, message = "User daily repeat count must be greater than or equal to 0")
    private int userDailyRepeatCount;
    @Min(value = 0, message = "User weekly repeat count must be greater than or equal to 0")
    private int userWeeklyRepeatCount;
}