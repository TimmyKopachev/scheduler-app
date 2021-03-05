package scheduler.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DateExecutorService {

    private final Scheduler scheduler;

    public void scheduleTask() throws SchedulerException {
        
    }
}
