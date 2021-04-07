package com.envisioncn.gssc.libra.batch.tasklets;

import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.Objects;

/**
 * Asserts that a given directory (file/directory) exists.
 * @author jonnas
 * @date 2021-04-07
 */
public class ResourceAssertTasklet implements Tasklet {
    Resource resource = null;
    String assertionMessage = null;
    Integer expectedFileCount = null;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (!resource.exists()) {
            String msg = assertionMessage;
            if (msg == null) {
                msg = "Resource " + resource.getDescription() + " does not exist";
            }
            throw new JobExecutionException(msg);
        } else if (expectedFileCount != null) {
            File file = resource.getFile();
            if (!file.isDirectory()) {
                throw new JobExecutionException("Resource is not a directory");
            }
            if (Objects.requireNonNull(file.list()).length != expectedFileCount) {
                throw new JobExecutionException("Directory "
                        + resource.getFilename()
                        + " does not contain the expected nr of files "
                        + expectedFileCount + ". Actual nr of files is "
                        + Objects.requireNonNull(file.list()).length);
            }
        }

        return RepeatStatus.FINISHED;
    }

    /**
     *
     * @param resource The directory to be checked.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     *
     * @param assertionMessage An optional message to be presented if assertion fails.
     */
    public void setAssertionMessage(String assertionMessage) {
        this.assertionMessage = assertionMessage;
    }

    /**
     *
     * @param expectedFileCount The expected amount of files in the provided directory. Optional.
     */
    public void setExpectedFileCount(Integer expectedFileCount) {
        this.expectedFileCount = expectedFileCount;
    }
}

