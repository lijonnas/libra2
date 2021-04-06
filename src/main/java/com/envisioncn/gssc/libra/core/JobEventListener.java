package com.envisioncn.gssc.libra.core;

/**
 * @author jonnas
 * @date 2021-04-06
 */
public interface JobEventListener {
    void onJobChange(JobInstanceInfo jobInstanceInfo);
    void onStepChange(BasicJobInstanceInfo basicJobInstanceInfo, StepInfo stepInfo);
}
