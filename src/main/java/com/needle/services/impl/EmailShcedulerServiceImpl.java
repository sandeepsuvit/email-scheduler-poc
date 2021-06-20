package com.needle.services.impl;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.needle.dtos.email.BaseResponse;
import com.needle.dtos.email.EmailRequest;
import com.needle.jobs.EmaillSchedulerJob;
import com.needle.jobs.listeners.EmailJobListener;
import com.needle.services.AbstractJobBuilder;
import com.needle.services.EmailSchedulerService;
import com.needle.utils.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailShcedulerServiceImpl extends AbstractJobBuilder implements EmailSchedulerService {

	@Autowired
	public EmailShcedulerServiceImpl(Scheduler scheduler) {
		super(scheduler);
	}

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

		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("email", request.getEmail());
		jobDataMap.put("subject", request.getSubject());
		jobDataMap.put("body", request.getBody());
		// Used for persisting the job in the db and not removing after execution
		jobDataMap.put("storeDurably", true);

		String key = UUID.randomUUID().toString();

		// Listener attached to jobKey
		addJobListener(key, EmaillSchedulerJob.class, new EmailJobListener());

		// Listener attached to group only.
//		addJobListenerByGroup(EmaillSchedulerJob.class, new EmailJobListener());

		JobDetail jobDetail = addJob(EmaillSchedulerJob.class, key, Date.from(deliveryTime.toInstant()), jobDataMap);

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

}
