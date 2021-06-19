package com.needle.service;

import org.quartz.SchedulerException;

import com.needle.dtos.email.EmailRequest;
import com.needle.dtos.email.EmailResponse;

public interface EmailSchedulerService {
	/**
	 * Schedule an email job
	 * 
	 * @param request
	 * @return
	 * @throws SchedulerException
	 */
	EmailResponse shceduleJob(EmailRequest request) throws SchedulerException;
}
