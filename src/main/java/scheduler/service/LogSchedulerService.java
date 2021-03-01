package scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class LogSchedulerService implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(context.getJobDetail().getDescription() + ": {}", new Date());
    }
}
