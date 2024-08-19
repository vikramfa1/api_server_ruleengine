package com.apiserver.apiserver.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSubmitterRequestDTO {
    String userId;
    String jobType;
    String jobName;
    String language;
    String appType;
}
