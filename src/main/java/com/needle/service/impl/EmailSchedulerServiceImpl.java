package com.needle.service.impl;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.needle.dtos.EmailRequest;
import com.needle.dtos.EmailResponse;
import com.needle.job.EmaillSchedulerJob;
import com.needle.service.EmailSchedulerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailSchedulerServiceImpl implements EmailSchedulerService {
	private static final String JOB_GROUP = "email-group";
	private static final String TRIGGER_GROUP = "email-triggers";

	@Autowired
	private Scheduler scheduler;

	@Override
	public EmailResponse shceduleJob(EmailRequest request) throws SchedulerException {
		log.info("Entering method {} from class {}", "shceduleJob", this.getClass().getName());
		// Get the delivery time
		ZonedDateTime deliveryTime = ZonedDateTime.of(request.getDeliverOn(), request.getTimeZone());

		// Check if the message delivery time specified is before current date as per
		// the time zone
		if (deliveryTime.isBefore(ZonedDateTime.now())) {
			// @formatter:off
			return EmailResponse.builder()
					.success(false)
					.message("Invalid delivery time specified")
					.build();
			// @formatter:on
		}

		JobDetail jobDetail = buildJobDetail(request);
		Trigger trigger = buildJobTrigger(jobDetail, deliveryTime);

		scheduler.scheduleJob(jobDetail, trigger);

		log.info("Exiting method {} from class {}", "shceduleJob", this.getClass().getName());
		// @formatter:off
        return EmailResponse.builder()
        		.success(true)
        		.jobId(jobDetail.getKey().getName())
        		.jobGroup(jobDetail.getKey().getGroup())
        		.message("Email Scheduled Successfully!")
        		.build();
        // @formatter:on
	}

	/**
	 * A scheduled object should have a job detail reference
	 * 
	 * Build Job detail
	 * 
	 * @param request
	 * @return
	 */
	private JobDetail buildJobDetail(EmailRequest request) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("email", request.getEmail());
		jobDataMap.put("subject", request.getSubject());
		jobDataMap.put("body", request.getBody());

		// @formatter:off
		return JobBuilder.newJob(EmaillSchedulerJob.class)
				.withIdentity(UUID.randomUUID().toString(), JOB_GROUP)
				.withDescription("Send Email Job")
				.usingJobData(jobDataMap)
                .storeDurably() // Store the reference of the job in db
				.build();
		// @formatter:on
	}

	/**
	 * A scheduled object should have a trigger associated with a job detail
	 * 
	 * Build job trigger
	 * 
	 * @param jobDetail
	 * @param startAt
	 * @return
	 */
	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
		// @formatter:off
		return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP)
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
		// @formatter:on
	}

}
