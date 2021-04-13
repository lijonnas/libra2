package com.envisioncn.gssc.libra.schedule;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author jonnas
 * @date 2021-04-09
 */
public abstract class AbstractScheduledJob extends QuartzJobBean {
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected JobRegistry getJobRegistry() {
        return applicationContext.getBean(JobRegistry.class);
    }

    protected JobLauncher getJobLauncher() {
        return applicationContext.getBean(JobLauncher.class);
    }

    protected JobExplorer getJobExplorer() {
        return applicationContext.getBean(JobExplorer.class);
    }

    protected JobOperator getJobOperator() {
        return applicationContext.getBean(JobOperator.class);
    }

    protected JobRepository getJobRepository() {
        return applicationContext.getBean(JobRepository.class);
    }
}
