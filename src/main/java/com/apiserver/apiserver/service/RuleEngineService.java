package com.apiserver.apiserver.service;

import com.apiserver.apiserver.DTO.JobSubmitterRequestDTO;

public interface RuleEngineService {

    public void createRuleEngine(String workflowId);
    public String getNextTask(String workflowId, String taskId);
    public void initiateJob(JobSubmitterRequestDTO jobSubmitterRequestDTO, Long uId);
    public void execute(String jobType, Long id, String completedTask);
}
