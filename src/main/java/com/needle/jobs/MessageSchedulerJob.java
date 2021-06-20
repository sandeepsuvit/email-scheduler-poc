package com.needle.jobs;

import java.util.Optional;
import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.needle.entities.Message;
import com.needle.repositories.MessageRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
public class MessageSchedulerJob extends QuartzJobBean {
	@Autowired
    private MessageRepository messageRepository;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing Message Job with key {}", context.getJobDetail().getKey());
		
		// Get the job metadata information
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		
		// Extract the message id from the data map
		UUID messageId = UUID.fromString(String.valueOf(jobDataMap.get("messageId")));
		
		if (null != messageId) {
			Optional<Message> messageOpt = messageRepository.findById(messageId);
			
			if (messageOpt.isPresent()) {
				Message message = messageOpt.get();
				message.setVisible(true);
				messageRepository.save(message);
			}
		}
	}
}
