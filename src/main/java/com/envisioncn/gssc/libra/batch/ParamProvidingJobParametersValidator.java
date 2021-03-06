package com.envisioncn.gssc.libra.batch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jonnas
 * @date 2021-04-06
 */
public class ParamProvidingJobParametersValidator implements JobParametersProvider, JobParametersValidator, InitializingBean {
    private List<String> requiredKeys = Collections.emptyList();
    private List<String> optionalKeys = Collections.emptyList();

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        Assert.notNull(parameters, () -> "Parameters must nu be empty");
        ArrayList<String> missingParams = new ArrayList<>();
        for (String requiredKey:requiredKeys) {
            if (StringUtils.isEmpty(parameters.getString(requiredKey))) {
                missingParams.add(requiredKey);
            }
        }
        if (!missingParams.isEmpty()) {
            String s = StringUtils.join(missingParams, ",");
            throw new JobParametersInvalidException("Required parameters missing. " + s);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public List<String> getRequiredKeys() {
        return requiredKeys;
    }

    @Override
    public List<String> getOptionalKeys() {
        return optionalKeys;
    }

    public void setOptionalKeys(List<String> optionalKeys) {
        this.optionalKeys = optionalKeys;
    }
    public void setRequiredKeys(List<String> requiredKeys) {
        this.requiredKeys = requiredKeys;
    }
}

