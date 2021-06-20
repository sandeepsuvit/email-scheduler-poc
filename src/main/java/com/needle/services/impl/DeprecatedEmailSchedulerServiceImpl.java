package com.needle.services.impl;

import java.time.ZonedDateTime;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import com.needle.dtos.email.BaseResponse;
import com.needle.dtos.email.EmailRequest;
import com.needle.jobs.EmaillSchedulerJob;
import com.needle.jobs.listeners.EmailJobListener;
import com.needle.services.EmailSchedulerService;
import com.needle.utils.CommonConstants;
import com.needle.utils.JobBuilderUtils;

import lombok.extern.slf4j.Slf4j;

@Deprecated(forRemoval = true)
@Slf4j
//@Service
public class DeprecatedEmailSchedulerServiceImpl implements EmailSchedulerService {
	private static final String JOB_GROUP = "email-job-group";
	private static final String TRIGGER_GROUP = "email-trigger-group";

	@Autowired
	private Scheduler scheduler;

	@Override
	public BaseResponse schedule(EmailRequest request) throws SchedulerException {
		log.info(CommonConstants.LOGS.ENTRY, "schedule", this.getClass().getName());
		// Get the delivery time
		ZonedDateTime deliveryTime = ZonedDateTime.of(request.getDeliverOn(), request.getTimeZone());

		// Check if the message delivery time specified is before current date as per
		// the time zone
		if (deliveryTime.isBefore(ZonedDateTime.now())) {
			// @formatter:off
			return BaseResponse.builder()
					.success(false)
					.message("Invalid delivery time specified")
					.build();
			// @formatter:on
		}

		JobDetail jobDetail = buildJobDetail(request);
		Trigger trigger = buildJobTrigger(jobDetail, deliveryTime);

		// Listener attached to jobKey
		scheduler.getListenerManager().addJobListener(new EmailJobListener(), KeyMatcher.keyEquals(jobDetail.getKey()));

		// Listener attached to group named "email-group" only.
		// scheduler.getListenerManager().addJobListener(new EmailJobListener(),
		// GroupMatcher.jobGroupEquals(JOB_GROUP));

		scheduler.scheduleJob(jobDetail, trigger);

		log.info(CommonConstants.LOGS.EXIT, "schedule", this.getClass().getName());
		
		// @formatter:off
        return BaseResponse.builder()
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

		return JobBuilderUtils.buildJobDetail(EmaillSchedulerJob.class, jobDataMap, JOB_GROUP, "Send Email Job", true);
	}

	/**
	 * A scheduled object should have a trigger associated with a job detail.
	 * Trigger is where we define the specific properties or behavior of the job.
	 * 
	 * Build job trigger
	 * 
	 * @param jobDetail
	 * @param startAt
	 * @return
	 */
	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
		return JobBuilderUtils.buildTriggerThatTriggerOnce(jobDetail, TRIGGER_GROUP, "Send Email Trigger",
				Date.from(startAt.toInstant()));
	}

}
