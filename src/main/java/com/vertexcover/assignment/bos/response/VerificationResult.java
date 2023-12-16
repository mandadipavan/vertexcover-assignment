package com.vertexcover.assignment.bos.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VerificationResult {
    private boolean valid;
    private String message;
    private int remainingUses;
    private TimeLimits timeLimits;
}
