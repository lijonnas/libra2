//package com.envisioncn.gssc.libra.schedule;
//
//import com.envisioncn.gssc.libra.utils.ApplicationContextUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.quartz.CronScheduleBuilder;
//import org.quartz.CronTrigger;
//import org.quartz.Job;
//import org.quartz.JobBuilder;
//import org.quartz.JobDataMap;
//import org.quartz.JobDetail;
//import org.quartz.JobKey;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.TriggerBuilder;
//import org.quartz.TriggerKey;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.util.ReflectionUtils;
//
//import java.lang.reflect.Method;
//
///**
// * @author jonnas
// * @date 2021-04-06
// */
//public class QuartzHandler {
//    private final static Logger log = LoggerFactory.getLogger(QuartzHandler.class);
//    public final static int TASK_STATUS_STOPED = 0;// 定时任务状态-停止
//    public final static int TASK_STATUS_RUNNING = 1;// 定时任务状态-启动
//
//    private final static String JOB_NAME_PREFIX = "JOB_";
//
//    private final static String TRIGGER_GROUP_NAME = "ISIM_TRIGGER_GROUP";
//
//    public final static String DEF_JOB_GROUP_NAME = "ISIM_JOB_GROUP";
//
//    /**
//     * 获取CronTrigger
//     * @param scheduler
//     * @param jobId
//     * @return
//     */
//    public static CronTrigger getCronTrigger(Scheduler scheduler, Integer jobId) {
//        try {
//            return (CronTrigger)scheduler.getTrigger(getTriggerKey(jobId));
//        } catch (SchedulerException e) {
//            log.error("获取CronTrigger异常：", e);
//            throw new QuartzJobException("获取CronTrigger异常：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 添加定时Job
//     * @param scheduler
//     * @param job
//     */
//    public static void addJob(Scheduler scheduler, ScheduleJob job) {
//        try {
//            // 构建job信息
//            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) ScheduleJob.class).withIdentity(getJobKey(job)).build();
//            // 添加具体Job信息
//            jobDetail.getJobDataMap().put(ScheduleJob.JOB_KEY, job);
//
//            // Cron表达式调度构建器
//            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression()).withMisfireHandlingInstructionDoNothing();
//
//            // 构建trigger
//            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(job.getId())).withSchedule(scheduleBuilder).build();
//
//            // 交给scheduler调度
//            scheduler.scheduleJob(jobDetail, trigger);
//
//            // 暂停JOB
//            if (job.getStatus() == TASK_STATUS_STOPED) {
//                pauseJob(scheduler, job);
//            }
//        } catch (SchedulerException e) {
//            log.error("添加定时Job失败：", e);
//            throw new QuartzJobException("添加定时Job失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 更新定时Job
//     * @param scheduler
//     * @param job
//     */
//    public static void updateJob(Scheduler scheduler, ScheduleJob job) {
//        try {
//            TriggerKey triggerKey = getTriggerKey(job.getId());
//
//            // 表达式调度构建器
//            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression()).withMisfireHandlingInstructionDoNothing();
//
//            CronTrigger trigger = getCronTrigger(scheduler, job.getId());
//
//            // Cron表达式调度构建器
//            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
//
//            // 添加具体Job信息
//            trigger.getJobDataMap().put(ScheduleJob.JOB_KEY, job);
//
//            // 重置job
//            scheduler.rescheduleJob(triggerKey, trigger);
//
//            // 暂停Job
//            if (job.getStatus() == TASK_STATUS_STOPED) {
//                pauseJob(scheduler, job);
//            }
//        } catch (SchedulerException e) {
//            log.error("更新定时Job失败：", e);
//            throw new QuartzJobException("更新定时Job失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 立即执行定时Job
//     * @param scheduler
//     * @param job
//     */
//    public static void runJob(Scheduler scheduler, ScheduleJob job) {
//        try {
//            JobDataMap dataMap = new JobDataMap();
//            dataMap.put(ScheduleJob.JOB_KEY, job);
//            scheduler.triggerJob(getJobKey(job), dataMap);
//        } catch (SchedulerException e) {
//            log.error("立即执行定时Job失败：", e);
//            throw new QuartzJobException("立即执行定时Job失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 暂停定时Job
//     */
//    public static void pauseJob(Scheduler scheduler, ScheduleJob job) {
//        try {
//            scheduler.pauseJob(getJobKey(job));
//        } catch (SchedulerException e) {
//            log.error("暂停定时Job失败：", e);
//            throw new QuartzJobException("暂停定时Job失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 恢复定时Job
//     */
//    public static void resumeJob(Scheduler scheduler, ScheduleJob job) {
//        try {
//            scheduler.resumeJob(getJobKey(job));
//        } catch (SchedulerException e) {
//            log.error("恢复定时Job失败：", e);
//            throw new QuartzJobException("恢复定时Job失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 删除定时Job
//     */
//    public static void deleteJob(Scheduler scheduler, ScheduleJob job) {
//        try {
//            scheduler.deleteJob(getJobKey(job));
//        } catch (SchedulerException e) {
//            log.error("删除定时Job失败：", e);
//            throw new QuartzJobException("删除定时Job失败：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 立即执行Job
//     * @param job
//     */
//    public static void execJob(ScheduleJob job) {
//        Method method = null;
//        try {
//            ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
//            Object jobClass = applicationContext.getBean(job.getBeanName());
//            String params = job.getParams();
//            if (StringUtils.isNotEmpty(params)) {
//                method = jobClass.getClass().getDeclaredMethod(job.getMethodName(), String.class);
//            } else {
//                method = jobClass.getClass().getDeclaredMethod(job.getMethodName());
//            }
//            ReflectionUtils.makeAccessible(method);
//            // 利用反射执行Job
//            if (StringUtils.isNotBlank(params)) {
//                method.invoke(jobClass, params);
//            } else {
//                method.invoke(jobClass);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("立即执行Job失败(" + job.getId() + ")", e);
//        }
//    }
//
//    /**
//     * 获取Trigger Key
//     * @param jobId
//     * @return
//     */
//    private static TriggerKey getTriggerKey(Integer jobId) {
//        return TriggerKey.triggerKey(JOB_NAME_PREFIX + jobId, TRIGGER_GROUP_NAME);
//    }
//
//    /**
//     * 获取Job Key
//     * @param job
//     * @return
//     */
//    private static JobKey getJobKey(ScheduleJob job) {
//        String group = StringUtils.isEmpty(job.getGroupName()) ? DEF_JOB_GROUP_NAME : job.getGroupName();
//        return JobKey.jobKey(JOB_NAME_PREFIX + job.getId(), group);
//    }
//}
