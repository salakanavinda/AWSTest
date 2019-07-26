/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.scheduler;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihub.asm360.adaptor.app.Constants;
import com.ihub.asm360.adaptor.core.dto.ASMAlert;
import com.ihub.asm360.adaptor.core.response.servicenow.ServiceNowResponse;
import com.ihub.asm360.adaptor.core.scheduler.BaseScheduler;

/**
 * The Class WileyServiceNowScheduler.
 */
@Component
public class WileyServiceNowScheduler extends BaseScheduler<ServiceNowResponse> {

	/** The object mapper. */
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Instantiates a new wiley service now scheduler.
	 */
	public WileyServiceNowScheduler() {
		super(Constants.Profile.WileyServiceNow.PROFILE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ihub.asm360.adaptor.core.scheduler.BaseScheduler#convert(com.ihub.asm360.adaptor.core.response.BaseResponse)
	 */
	@Override
	public List<ASMAlert> convert(String text) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Schedule.
	 */
	// @Scheduled(cron = "0 0 */1 * * *")
	public void schedule() {

	}

}
