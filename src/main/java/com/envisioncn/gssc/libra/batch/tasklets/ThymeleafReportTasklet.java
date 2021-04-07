package com.envisioncn.gssc.libra.batch.tasklets;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;

/**
 * Report tasklet based on Thymeleaf templating
 * @author jonnas
 * @date 2021-04-07
 */
public abstract class ThymeleafReportTasklet extends ReportTasklet {
    @Autowired
    protected TemplateEngine templateEngine;

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
}

