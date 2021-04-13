package com.envisioncn.gssc.libra.schedule;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;

/**
 * @author zhongshuangli
 * @date 2021-04-06
 */
@Getter
@Setter
public class QuartzJobLauncher extends AbstractScheduledJob {
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            Map< String, Object > jobDataMap = jobExecutionContext.getMergedJobDataMap();
            String jobName = (String)jobDataMap.get("jobName");
            Job job = jobLocator.getJob(jobName);
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, params);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
