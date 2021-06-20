package com.needle.services;

import java.util.UUID;

import org.quartz.SchedulerException;

import com.needle.dtos.email.BaseResponse;
import com.needle.dtos.message.MessageRequest;

public interface MessageSchedulerService {
	/**
	 * Schedule a message
	 * 
	 * @param request
	 * @return
	 * @throws SchedulerException 
	 */
	BaseResponse schedule(MessageRequest request) throws SchedulerException;

	/**
	 * Unschedule a message
	 * 
	 * @param messageId
	 * @return
	 * @throws SchedulerException 
	 */
	BaseResponse unschedule(UUID messageId) throws SchedulerException;
}
	