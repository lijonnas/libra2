package com.envisioncn.gssc.libra.schedule;

import lombok.Data;

import java.util.Date;

/**
 * @author jonnas
 * @date 2021-04-06
 */
@Data
public class JobTriggerLog {
    Date tstamp;
    String status;
    JobTrigger.TriggerType triggerType;
    String triggeringJobName;
    String jobName;
    String cron;
    String errorMsg;
}

