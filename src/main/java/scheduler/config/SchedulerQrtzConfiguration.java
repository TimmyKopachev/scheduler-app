package scheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import scheduler.service.LogSchedulerService;

@Slf4j
@Configuration
@EnableScheduling
@PropertySource("classpath:scheduler.properties")
@ConditionalOnExpression(value = "${scheduler.enable:true}")
public class SchedulerQrtzConfiguration {

    @Value("${quartz.cron.expression}")
    private String cronExpression;

    @Autowired
    private ApplicationContext applicationContext;

    private static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean(name = "logJob")
    public JobDetailFactoryBean logJob() {
        log.info("Init Log job");
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setName("LogSchedulerJob");
        factoryBean.setDescription("Invoke Log Scheduler Job...");
        factoryBean.setJobClass(LogSchedulerService.class);
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean
    public CronTriggerFactoryBean logTrigger(JobDetail logJob) {
        log.info("Init Log trigger");
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName("LogCronTrigger");
        factoryBean.setJobDetail(logJob);
        factoryBean.setCronExpression(cronExpression);
        return factoryBean;
    }

    @Bean
    public SchedulerFactoryBean logScheduler(JobDetail logJob, Trigger logTrigger, SpringBeanJobFactory springBeanJobFactory) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setSchedulerName("Log Scheduler");
        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setAutoStartup(true);
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactory.setJobDetails(logJob);
        schedulerFactory.setTriggers(logTrigger);
        schedulerFactory.setJobFactory(springBeanJobFactory);
        return schedulerFactory;
    }
}