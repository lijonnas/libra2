package com.envisioncn.gssc.libra.batch.tasklets;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * Closes all idle connections in the StagingDB connection pool. Used in situations where you must ensure that all
 * DB sessions are closed. For instance, if you must drop the Staging DB schema.
 * @author jonnas
 * @date 2021-04-07
 */
@Slf4j
@Getter
@Setter
public class CloseDbConnectionsTasklet extends ReportTasklet implements ReportGenerator, InitializingBean {
    private String reportName = "closeDbConnectionsReport";
    private String reportFilename = "closeDbConnections.log";
    private String encoding = "UTF-8";

    @Autowired
    @Qualifier("stagingDataSource")
    private DataSource dataSource;

    public CloseDbConnectionsTasklet() {
        addReportGenerator(this);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        if(reportFilename == null) {
            throw new Exception("reportFilename must not be null");
        }
    }

    @Override
    @Transactional(readOnly = true, transactionManager = "stagingTransactionManager")
    public ReportGenerationResult generateReport(File reportFile, StepContribution sc, ChunkContext cc) throws Exception {
        log.info("Exporting to file. {}", reportFile);
        DruidDataSource basicDataSource = (DruidDataSource)dataSource;
        int active = basicDataSource.getActiveCount();
        if (active > 0) {
            throw new Exception("There are active connections. Can only close idle connections");
        }
        int initiallyIdle = basicDataSource.getWaitThreadCount();

        if (initiallyIdle > 0) {
            int maxAttempts = initiallyIdle;
            int attempts = 0;
            int idle = 0;
            while((idle = basicDataSource.getWaitThreadCount()) > 0) {
                log.debug("Idle:   {}, Attempts: {}", idle, attempts);
                if (attempts++ > maxAttempts) {
                    throw new Exception("Spent " + attempts + " attempts to close all connections, but " + idle + " are still idle");
                }
                basicDataSource.validateConnection(basicDataSource.getConnection());
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(reportFile), Charset.forName(encoding).newEncoder())) {
            if (initiallyIdle == 0) {
                writer.append("No connections to close. Doing nothing").append(System.lineSeparator());
            }
            else {
                writer.append("Closed ")
                        .append(String.valueOf(initiallyIdle))
                        .append(" idle connections")
                        .append(System.lineSeparator());
            }
        }
        return ReportGenerationResult.OK;
    }
}

