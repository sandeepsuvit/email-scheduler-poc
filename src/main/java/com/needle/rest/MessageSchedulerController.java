package com.needle.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.needle.dtos.email.BaseResponse;
import com.needle.dtos.message.MessageRequest;
import com.needle.services.MessageSchedulerService;

@RestController
@RequestMapping("messages")
public class MessageSchedulerController {
	@Autowired
	private MessageSchedulerService schedulerService;

	/**
	 * Schedule a message job
	 * 
	 * @param request
	 * @return
	 * @throws SchedulerException
	 */
	@PostMapping("schedule")
	public ResponseEntity<BaseResponse> schedule(@Valid @RequestBody MessageRequest request) throws SchedulerException {
		return ResponseEntity.ok(schedulerService.schedule(request));
	}

	/**
	 * Unschedule a message job
	 * 
	 * @param messageId
	 * @return
	 * @throws SchedulerException
	 */
	@DeleteMapping(path = "schedule/{messageId}")
	public ResponseEntity<BaseResponse> unschedule(@PathVariable(name = "messageId") UUID messageId)
			throws SchedulerException {
		return ResponseEntity.ok(schedulerService.unschedule(messageId));
	}
}
