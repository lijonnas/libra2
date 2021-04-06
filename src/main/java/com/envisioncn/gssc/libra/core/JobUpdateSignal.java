package com.envisioncn.gssc.libra.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jonnas
 * @date 2021-04-06
 */
@Data
@AllArgsConstructor
public class JobUpdateSignal {
    enum UpdateType {StepUpdate, JobUpdate}

    private UpdateType updateType;
    private Long jobExecutionId;
    private String stepName;
}