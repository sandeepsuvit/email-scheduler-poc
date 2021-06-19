package com.needle.service;

import org.quartz.SchedulerException;

import com.needle.dtos.email.EmailRequest;
import com.needle.dtos.email.BaseResponse;

public interface EmailSchedulerService {
	/**
	 * Schedule an email job
	 * 
	 * @param request
	 * @return
	 * @throws SchedulerException
	 */
	BaseResponse schedule(EmailRequest request) throws SchedulerException;
}
