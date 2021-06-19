package com.needle.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.needle.dtos.email.BaseResponse;
import com.needle.dtos.message.MessageRequest;
import com.needle.entities.Message;
import com.needle.job.MessageSchedulerJob;
import com.needle.repository.MessageRepository;
import com.needle.service.MessageSchedulerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSchedulerServiceImpl implements MessageSchedulerService {
	private static final String JOB_GROUP = "message-group";
	private static final String TRIGGER_GROUP = "message-triggers";

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private Scheduler scheduler;

	@Override
	public BaseResponse schedule(MessageRequest request) throws SchedulerException {
		log.info("Entering method {} from class {}", "schedule", this.getClass().getName());

		Message message = new Message();
		message.setContent(request.getContent());
		message.setVisible(false);
		message.setMakeVisibleAt(request.getMakeVisibleAt());

		// save messages in table
		message = messageRepository.save(message);

		JobDetail jobDetail = buildJobDetail(request, message.getId());
		Trigger trigger = buildJobTrigger(jobDetail, message.getMakeVisibleAt());
		
		scheduler.scheduleJob(jobDetail, trigger);
		
		log.info("Exiting method {} from class {}", "schedule", this.getClass().getName());
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
		log.info("Entering method {} from class {}", "unschedule", this.getClass().getName());

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

		log.info("Exiting method {} from class {}", "unschedule", this.getClass().getName());
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

		// @formatter:off
		return JobBuilder.newJob(MessageSchedulerJob.class)
				.withIdentity(messageId.toString(), JOB_GROUP)
				.withDescription("Send Message Job")
				.usingJobData(jobDataMap)
//                .storeDurably() // Store the reference of the job in db
				.build();
		// @formatter:on
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
		// @formatter:off
		return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP)
                .withDescription("Send Message Trigger")
                .startAt(getStartAt(startAt)) // When to start the trigger
                .withSchedule(
                		SimpleScheduleBuilder
                			.simpleSchedule()
                			.withMisfireHandlingInstructionFireNow()) // Schedule to run the trigger
                .build();
		// @formatter:on
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
