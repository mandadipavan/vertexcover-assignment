package com.vertexcover.assignment.bos.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdatedRepeatCounts {
    private int globalTotalRemaining;
    private int userTotalRemaining;
    private int userDailyRemaining;
    private int userWeeklyRemaining;
}