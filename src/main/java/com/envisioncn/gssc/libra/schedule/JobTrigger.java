package com.envisioncn.gssc.libra.schedule;

import lombok.Data;

/**
 * @author jonnas
 * @date 2021-04-06
 */
@Data
public class JobTrigger {
    public enum TriggerType {CRON, JOB_COMPLETION}
    Long id;

    TriggerType triggerType;
    String triggeringJobName;
    String jobName;
    String cron;
    String jobParameters;
}