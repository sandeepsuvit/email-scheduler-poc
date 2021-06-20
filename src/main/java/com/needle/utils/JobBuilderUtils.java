package com.needle.utils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class JobBuilderUtils {

	private JobBuilderUtils() {
		throw new AssertionError("Suppress default constructor for noninstantiability");
	}

	/**
	 * Build a generic job detail
	 * 
	 * @param <T>
	 * @param jobClass
	 * @param jobDataMap
	 * @param group
	 * @param description
	 * @return
	 */
	public static <T extends Job> JobDetail buildJobDetail(final Class<T> jobClass, JobDataMap jobDataMap, String group,
			String description) {
		// @formatter:off
		return JobBuilder.newJob(jobClass)
				.withIdentity(UUID.randomUUID().toString(), group)
				.withDescription(description)
				.usingJobData(jobDataMap)
				.build();
		// @formatter:on
	}

	/**
	 * Build a generic job detail that stores the instance of the job in db
	 * 
	 * @param <T>
	 * @param jobClass
	 * @param jobDataMap
	 * @param group
	 * @param description
	 * @param shouldStoreDurably
	 * @return
	 */
	public static <T extends Job> JobDetail buildJobDetail(final Class<T> jobClass, JobDataMap jobDataMap, String group,
			String description, boolean shouldStoreDurably) {
		if (shouldStoreDurably) {
			// @formatter:off
			return JobBuilder.newJob(jobClass)
					.withIdentity(UUID.randomUUID().toString(), group)
					.withDescription(description)
					.usingJobData(jobDataMap)
					.storeDurably() // Store the reference of the job in db
					.build();
			// @formatter:on
		} else {
			return buildJobDetail(jobClass, jobDataMap, group, description);
		}
	}

	/**
	 * Build a job trigger that triggers once on a date specified
	 * 
	 * @param jobDetail
	 * @param group
	 * @param description
	 * @param triggerStartTime
	 * @return
	 */
	public static Trigger buildTriggerThatTriggerOnce(final JobDetail jobDetail, String group, String description,
			Date triggerStartTime) {
		// @formatter:off
		// Schedule to run the trigger
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
				.simpleSchedule()
				.withMisfireHandlingInstructionFireNow();

		return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), group)
                .withDescription(description)
                .startAt(triggerStartTime) // When to start the trigger
                .withSchedule(scheduleBuilder) 
                .build();
		// @formatter:on
	}

	/**
	 * Build a job trigger that repeats with a time interval for ever
	 * 
	 * @param jobDetail
	 * @param group
	 * @param description
	 * @param repeatIntervalInSeconds
	 * @return
	 */
	public static Trigger buildTriggerThatRepeatsByInterval(final JobDetail jobDetail, String group, String description,
			int repeatIntervalInSeconds) {
		// @formatter:off
		// Schedule to run the trigger
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
				.simpleSchedule()
				.repeatForever()
				.withIntervalInSeconds(repeatIntervalInSeconds)
				.withMisfireHandlingInstructionFireNow();
		
		return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), group)
                .withDescription(description)
                .withSchedule(scheduleBuilder)
                .build();
		// @formatter:on
	}

	/**
	 * Build a job trigger with a cron expression
	 * 
	 * @param jobDetail
	 * @param group
	 * @param description
	 * @param cronExpression
	 * @return
	 */
	public static CronTrigger buildTriggerWithCron(final JobDetail jobDetail, String group, String description,
			String cronExpression) {
		// @formatter:off
		// Building triggers based on expressions
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		
		return TriggerBuilder.newTrigger()
				.withIdentity(jobDetail.getKey().getName(), group)
				.withDescription(description)
				.withSchedule(cronScheduleBuilder)
				.build();
		// @formatter:on
	}

	/**
	 * Build a job trigger with a cron expression and a timeout
	 * 
	 * @param jobDetail
	 * @param group
	 * @param description
	 * @param cronExpression
	 * @param timeoutSeconds
	 * @return
	 */
	public static CronTrigger buildTriggerWithCronAndTimeout(final JobDetail jobDetail, String group,
			String description, String cronExpression, long timeoutSeconds) {
		// Calculation end time
		Date endDate = DateTimeUtils.localDateTime2Date(LocalDateTime.now().plusSeconds(timeoutSeconds));

		// @formatter:off
		// Building triggers based on expressions
		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
		
		return TriggerBuilder.newTrigger()
				.withIdentity(jobDetail.getKey().getName(), group)
				.withDescription(description)
				.startNow() // Start on
				.endAt(endDate) // End at
				.withSchedule(cronScheduleBuilder)
				.build();
		// @formatter:on
	}
}
