package com.needle.services;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.util.CollectionUtils;

import com.needle.utils.DateTimeUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractJobBuilder {
	private Scheduler scheduler = null;

	private static final String JOB_NAME_PREFIX = "JOB_"; // Task name prefix

	/**
	 * This needs to be called by the class extending this class
	 * 
	 * @param scheduler
	 */
	protected void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Execute task after specified time (only once)
	 * 
	 * @param jobClass
	 * @param jobName
	 * @param triggerStartTime
	 * @param dataMap
	 * @return
	 */
	@SneakyThrows
	protected JobDetail addJob(Class<? extends Job> jobClass, String jobName, Date triggerStartTime,
			JobDataMap dataMap) {
		// use job Class name as group name
		String groupName = jobClass.getSimpleName();

		// Create task trigger
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName).startAt(triggerStartTime)
				.build();

		// Binding triggers and tasks into the scheduler
		return this.scheduleJob(jobClass, groupName, jobName, dataMap, trigger);
	}

	/**
	 * Tasks with triggers (executed multiple times)
	 * 
	 * @param jobClass
	 * @param jobName
	 * @param cronExpression
	 * @param dataMap
	 * @return
	 */
	@SneakyThrows
	protected JobDetail addJobWithCron(Class<? extends Job> jobClass, String jobName, String cronExpression,
			JobDataMap dataMap) {
		// use job Class name as group name
		String groupName = jobClass.getSimpleName();

		// Building triggers based on expressions
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName)
				.withSchedule(cronScheduleBuilder).build();

		// Binding triggers and tasks into the scheduler
		return this.scheduleJob(jobClass, groupName, jobName, dataMap, cronTrigger);
	}

	/**
	 * Tasks with triggers and a specified time period (immediate execution)
	 * 
	 * @param jobClass
	 * @param jobName
	 * @param cronExpression
	 * @param timeoutSeconds
	 * @param dataMap
	 * @return
	 */
	@SneakyThrows
	protected JobDetail addJobWithCron(Class<? extends Job> jobClass, String jobName, String cronExpression,
			long timeoutSeconds, JobDataMap dataMap) {
		// use job Class name as group name
		String groupName = jobClass.getSimpleName();

		// Calculation end time
		Date endDate = DateTimeUtils.localDateTime2Date(LocalDateTime.now().plusSeconds(timeoutSeconds));

		// Build a trigger based on an expression and specify a time period
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, groupName).startNow().endAt(endDate)
				.withSchedule(cronScheduleBuilder).build();

		// Binding triggers and tasks into the scheduler
		return this.scheduleJob(jobClass, groupName, jobName, dataMap, cronTrigger);
	}

	/**
	 * Method that schedules the job by setting a job name
	 * 
	 * @param jobClass
	 * @param groupName
	 * @param jobName
	 * @param dataMap
	 * @param trigger
	 * @return
	 */
	@SneakyThrows
	private JobDetail scheduleJob(Class<? extends Job> jobClass, String groupName, String jobName, JobDataMap dataMap,
			Trigger trigger) {
		jobName = String.format("%s%s", JOB_NAME_PREFIX, jobName);
		log.info("Create task with name: {}", jobName);

		final String storeDurablyKey = "storeDurably";

		// Check if the data needs to be stored in the db even after execution
		final boolean storeDurably = dataMap.containsKey(storeDurablyKey) ? dataMap.getBooleanValue(storeDurablyKey)
				: false;

		// Remove Key if not used
		dataMap.remove(storeDurablyKey);

		// @formatter:off
		// Create task
		JobDetail jobDetail = JobBuilder.newJob(jobClass)
				.withIdentity(jobName, groupName)
				.setJobData(dataMap)
				.storeDurably(storeDurably)
				.build();
		// @formatter:on

		// Binding triggers and tasks into the scheduler
		scheduler.scheduleJob(jobDetail, trigger);

		return jobDetail;
	}

	/**
	 * Delete a job referring to its key and group
	 * 
	 * @param name
	 * @param group
	 * @return
	 */
	@SneakyThrows
	protected boolean deleteJob(String name, String group) {
		JobKey jobKey = new JobKey(name, group);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		if (jobDetail == null) {
			throw new RuntimeException("Task does not exist");
		}

		return scheduler.deleteJob(jobKey);
	}

	/**
	 * Modify the execution time of a job (Applicable to cron triggers for now)
	 * 
	 * @param name
	 * @param group
	 * @param time
	 * @return
	 */
	@SneakyThrows
	protected boolean modifyJob(String name, String group, String time) {
		Date date = null;
		TriggerKey triggerKey = new TriggerKey(name, group);
		CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		String oldTime = cronTrigger.getCronExpression();

		if (!oldTime.equalsIgnoreCase(time)) {
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(time);
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
					.withSchedule(cronScheduleBuilder).build();
			date = scheduler.rescheduleJob(triggerKey, trigger);
		}

		return date != null;
	}

	/**
	 * Get Job status
	 * 
	 * @param name
	 * @param group
	 * @return
	 */
	@SneakyThrows
	protected TriggerState getJobState(String name, String group) {
		TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
		return scheduler.getTriggerState(triggerKey);
	}

	/**
	 * Get Job state
	 * 
	 * @param triggerKey
	 * @return
	 */
	@SneakyThrows
	protected TriggerState getJobState(TriggerKey triggerKey) {
		return scheduler.getTriggerState(triggerKey);
	}

	/**
	 * Pause all Jobs
	 */
	@SneakyThrows
	protected void pauseAllJob() {
		scheduler.pauseAll();
	}

	/**
	 * Pause a Job by its key and group
	 * 
	 * @param name
	 * @param group
	 */
	@SneakyThrows
	protected void pauseJob(String name, String group) {
		JobKey jobKey = new JobKey(name, group);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);

		if (jobDetail == null) {
			throw new RuntimeException("Task does not exist");
		}

		scheduler.pauseJob(jobKey);
	}

	/**
	 * Resume all Jobs
	 */
	@SneakyThrows
	protected void resumeAllJob() {
		scheduler.resumeAll();
	}

	/**
	 * Resume a Job by key and group
	 * 
	 * @param name
	 * @param group
	 */
	@SneakyThrows
	protected void resumeJob(String name, String group) {
		JobKey jobKey = new JobKey(name, group);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		if (jobDetail == null) {
			throw new RuntimeException("Task does not exist");
		}
		scheduler.resumeJob(jobKey);
	}

	/**
	 * Register task listener (Global listener, listen to all tasks)
	 * 
	 * @param listener
	 */
	@SneakyThrows
	protected void addJobListener(JobListener listener) {
		scheduler.getListenerManager().addJobListener(listener);
	}

	/**
	 * Add task listener to a Job
	 * 
	 * @param name
	 * @param jobClass
	 * @param listener
	 */
	@SneakyThrows
	protected void addJobListener(String name, Class<? extends Job> jobClass, JobListener listener) {
		name = String.format("%s%s", JOB_NAME_PREFIX, name);

		String group = jobClass.getSimpleName();
		JobKey jobKey = new JobKey(name, group);

		Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobKey);

		scheduler.getListenerManager().addJobListener(listener, matcher);
	}

	/**
	 * Add task listener to a Job Group
	 * 
	 * @param jobClass
	 * @param listener
	 */
	@SneakyThrows
	protected void addJobListenerByGroup(Class<? extends Job> jobClass, JobListener listener) {
		String group = jobClass.getSimpleName();
		Matcher<JobKey> matcher = GroupMatcher.groupEquals(group);
		scheduler.getListenerManager().addJobListener(listener, matcher);
	}

	/**
	 * Register trigger listener (Global listener, listen to all tasks)
	 * 
	 * @param listener
	 */
	@SneakyThrows
	protected void addTriggerListener(TriggerListener listener) {
		scheduler.getListenerManager().addTriggerListener(listener);
	}

	/**
	 * Query the number of running tasks through group
	 * 
	 * @param jobClass
	 * @return
	 */
	@SneakyThrows
	protected long getRunningJobCountByGroup(Class<? extends Job> jobClass) {
		String groupName = jobClass.getSimpleName();
		GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals(groupName);
		Set<JobKey> jobKeySet = scheduler.getJobKeys(matcher);

		if (!CollectionUtils.isEmpty(jobKeySet)) {
			return jobKeySet.stream().filter(d -> d.getGroup().equals(groupName)).count();
		}

		return 0;
	}
}
