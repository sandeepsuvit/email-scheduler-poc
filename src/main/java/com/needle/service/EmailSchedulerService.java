package com.needle.service;

import org.quartz.SchedulerException;

import com.needle.dtos.EmailRequest;
import com.needle.dtos.EmailResponse;

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
