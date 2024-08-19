package com.apiserver.apiserver.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSubmitterResponseDTO {
    String userId;
    String jobType;
    String jobName;
    String language;
    String webAppType;
    Long jobId;
    String executionStatus;
    String currentTask;
    String previousTask;
}
