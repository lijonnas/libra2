package com.envisioncn.gssc.libra.batch.tasklets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author jonnas
 * @date 2021-04-07
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class ReportGenerationResult {
    public enum ReportGenerationResultStatus {
        OK,
        /* Indicates that the step should be considered failed. */
        Failed
    }

    @NonNull
    private ReportGenerationResultStatus status;
    private String message;

    public static ReportGenerationResult OK = new ReportGenerationResult(ReportGenerationResultStatus.OK);
}

