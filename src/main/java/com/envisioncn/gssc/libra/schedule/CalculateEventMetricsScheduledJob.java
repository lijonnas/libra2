package com.envisioncn.gssc.libra.schedule;

import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;

/**
 * @author jonnas
 * @date 2021-04-09
 */
@DisallowConcurrentExecution /* because we store job state between executions */
@PersistJobDataAfterExecution /* because we store last fire time between executions */
public class CalculateEventMetricsScheduledJob extends AbstractScheduledJob {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobRegistry jobRegistry = getJobRegistry();
        JobLauncher jobLauncher = getJobLauncher();
        long finishedAt = context.getMergedJobDataMap().getLong("finishedAt");
        Date startingFrom = new Date(finishedAt);
        Date endingAt = new Date();
        Map<String, JobParameter> parameters = Maps.newHashMap();
        parameters.put("startingFrom", new JobParameter(startingFrom));
        parameters.put("endingAt", new JobParameter(endingAt));
        try {
            jobLauncher.run(jobRegistry.getJob("calculateEventMetricsJob"), new JobParameters(parameters));
            context.getMergedJobDataMap().putAsString("finishedAt", endingAt.getTime());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
