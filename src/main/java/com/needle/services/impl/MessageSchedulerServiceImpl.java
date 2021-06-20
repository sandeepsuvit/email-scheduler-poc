package com.needle.services.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.needle.dtos.email.BaseResponse;
import com.needle.dtos.message.MessageRequest;
import com.needle.entities.Message;
import com.needle.jobs.MessageSchedulerJob;
import com.needle.repositories.MessageRepository;
import com.needle.services.MessageSchedulerService;
import com.needle.utils.CommonConstants;
import com.needle.utils.JobBuilderUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSchedulerServiceImpl implements MessageSchedulerService {
	private static final String JOB_GROUP = "message-job-group";
	private static final String TRIGGER_GROUP = "message-trigger-group";

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private Scheduler scheduler;

	@Override
	public BaseResponse schedule(MessageRequest request) throws SchedulerException {
		log.info(CommonConstants.LOGS.ENTRY, "schedule", this.getClass().getName());

		Message message = new Message();
		message.setContent(request.getContent());
		message.setVisible(false);
		message.setMakeVisibleAt(request.getMakeVisibleAt());

		// save messages in table
		message = messageRepository.save(message);

		JobDetail jobDetail = buildJobDetail(request, message.getId());
		Trigger trigger = buildJobTrigger(jobDetail, message.getMakeVisibleAt());

		scheduler.scheduleJob(jobDetail, trigger);

		log.info(CommonConstants.LOGS.EXIT, "schedule", this.getClass().getName());
		
		// @formatter:off
        return BaseResponse.builder()
        		.success(true)
        		.jobId(jobDetail.getKey().getName())
        		.jobGroup(jobDetail.getKey().getGroup())
        		.message("Message Scheduled Successfully!")
        		.build();
        // @formatter:on
	}

	@Override
	public BaseResponse unschedule(UUID messageId) throws SchedulerException {
		log.info(CommonConstants.LOGS.ENTRY, "unschedule", this.getClass().getName());

		Message message = messageRepository.findById(messageId)
				.orElseThrow(() -> new RuntimeException("Unable to find the message"));

		message.setVisible(false);

		// update messages in table
		messageRepository.save(message);

		// Reference to the job
		JobKey jobKey = new JobKey(message.getId().toString(), JOB_GROUP);
		// Reference to the trigger
		TriggerKey triggerKey = new TriggerKey(jobKey.getName(), TRIGGER_GROUP);

		scheduler.deleteJob(jobKey);
		scheduler.unscheduleJob(triggerKey);

		log.info(CommonConstants.LOGS.EXIT, "unschedule", this.getClass().getName());
		
		// @formatter:off
        return BaseResponse.builder()
        		.success(true)
        		.jobId(jobKey.getName())
        		.jobGroup(jobKey.getGroup())
        		.message("Message Unscheduled Successfully!")
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
	private JobDetail buildJobDetail(MessageRequest request, UUID messageId) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("messageId", messageId);
		jobDataMap.put("content", request.getContent());

		return JobBuilderUtils.buildJobDetail(MessageSchedulerJob.class, jobDataMap, JOB_GROUP, "Send Message Job");
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
	private Trigger buildJobTrigger(JobDetail jobDetail, LocalDateTime startAt) {
		return JobBuilderUtils.buildTriggerThatTriggerOnce(jobDetail, TRIGGER_GROUP, "Send Message Trigger",
				getStartAt(startAt));
	}

	/**
	 * Local Date Time to Date converter
	 * 
	 * @param dateToConvert
	 * @return
	 */
	private Date getStartAt(LocalDateTime dateToConvert) {
		return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}
}
