package com.envisioncn.gssc.libra.core.metadata;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jonnas
 * @date 2021-04-06
 */
@Data
public class FlowMetadata {
    private String id;
    private List<StepMetadata> steps = new ArrayList<>();
    private SplitMetadata split;
    public void addStep(StepMetadata step) {
        steps.add(step);
        step.setFlow(this);
    }

    @Override
    public String toString() {
        return "FlowMetadata("
                + "split="+(split != null ? split.getId():"null")
                + ", steps="+ StringUtils.join(steps)
                + ")";
    }
}

