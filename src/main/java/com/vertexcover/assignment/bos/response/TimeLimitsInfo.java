package com.vertexcover.assignment.bos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeLimitsInfo {
    private int remaining;
    private int limit;
}
