package com.apiserver.apiserver.service;

import com.apiserver.apiserver.DTO.JobSubmitterRequestDTO;
import com.apiserver.apiserver.DTO.JobSubmitterResponseDTO;
import com.apiserver.apiserver.entity.JobsEntity;

public interface JobSubmitterService {

    JobsEntity submitJob(JobSubmitterRequestDTO jobSubmitterRequestDTO);
    JobSubmitterResponseDTO fetchJob(Long jobId);

}
