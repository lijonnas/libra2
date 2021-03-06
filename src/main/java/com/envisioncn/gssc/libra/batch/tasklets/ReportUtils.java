package com.envisioncn.gssc.libra.batch.tasklets;

import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.StepExecution;

import java.io.File;

/**
 * Utilities for tasklets that generate reports.
 * @author jonnas
 * @date 2021-04-07
 */
public class ReportUtils {
    public static File generateReportFile(String reportDir, StepExecution stepExecution, String filename) {
        String outputDir = FilenameUtils.concat(reportDir,
                stepExecution.getJobExecution().getJobInstance().getJobName()
                        + "/" + stepExecution.getJobExecution().getJobId()
                        + "_" + stepExecution.getJobExecutionId()
                        + "/" + stepExecution.getStepName());
        String path = FilenameUtils.concat(outputDir, filename);
        File file = new File(path);
        return file;
    }

    public static String generateReportUrl(StepExecution stepExecution, String filename) {
        String reportUrl = "reports/" + stepExecution.getJobExecution().getJobInstance().getJobName()
                + "/" + stepExecution.getJobExecution().getJobId()
                + "_" + stepExecution.getJobExecutionId()
                + "/" + stepExecution.getStepName()
                + "/" + filename;
        return reportUrl;
    }
}
