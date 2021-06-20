package com.needle.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
public class EmaillSchedulerJob extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Executing Email Job with key {}", context.getJobDetail().getKey());

		// Get the job metadata information
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		
		String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipientEmail = jobDataMap.getString("email");
        
        sendMail("sandeep@mail.com", recipientEmail, subject, body);
	}

	/**
	 * Handle email sending logic
	 * 
	 * @param from
	 * @param to
	 * @param subject
	 * @param body
	 */
	private void sendMail(String from, String to, String subject, String body) {
		log.info("Sending email to: {}, from: {}, subject: {}, body: {}", from, to, subject, body);
	}
}
