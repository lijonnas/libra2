package com.envisioncn.gssc.libra.schedule;

import java.util.Map;

import com.google.common.collect.Maps;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;

/**
 * @author jonnas
 * @date 2021-04-09
 * @DisallowConcurrentExecution - no need for this because jobs don't intersect
 * @PersistJobDataAfterExecution - don't store any data between executions
 */
public class CalculateOnlineMetricsScheduledJob extends AbstractScheduledJob {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobRegistry jobRegistry = getJobRegistry();
        JobLauncher jobLauncher = getJobLauncher();
        Map<String, JobParameter> parameters = Maps.newHashMap();
        parameters.put("scheduledFireTime", new JobParameter(context.getScheduledFireTime()));
        try {
            jobLauncher.run(jobRegistry.getJob("calculateOnlineMetricsJob"), new JobParameters(parameters));
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
