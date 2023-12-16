package com.vertexcover.assignment.bos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeLimits {
    private TimeLimitsInfo daily;
    private TimeLimitsInfo weekly;
}