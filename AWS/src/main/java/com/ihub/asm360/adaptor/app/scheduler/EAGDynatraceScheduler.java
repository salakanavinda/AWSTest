/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.scheduler;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihub.asm360.adaptor.app.Constants;
import com.ihub.asm360.adaptor.core.dto.ASMAlert;
import com.ihub.asm360.adaptor.core.response.Application;
import com.ihub.asm360.adaptor.core.response.dynatrace.DynatraceAffectedEntity;
import com.ihub.asm360.adaptor.core.response.dynatrace.DynatraceProblem;
import com.ihub.asm360.adaptor.core.response.dynatrace.DynatraceResponse;
import com.ihub.asm360.adaptor.core.scheduler.BaseScheduler;
import com.ihub.asm360.adaptor.core.service.GlobalPropertyService;

/**
 * The Class EAGDynatraceScheduler.
 */
@Component
public class EAGDynatraceScheduler extends BaseScheduler<DynatraceResponse> {

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

	/** The object mapper. */
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Instantiates a new EAG dynatrace scheduler.
	 */
	public EAGDynatraceScheduler() {
		super(Constants.Profile.EAGDynatrace.PROFILE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ihub.asm360.adaptor.core.scheduler.BaseScheduler#convert(com.ihub.asm360.adaptor.core.response.BaseResponse)
	 */
	@Override
	public List<ASMAlert> convert(String text) throws IOException {
		DynatraceResponse response = objectMapper.readValue(text, new TypeReference<DynatraceResponse>() {
		});

		List<DynatraceProblem> problemList = response.getResult().getProblems();

		String incidentUrl = globalPropertyService.getPropertyByKey(Constants.Profile.EAGDynatrace.PROFILE_NAME,
				Constants.Profile.EAGDynatrace.INCIDENT_DETAIL_URL).getValue();

		List<ASMAlert> alertList = new ArrayList<>();

		for (DynatraceProblem problem : problemList) {
			// TODO Check the attribute for app name
			List<Application> applications = getAppDetailsByName(problem.getId());

			for (Application application : applications) {

				ASMAlert alert = new ASMAlert();
				alert.setEventType("ALERT_EVENT");
				alert.setSubsystem("sys1");
				alert.setStatus(problem.getStatus());
				alert.setCaseType("Reactive");
				alert.setAlertTickerMsg(problem.getDisplayName());

				alert.setCasePriority(problem.getSeverityLevel());
				alert.setCaseRef(problem.getId());
				alert.setIncType("Reactive");
				alert.setCaseTitle(problem.getDisplayName());

				List<DynatraceAffectedEntity> entities = problem.getTagsOfAffectedEntities();

				for (DynatraceAffectedEntity entity : entities) {
					String type = entity.getContext();

					if (TYPE_APPLICATION.equals(type) || TYPE_INFRASTRUCTURE.equals(type)) {
						alert.setAlertType(TYPE_INFRA_APP);
					} else {
						alert.setAlertType(TYPE_FUNCTIONAL_MONITORING);
					}
				}

				alert.setCaseRef(problem.getId() + "-" + application.getProgram().getProgramName());
				alert.setCellId(application.getSalsaAppId());
				alert.setCellName(application.getSalsaName());
				alert.setCellParent(application.getProgram().getProgramName());
				alert.setLob(application.getProgram().getProgramName());
				alert.setUuid(application.getUuid());
				alert.setUrlPath(incidentUrl + problem.getId());

				alertList.add(alert);
			}

		}
		return alertList;
	}

	/**
	 * Schedule.
	 */
	// @Scheduled(cron = "0 0 */1 * * *")
	public void schedule() {

		String isPullActive = globalPropertyService.getPropertyByKey(Constants.Profile.EAGDynatrace.PROFILE_NAME,
				Constants.Profile.EAGDynatrace.IS_PULL_ACTIVE).getValue();

		if (Constants.Strings.YES.equalsIgnoreCase(isPullActive)) {
			String apiToken = globalPropertyService.getPropertyByKey(Constants.Profile.EAGDynatrace.PROFILE_NAME,
					Constants.Profile.EAGDynatrace.API_TOKEN).getValue();
			String pullUrl = globalPropertyService.getPropertyByKey(Constants.Profile.EAGDynatrace.PROFILE_NAME,
					Constants.Profile.EAGDynatrace.INCIDENT_PULL_URL).getValue();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.add("Authorization", apiToken);

			try {
				pull(headers, pullUrl);
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
