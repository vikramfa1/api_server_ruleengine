package com.apiserver.apiserver.service;

import com.apiserver.apiserver.DTO.JobSubmitterRequestDTO;
import com.apiserver.apiserver.DTO.OperationsDTO;
import com.apiserver.apiserver.entity.JobsEntity;
import com.apiserver.apiserver.repository.JobsRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class RuleEngineServiceImpl implements RuleEngineService {
    Map<String, LinkedHashSet<String>> ruleEngineMap;

    @Autowired
    JobsRepository jobsRepository;

    @Autowired
    RestTemplate restTemplate;

    @Value("${worker.node.task.url}")
    private String workerNodeTaskUrl;

    @PostConstruct
    public void init() {
        ruleEngineMap = new HashMap<>();
        //workflow 1
        LinkedHashSet<String> springBootWorkflow1 = new LinkedHashSet<>();
        springBootWorkflow1.add("GITHUB_CLONE");
        springBootWorkflow1.add("JENKINS_PIPELINE");
        springBootWorkflow1.add("K8S_DEPLOYMENT");
        ruleEngineMap.put("SPRING", springBootWorkflow1);

        //workflow2
        LinkedHashSet<String> javaLibWorkflow2 = new LinkedHashSet<>();
        javaLibWorkflow2.add("GITHUB_CLONE");
        javaLibWorkflow2.add("JENKINS_PIPELINE");
        ruleEngineMap.put("JAVA_LIB", javaLibWorkflow2);
    }
    @Override
    public void createRuleEngine(String workflowId) {
        //API can be exposed to create new sequence dynamically;
    }

    @Override
    public void initiateJob(JobSubmitterRequestDTO jobSubmitterRequestDTO, Long uId) {
        String task = getNextTask(jobSubmitterRequestDTO.getAppType(), "START");
        JobsEntity jobsEntity = jobsRepository.findById(uId).get();
        jobsEntity.setCurrentTask(task);
        jobsRepository.save(jobsEntity);
        assignTask(jobsEntity.getCurrentTask(), jobsEntity.getId(), jobsEntity.getWebAppType(), jobsEntity.getJobName());
    }

    @Override
    public void execute(String jobType, Long id, String completedTask) {
        String task = getNextTask(jobType, completedTask);
        JobsEntity jobsEntity = jobsRepository.findById(id).get();
        jobsEntity.setCurrentTask(task);
        jobsEntity.setPreviousTask(completedTask);
        if(task.equals("COMPLETED")||task.equals("FAILED"))
            jobsEntity.setExecutionStatus(task);
        jobsRepository.save(jobsEntity);
        if(!(task.equals("COMPLETED")||task.equals("FAILED")))
            assignTask(jobsEntity.getCurrentTask(), jobsEntity.getId(), jobsEntity.getWebAppType(), jobsEntity.getJobName());
    }

    public void assignTask(String task, Long id, String webAppType, String jobName) {
        String endpointUrl = workerNodeTaskUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // Create request body as JSON
        OperationsDTO operationsDTO = OperationsDTO.builder().taskId(task).jobId(id).operationType(webAppType).jobName(jobName).build();

        HttpEntity<OperationsDTO> requestEntity = new HttpEntity<>(operationsDTO, headers);

        ResponseEntity<OperationsDTO> response = restTemplate.exchange(
                endpointUrl,
                HttpMethod.POST,
                requestEntity,
                OperationsDTO.class
        );

        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            System.out.println("POST request successful: " + response.getBody());
        } else {
            System.err.println("POST request failed: " + response.getStatusCode());
        }
    }

    @Override
    public String getNextTask(String workflowId, String taskId) {
        if(taskId.equals("START"))
            return ruleEngineMap.get(workflowId).stream().findFirst().get();
        String nextTask = findNextString(ruleEngineMap.get(workflowId), taskId);
        return nextTask!=null?nextTask:"COMPLETED";
    }

    public static String findNextString(LinkedHashSet<String> set, String target) {
        Iterator<String> iterator = set.iterator();
        String nextString = null;

        while (iterator.hasNext()) {
            String current = iterator.next();
            if (current.equals(target)) {
                if (iterator.hasNext()) {
                    nextString = iterator.next();
                }
                break;
            }
        }
        return nextString;
    }
}
