/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.scheduler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihub.asm360.adaptor.app.Constants;
import com.ihub.asm360.adaptor.core.dto.ASMAlert;
import com.ihub.asm360.adaptor.core.entity.Alert;
import com.ihub.asm360.adaptor.core.entity.DataMapper;
import com.ihub.asm360.adaptor.core.entity.Payload;
import com.ihub.asm360.adaptor.core.response.Application;
import com.ihub.asm360.adaptor.core.response.servicenow.ServiceNowResponse;
import com.ihub.asm360.adaptor.core.response.servicenow.ServiceNowResult;
import com.ihub.asm360.adaptor.core.scheduler.BaseScheduler;
import com.ihub.asm360.adaptor.core.service.AlertService;
import com.ihub.asm360.adaptor.core.service.DataMapperService;
import com.ihub.asm360.adaptor.core.service.GlobalPropertyService;
import com.ihub.asm360.adaptor.core.service.PayloadService;

/**
 * The Class EAGServiceNowScheduler.
 */
@Component
@EnableScheduling
public class EAGServiceNowScheduler extends BaseScheduler<ServiceNowResponse> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(EAGServiceNowScheduler.class);

	/** The Constant APPLICATION_INFRASTRUCTURE_ISSUES. */
	private static final String APPLICATION_INFRASTRUCTURE_ISSUES = "Application Infrastructure Issues";

	/** The Constant CONTACT_TYPE_EVENT_MONITORING. */
	private static final String CONTACT_TYPE_EVENT_MONITORING = "Event Monitoring";

	/** The Constant CONTACT_TYPE_EMAIL. */
	private static final String CONTACT_TYPE_EMAIL = "Email";

	/** The Constant TYPE_INFRA_APP. */
	private static final String TYPE_INFRA_APP = "INFRA_APP";

	/** The Constant TYPE_FUNCTIONAL_MONITORING. */
	private static final String TYPE_FUNCTIONAL_MONITORING = "FUNCTIONAL_MONITORING";

	/** The global property service. */
	@Autowired
	private GlobalPropertyService globalPropertyService;

	/** The data mapper service. */
	@Autowired
	private DataMapperService dataMapperService;

	/** The payload service. */
	@Autowired
	private PayloadService payloadService;

	/** The alert service. */
	@Autowired
	private AlertService alertService;

	/** The simple date format. */
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/** The object mapper. */
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Instantiates a new EAG service now scheduler.
	 */
	public EAGServiceNowScheduler() {
		super(Constants.Profile.EAGServiceNow.PROFILE_NAME);
	}

	/**
	 * Convert.
	 *
	 * @param text
	 *            the text
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ihub.asm360.adaptor.core.scheduler.BaseScheduler#convert(java.lang.
	 * String)
	 */
	@Override
	public List<ASMAlert> convert(String text) throws IOException {

		ServiceNowResponse response = objectMapper.readValue(text, new TypeReference<ServiceNowResponse>() {
		});

		LOGGER.debug("Converting ServiceNowResponse, {}", response);

		List<ServiceNowResult> resultList = response.getResult();

		String incidentUrl = globalPropertyService.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
				Constants.Profile.EAGServiceNow.INCIDENT_DETAIL_URL).getValue();

		List<ASMAlert> alertList = new ArrayList<>();

		for (ServiceNowResult result : resultList) {

			boolean unclassified = Boolean.TRUE;

			String category = result.getCategory();
			String subCategory = result.getSubcategory();

			Payload payload = new Payload();
			payload.setPayload(result.toString());
			payload.setSource(Constants.Profile.EAGServiceNow.PROFILE_NAME);
			payloadService.save(payload);
			
			LOGGER.debug("Category = {}, SubCategory = {}",category,subCategory);
			
			if ("Absence & Attendance".equalsIgnoreCase(subCategory)) {

				if ("velocity5".equalsIgnoreCase(category)) {

					String appName = "Other";
					String issueType = result.getU_issue_type();

					if ("Leave Balance".equalsIgnoreCase(issueType)) {
						appName = "Balance";
					} else if ("Workflow".equalsIgnoreCase(issueType)) {
						appName = "Workflow";
					} else if ("Missing Leave Types".equalsIgnoreCase(issueType)) {
						appName = "Eligibility";
					}

					List<Application> applications = getAppDetailsByName(appName);

					for (Application application : applications) {

						unclassified = Boolean.FALSE;

						ASMAlert alert = createAlert(incidentUrl, result, application);

						alertList.add(alert);

						List<ASMAlert> list = createCloseAlert(alert);

						if (!list.isEmpty()) {
							alertList.addAll(list);
						}

					}

				}
			} else if (category != null && !category.trim().isEmpty()) {

				List<Application> applications = getAppDetailsByName(category);

				for (Application application : applications) {

					unclassified = Boolean.FALSE;

					ASMAlert alert = createAlert(incidentUrl, result, application);

					alertList.add(alert);

					List<ASMAlert> list = createCloseAlert(alert);

					if (!list.isEmpty()) {
						alertList.addAll(list);
					}
				}
			} else {
				LOGGER.debug("Category not found!");
			}

			if (unclassified) {
				
				
				
				String priority = result.getPriority();

				LOGGER.debug("Found Unclassified, priority = {}", priority);
				
				if ("1".equals(priority)) {

					List<Application> applications = getAppDetailsByName("Unclassified P1");

					for (Application application : applications) {
						ASMAlert alert = createAlert(incidentUrl, result, application);

						alertList.add(alert);

						List<ASMAlert> list = createCloseAlert(alert);

						if (!list.isEmpty()) {
							alertList.addAll(list);
						}
					}
				} else if ("2".equals(priority)) {

					List<Application> applications = getAppDetailsByName("Unclassified P2");

					for (Application application : applications) {
						ASMAlert alert = createAlert(incidentUrl, result, application);

						alertList.add(alert);

						List<ASMAlert> list = createCloseAlert(alert);

						if (!list.isEmpty()) {
							alertList.addAll(list);
						}
					}
				} else {

					byte[] encodedAuth = Base64.encodeBase64(result.getNumber().getBytes(Charset.forName("US-ASCII")));

					List<Alert> currentAlerts = alertService.getByUuid(new String(encodedAuth));

					for (Alert currentAlert : currentAlerts) {

						ASMAlert alert = new ASMAlert();
						alert.setEventType(ASMAlert.EVENT);
						alert.setSubsystem(ASMAlert.SUB_SYSTEM);
						alert.setStatus(ASMAlert.STATUS_CLOSE);
						alert.setCaseType(ASMAlert.CASE_TYPE_REACTIVE);
						alert.setIncType(ASMAlert.CASE_TYPE_REACTIVE);
						alert.setAlertTickerMsg(currentAlert.getTitle());
						alert.setCasePriority(currentAlert.getPriority());
						alert.setCaseTitle(currentAlert.getTitle());
						alert.setSource(currentAlert.getSource());
						alert.setCreatedTime(currentAlert.getCreatedTime());
						alert.setAlertType(currentAlert.getType());
						alert.setCaseRef(currentAlert.getCaseRef());
						alert.setCellId(currentAlert.getCellId());
						alert.setCellName(currentAlert.getCellName());
						alert.setCellParent(currentAlert.getLob());
						alert.setLob(currentAlert.getLob());
						alert.setUuid(currentAlert.getAppUuid());
						alert.setUrlPath(currentAlert.getUrl());
						alert.setAlertUuid(currentAlert.getUuid());

						alertList.add(alert);

					}
				}
			}
		}
		return alertList;
	}

	/**
	 * Creates the alert.
	 *
	 * @param incidentUrl
	 *            the incident url
	 * @param result
	 *            the result
	 * @param application
	 *            the application
	 * @return the ASM alert
	 */
	private ASMAlert createAlert(String incidentUrl, ServiceNowResult result, Application application) {

		ASMAlert alert = new ASMAlert();

		byte[] encodedAuth = Base64.encodeBase64(result.getNumber().getBytes(Charset.forName("US-ASCII")));

		alert.setEventType(ASMAlert.EVENT);
		alert.setSubsystem(ASMAlert.SUB_SYSTEM);
		alert.setStatus(convertStatus(result.getState()));
		alert.setCaseType(ASMAlert.CASE_TYPE_REACTIVE);
		alert.setIncType(ASMAlert.CASE_TYPE_REACTIVE);
		alert.setAlertTickerMsg(result.getShort_description());
		alert.setCasePriority(convertPriority(result.getPriority()));
		alert.setCaseTitle(result.getShort_description());
		alert.setSource(convertSource(result.getCaller_id().getValue()));

		try {
			alert.setCreatedTime(simpleDateFormat.parse(result.getSys_created_on()));
		} catch (ParseException e) {
			alert.setCreatedTime(new Date());
			LOGGER.error(e.getMessage(), e);
		}

		String subCategory = result.getSubcategory();

		if (APPLICATION_INFRASTRUCTURE_ISSUES.equals(subCategory)) {
			alert.setAlertType(TYPE_INFRA_APP);
		} else {
			alert.setAlertType(TYPE_FUNCTIONAL_MONITORING);
		}

		alert.setCaseRef(result.getNumber() + "-" + application.getProgram().getProgramName());
		alert.setCellId(application.getSalsaAppId());
		alert.setCellName(application.getSalsaName());
		alert.setCellParent(application.getProgram().getProgramName());
		alert.setLob(application.getProgram().getProgramName());
		alert.setUuid(application.getUuid());
		alert.setUrlPath(String.format(incidentUrl, result.getSys_id()));
		alert.setAlertUuid(new String(encodedAuth));
		
		
		LOGGER.debug("Creating ASMAlert = {}", alert);
		
		return alert;
	}

	/**
	 * Creates the close alert.
	 *
	 * @param asmAlert
	 *            the asm alert
	 * @return the ASM alert
	 */
	private List<ASMAlert> createCloseAlert(ASMAlert asmAlert) {
		List<ASMAlert> list = new ArrayList<>();
		List<Alert> currentAlerts = alertService.getByUuid(asmAlert.getAlertUuid());

		for (Alert currentAlert : currentAlerts) {

			//if (currentAlert != null && !currentAlert.getAppUuid().equals(asmAlert.getCaseRef())) {
				ASMAlert alert = new ASMAlert();

				alert.setEventType(ASMAlert.EVENT);
				alert.setSubsystem(ASMAlert.SUB_SYSTEM);
				alert.setStatus(ASMAlert.STATUS_CLOSE);
				alert.setCaseType(ASMAlert.CASE_TYPE_REACTIVE);
				alert.setIncType(ASMAlert.CASE_TYPE_REACTIVE);
				alert.setAlertTickerMsg(currentAlert.getTitle());
				alert.setCasePriority(currentAlert.getPriority());
				alert.setCaseTitle(currentAlert.getTitle());
				alert.setSource(currentAlert.getSource());
				alert.setCreatedTime(currentAlert.getCreatedTime());
				alert.setAlertType(currentAlert.getType());
				alert.setCaseRef(currentAlert.getCaseRef());
				alert.setCellId(currentAlert.getCellId());
				alert.setCellName(currentAlert.getCellName());
				alert.setCellParent(currentAlert.getLob());
				alert.setLob(currentAlert.getLob());
				alert.setUuid(currentAlert.getAppUuid());
				alert.setUrlPath(currentAlert.getUrl());
				alert.setAlertUuid(currentAlert.getUuid());

				LOGGER.debug("Creating Close ASMAlert = {}", alert);
				
				list.add(alert);
			//}
		}

		return list;
	}

	/**
	 * Schedule.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ihub.asm360.adaptor.core.scheduler.BaseScheduler#schedule()
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	@Override
	public void schedule() {

		String isPullActive = globalPropertyService.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
				Constants.Profile.EAGServiceNow.IS_PULL_ACTIVE).getValue();

		LOGGER.debug("Pulling details from ServiceNow, {}", isPullActive);

		if (Constants.Strings.YES.equalsIgnoreCase(isPullActive)) {

			String interval = globalPropertyService.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
					Constants.Profile.EAGServiceNow.PULL_INTERVAL_SECONDS).getValue();

			Calendar fetchTime = Calendar.getInstance();
			fetchTime.add(Calendar.SECOND, -1 * (Integer.parseInt(interval)));
			String time = simpleDateFormat.format(fetchTime.getTime());

			LOGGER.debug("Fetching incidents updated after, {}", fetchTime.getTime());

			String username = globalPropertyService.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
					Constants.Profile.EAGServiceNow.INCIDENT_PULL_USERNAME).getValue();
			String encodedPassword = globalPropertyService
					.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
							Constants.Profile.EAGServiceNow.INCIDENT_PULL_PASSWORD)
					.getValue();
			String pullUrl = globalPropertyService.getPropertyByKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
					Constants.Profile.EAGServiceNow.INCIDENT_PULL_URL).getValue();

			pullUrl = String.format(pullUrl, time);

			byte[] decodedAuth = Base64.decodeBase64(encodedPassword.getBytes(Charset.forName("US-ASCII")));
			String password = new String(decodedAuth);
			HttpHeaders headers = createHeader(username, password);
			headers.setContentType(MediaType.APPLICATION_JSON);

			try {
				pull(headers, pullUrl);
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Convert status.
	 *
	 * @param status
	 *            the status
	 * @return the string
	 */
	private String convertStatus(String status) {
		String value = ASMAlert.STATUS_OPEN;
		String canceled = "8";
		String closed = "7";
		String resolved = "6";

		if (canceled.equals(status) || closed.equals(status) || resolved.equals(status)) {
			value = ASMAlert.STATUS_CLOSE;
		}

		return value;
	}

	/**
	 * Convert priority.
	 *
	 * @param priority
	 *            the priority
	 * @return the string
	 */
	private String convertPriority(String priority) {
		String value = ASMAlert.PRIORITY_P5;
		String critical = "1";
		String high = "2";
		String moderate = "3";
		String low = "4";

		if (critical.equals(priority)) {
			value = ASMAlert.PRIORITY_P1;
		} else if (high.equals(priority)) {
			value = ASMAlert.PRIORITY_P2;
		} else if (moderate.equals(priority)) {
			value = ASMAlert.PRIORITY_P3;
		} else if (low.equals(priority)) {
			value = ASMAlert.PRIORITY_P4;
		}

		return value;
	}

	/**
	 * Convert source.
	 *
	 * @param calledId
	 *            the called id
	 * @return the string
	 */
	private String convertSource(String calledId) {
		String value = "N/A";

		DataMapper dataMapper = dataMapperService.getDataByProfileTypeKey(Constants.Profile.EAGServiceNow.PROFILE_NAME,
				Constants.Profile.EAGServiceNow.DATA_MAPPER_CALLER_ID, calledId);

		if (dataMapper != null) {
			value = dataMapper.getValue();
		}

		return value;
	}
}
