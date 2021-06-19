package com.needle.rest;

import javax.validation.Valid;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.needle.dtos.email.EmailRequest;
import com.needle.dtos.email.EmailResponse;
import com.needle.service.EmailSchedulerService;

@RestController
public class EmailSchedulerController {
	@Autowired
	private EmailSchedulerService emailSchedulerService;

	@PostMapping("schedule")
	public ResponseEntity<EmailResponse> scheduleEmail(@Valid @RequestBody EmailRequest request)
			throws SchedulerException {
		return ResponseEntity.ok(emailSchedulerService.shceduleJob(request));
	}
}
