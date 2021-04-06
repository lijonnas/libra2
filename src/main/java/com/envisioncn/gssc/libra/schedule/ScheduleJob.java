package com.envisioncn.gssc.libra.schedule;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author jonnas
 * @date 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleJob  {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String taskName;// 任务名称

    private String groupName;// Group名称

    @NotBlank(message = "Bean名称不能为空!")
    private String beanName;// SpringBean名称

    @NotBlank(message = "方法名称不能为空!")
    private String methodName;// 方法名称

    private String params;// 参数

    @NotBlank(message = "cron表达式不能为空!")
    private String cronExpression;// cron表达式

    private Integer status;// 任务状态

    private String remark;// 备注

    private Integer tenantId;// 租户Id

    public final static String BEAN_NAME = "bean_name";

    public final static String METHOD_NAME = "method_name";

    public final static String STATUS = "status";

    public static final String JOB_KEY = "JOB_KEY";// Job调度key
}
