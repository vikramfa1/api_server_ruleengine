package com.apiserver.apiserver.controller;

import com.apiserver.apiserver.DTO.JobSubmitterRequestDTO;
import com.apiserver.apiserver.DTO.JobSubmitterResponseDTO;
import com.apiserver.apiserver.DTO.TaskUpdatesDTO;
import com.apiserver.apiserver.entity.JobsEntity;
import com.apiserver.apiserver.service.JobSubmitterService;
import com.apiserver.apiserver.service.RuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController()
public class JobSubmitController {

    @Autowired
    private RuleEngineService ruleEngineService;

    @Autowired
    private JobSubmitterService jobSubmitterService;

    @PostMapping(value = "/v1/jobs/submit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public JobSubmitterResponseDTO submitJob(@RequestBody JobSubmitterRequestDTO jobSubmitterRequestDTO) {
        JobsEntity entity = jobSubmitterService.submitJob(jobSubmitterRequestDTO);
        return JobSubmitterResponseDTO.builder().jobType(entity.getJobType())
                .userId(entity.getUserId())
                .executionStatus(entity.getExecutionStatus())
                .currentTask(entity.getCurrentTask())
                .jobId(entity.getId())
                .jobName(entity.getJobName())
                .language(entity.getLanguage())
                .webAppType(entity.getWebAppType())
                .previousTask(entity.getPreviousTask()).build();
    }

    @GetMapping(value = "/v1/job/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public JobSubmitterResponseDTO findJobStatus(@PathVariable("id") Long jobId) {
        return jobSubmitterService.fetchJob(jobId);
    }

    @PutMapping(value = "/v1/job/{id}/update", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String updateEvents(@RequestBody TaskUpdatesDTO completedTask) {
        ruleEngineService.execute(completedTask.getOperationType(), completedTask.getJobId(), completedTask.getTaskId());
        return "updated";
    }
}
