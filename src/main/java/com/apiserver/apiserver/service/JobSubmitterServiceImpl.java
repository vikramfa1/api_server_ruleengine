package com.apiserver.apiserver.service;

import com.apiserver.apiserver.DTO.JobSubmitterRequestDTO;
import com.apiserver.apiserver.DTO.JobSubmitterResponseDTO;
import com.apiserver.apiserver.entity.JobsEntity;
import com.apiserver.apiserver.repository.JobsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.apiserver.apiserver.configuration.StatusEnum.IN_PROGRESS;

@Service
public class JobSubmitterServiceImpl implements JobSubmitterService{

    @Autowired
    JobsRepository jobsRepository;

    @Autowired
    RuleEngineService ruleEngineService;

    @Override
    public JobsEntity submitJob(JobSubmitterRequestDTO jobSubmitterRequestDTO) {

        JobsEntity entity = JobsEntity.builder().userId(jobSubmitterRequestDTO.getUserId())
                .jobType(jobSubmitterRequestDTO.getJobType())
                .jobName(jobSubmitterRequestDTO.getJobName())
                .language(jobSubmitterRequestDTO.getLanguage())
                .webAppType(jobSubmitterRequestDTO.getAppType())
                .executionStatus(IN_PROGRESS.name()).build();
        JobsEntity savedEntity = jobsRepository.save(entity);
        ruleEngineService.initiateJob(jobSubmitterRequestDTO, savedEntity.getId());

        return savedEntity;
    }



    @Override
    public JobSubmitterResponseDTO fetchJob(Long jobId) {
        JobsEntity entity = jobsRepository.findById(jobId).get();
        return JobSubmitterResponseDTO.builder().jobType(entity.getJobType())
                .userId(entity.getUserId())
                .executionStatus(entity.getExecutionStatus())
                .language(entity.getLanguage())
                .jobId(entity.getId())
                .jobName(entity.getJobName())
                .webAppType(entity.getWebAppType())
                .currentTask(entity.getCurrentTask())
                .previousTask(entity.getPreviousTask()).build();
    }
}
