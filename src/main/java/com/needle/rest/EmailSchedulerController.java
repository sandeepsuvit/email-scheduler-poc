package com.needle.rest;

import javax.validation.Valid;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.needle.dtos.email.EmailRequest;
import com.needle.dtos.email.BaseResponse;
import com.needle.service.EmailSchedulerService;

@RestController
@RequestMapping("emails")
public class EmailSchedulerController {
	@Autowired
	private EmailSchedulerService emailSchedulerService;

	/**
	 * Schedule an email job
	 * 
	 * @param request
	 * @return
	 * @throws SchedulerException
	 */
	@PostMapping("schedule")
	public ResponseEntity<BaseResponse> schedule(@Valid @RequestBody EmailRequest request)
			throws SchedulerException {
		return ResponseEntity.ok(emailSchedulerService.schedule(request));
	}
}
