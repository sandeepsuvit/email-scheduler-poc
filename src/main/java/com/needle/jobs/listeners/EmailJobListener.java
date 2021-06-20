package com.needle.jobs.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.util.ObjectUtils;

import com.needle.utils.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailJobListener implements JobListener {
	public static final String LISTENER_NAME = "EMAIL_JOB_LISTENER";

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		log.info(CommonConstants.LOGS.ENTRY, "jobExecutionVetoed", this.getClass().getName());
		log.info(CommonConstants.LOGS.EXIT, "jobExecutionVetoed", this.getClass().getName());
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		log.info(CommonConstants.LOGS.ENTRY, "jobToBeExecuted", this.getClass().getName());

		String jobName = context.getJobDetail().getKey().toString();

		log.info("Job : {} is going to start...", jobName);

		log.info(CommonConstants.LOGS.EXIT, "jobToBeExecuted", this.getClass().getName());
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		log.info(CommonConstants.LOGS.ENTRY, "jobWasExecuted", this.getClass().getName());

		String jobName = context.getJobDetail().getKey().toString();

		log.info("Job : {} is finished...", jobName);

		if (null != jobException && !ObjectUtils.isEmpty(jobException.getMessage())) {
			log.error("Exception thrown by: {} Exception: {}", jobName, jobException.getMessage());
		}

		log.info(CommonConstants.LOGS.EXIT, "jobWasExecuted", this.getClass().getName());
	}

}
