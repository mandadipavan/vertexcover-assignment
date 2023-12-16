package com.vertexcover.assignment.bos.response;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class ApplicationResult {
    private String status;
    private String message;
    private UpdatedRepeatCounts updatedRepeatCounts;
}
