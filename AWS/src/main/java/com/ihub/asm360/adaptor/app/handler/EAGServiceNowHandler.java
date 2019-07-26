/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * The Class EAGServiceNowHandler.
 */
@Service
public class EAGServiceNowHandler extends BaseHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EAGServiceNowHandler.class);

	/** The Constant STATE_NEW. */
	private static final String STATE_NEW = "New";
	
	/** The Constant STATE_RESOLVED. */
	private static final String STATE_RESOLVED = "Resolved";

	/** The Constant PRIORITY_CRITICAL. */
	private static final String PRIORITY_CRITICAL = "1 - Critical";
	
	/** The Constant PRIORITY_HIGH. */
	private static final String PRIORITY_HIGH = "2 - High";
	
	/** The Constant PRIORITY_MODERATE. */
	private static final String PRIORITY_MODERATE = "3 - Moderate";
	
	/** The Constant PRIORITY_LOW. */
	private static final String PRIORITY_LOW = "4 - Low";

	/** The Constant IMPACT_APP_INFRA_ISSUE. */
	private static final String IMPACT_APP_INFRA_ISSUE = "Application Infrastructure Issues";

	/** The Constant TYPE_INFRA_APP. */
	private static final String TYPE_INFRA_APP = "INFRA_APP";
	
	/** The Constant TYPE_FUNCTIONAL_MONITORING. */
	private static final String TYPE_FUNCTIONAL_MONITORING = "FUNCTIONAL_MONITORING";

	/** The global property service. */
	@Autowired
	private GlobalPropertyService globalPropertyService;
	
	/** The simple date format. */
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Instantiates a new EAG service now handler.
	 */
	public EAGServiceNowHandler() {
		super(Constants.Profile.EAGServiceNow.PROFILE_NAME);
	}

	/* (non-Javadoc)
	 * @see com.ihub.asm360.adaptor.core.handler.BaseHandler#convert(com.ihub.asm360.adaptor.core.request.IncidentRequest)
	 */
	@Override
	public List<ASMAlert> convert(IncidentRequest request) {

		String incidentUrl = globalPropertyService.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
				Constants.Profile.EAGServiceNow.INCIDENT_DETAIL_URL).getValue();

		List<ASMAlert> alertList = new ArrayList<>();

		List<Application> applications = getAppDetailsByName(request.getApplication());

		for (Application application : applications) {

			ASMAlert alert = new ASMAlert();
			alert.setEventType(ASMAlert.EVENT);
			alert.setSubsystem(ASMAlert.SUB_SYSTEM);

			String state = request.getStatus();
			if (STATE_NEW.equals(state)) {
				alert.setStatus(ASMAlert.STATUS_OPEN);
			} else if (STATE_RESOLVED.equals(state)) {
				alert.setStatus(ASMAlert.STATUS_CLOSE);
			}

			alert.setCaseType("Reactive");
			alert.setIncType("Reactive");
			alert.setAlertTickerMsg(request.getTitle());
			alert.setCaseTitle(request.getTitle());
			
			try {
				alert.setCreatedTime(simpleDateFormat.parse(request.getCreatedTime()));
			} catch (ParseException e) {
				alert.setCreatedTime(new Date());
				LOGGER.error(e.getMessage(), e);
			}

			String priority = request.getPriority();
			if (PRIORITY_CRITICAL.equalsIgnoreCase(priority)) {
				alert.setCasePriority(ASMAlert.PRIORITY_P1);
			} else if (PRIORITY_HIGH.equalsIgnoreCase(priority)) {
				alert.setCasePriority(ASMAlert.PRIORITY_P2);
			} else if (PRIORITY_MODERATE.equalsIgnoreCase(priority)) {
				alert.setCasePriority(ASMAlert.PRIORITY_P3);
			} else if (PRIORITY_LOW.equalsIgnoreCase(priority)) {
				alert.setCasePriority(ASMAlert.PRIORITY_P4);
			} else {
				alert.setCasePriority(ASMAlert.PRIORITY_P5);
			}

			alert.setSource(request.getSource());

			String impact = request.getImpact();
			if (IMPACT_APP_INFRA_ISSUE.equals(impact)) {
				alert.setAlertType(TYPE_INFRA_APP);
			} else {
				alert.setAlertType(TYPE_FUNCTIONAL_MONITORING);
			}

			alert.setCaseRef(request.getId() + "-" + application.getProgram().getProgramName());
			alert.setCellId(application.getSalsaAppId());
			alert.setCellName(application.getSalsaName());
			alert.setCellParent(application.getProgram().getProgramName());
			alert.setLob(application.getProgram().getProgramName());
			alert.setUuid(application.getUuid());
			alert.setUrlPath(incidentUrl + request.getSysId());

			alertList.add(alert);

		}

		return alertList;
	}

}
