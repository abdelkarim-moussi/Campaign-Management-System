package com.app.cms.automation;

import com.app.cms.automation.entity.ExecutionStatus;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.repository.WorkflowExecutionRepository;
import com.app.cms.automation.service.WorkflowExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowScheduler {

    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowExecutionService executionService;


    @Scheduled(cron = "0 0 * * * *")
    public void resumeWaitingExecutions() {
        log.debug("Checking for workflows to resume...");

        List<WorkflowExecution> ready = executionRepository.findReadyToResume(LocalDateTime.now());

        if (!ready.isEmpty()) {
            log.info("Found {} workflows ready to resume", ready.size());

            for (WorkflowExecution execution : ready) {
                try {
                    log.info("Resuming workflow execution {}", execution.getId());
                    execution.setStatus(ExecutionStatus.RUNNING);
                    executionRepository.save(execution);

                    executionService.executeNextAction(execution);

                } catch (Exception e) {
                    log.error("Error resuming execution {}: {}",
                            execution.getId(), e.getMessage(), e);
                }
            }
        }
    }
}
