/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ihub.asm360.adaptor.app.Constants;
import com.ihub.asm360.adaptor.core.dto.ASMAlert;
import com.ihub.asm360.adaptor.core.handler.BaseHandler;
import com.ihub.asm360.adaptor.core.request.IncidentRequest;
import com.ihub.asm360.adaptor.core.response.Application;
import com.ihub.asm360.adaptor.core.service.GlobalPropertyService;

/**
 * The Class EAGDynatraceHandler.
 */
@Service
public class EAGDynatraceHandler extends BaseHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EAGDynatraceHandler.class);

	/** The Constant STATE_OPEN. */
	private static final String STATE_OPEN = "OPEN";

	/** The Constant STATE_RESOLVED. */
	private static final String STATE_RESOLVED = "RESOLVED";

	/** The Constant STATE_MERGED. */
	private static final String STATE_MERGED = "MERGED";

	/** The Constant TYPE_APPLICATION. */
	private static final String TYPE_APPLICATION = "APPLICATION";

	/** The Constant TYPE_SERVICE. */
	private static final String TYPE_SERVICE = "SERVICE";

	/** The Constant TYPE_INFRASTRUCTURE. */
	private static final String TYPE_INFRASTRUCTURE = "INFRASTRUCTURE";

	/** The Constant TYPE_INFRA_APP. */
	private static final String TYPE_INFRA_APP = "INFRA_APP";

	/** The Constant TYPE_FUNCTIONAL_MONITORING. */
	private static final String TYPE_FUNCTIONAL_MONITORING = "FUNCTIONAL_MONITORING";

	/** The global property service. */
	@Autowired
	private GlobalPropertyService globalPropertyService;

	/**
	 * Instantiates a new EAG dynatrace handler.
	 */
	public EAGDynatraceHandler() {
		super(Constants.Profile.EAGDynatrace.PROFILE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ihub.asm360.adaptor.core.handler.BaseHandler#convert(com.ihub.asm360.
	 * adaptor.core.request.IncidentRequest)
	 */
	@Override
	public List<ASMAlert> convert(IncidentRequest request) {
		
		String incidentUrl = globalPropertyService.getPropertyByKey(Constants.Profile.EAGDynatrace.PROFILE_NAME,
				Constants.Profile.EAGDynatrace.INCIDENT_DETAIL_URL).getValue();

		List<ASMAlert> alertList = new ArrayList<>();

		List<Application> applications = getAppDetailsByName(request.getApplication());

		for (Application application : applications) {

			ASMAlert asmAlert = new ASMAlert();
			asmAlert.setEventType(ASMAlert.EVENT);
			asmAlert.setSubsystem(ASMAlert.SUB_SYSTEM);

			String state = request.getStatus();
			if (STATE_OPEN.equals(state) || STATE_MERGED.equals(state)) {
				asmAlert.setStatus(ASMAlert.STATUS_OPEN);
			} else {
				asmAlert.setStatus(ASMAlert.STATUS_CLOSE);
			}

			asmAlert.setCaseType("Reactive");
			asmAlert.setIncType("Reactive");
			asmAlert.setAlertTickerMsg(request.getTitle());
			asmAlert.setCaseTitle(request.getTitle());
			asmAlert.setCasePriority(request.getSeverity());
			asmAlert.setSource("Dynatrace");

			String type = request.getType();
			if (TYPE_APPLICATION.equals(type) || TYPE_INFRASTRUCTURE.equals(type)) {
				asmAlert.setAlertType(TYPE_INFRA_APP);
			} else {
				asmAlert.setAlertType(TYPE_FUNCTIONAL_MONITORING);
			}

			asmAlert.setCaseRef(request.getId() + "-" + application.getProgram().getProgramName());
			asmAlert.setCellId(application.getSalsaAppId());
			asmAlert.setCellName(application.getSalsaName());
			asmAlert.setCellParent(application.getProgram().getProgramName());
			asmAlert.setLob(application.getProgram().getProgramName());
			asmAlert.setUuid(application.getUuid());
			asmAlert.setUrlPath(incidentUrl + request.getSysId());
			alertList.add(asmAlert);

		}

		return alertList;
	}

}
