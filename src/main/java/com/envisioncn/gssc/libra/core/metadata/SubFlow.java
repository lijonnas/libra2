package com.envisioncn.gssc.libra.core.metadata;

import lombok.Data;

/**
 * @author jonnas
 * @date 2021-04-06
 */
@Data
public abstract class SubFlow {
    protected  String id;
    protected String nextId;
}
