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
 import com.ihub.asm360.adaptor.app.AWSResponse.AwsResponse;
 import com.ihub.asm360.adaptor.core.response.servicenow.ServiceNowResult;
 import com.ihub.asm360.adaptor.core.scheduler.BaseScheduler;
 import com.ihub.asm360.adaptor.core.service.AlertService;
 import com.ihub.asm360.adaptor.core.service.DataMapperService;
 import com.ihub.asm360.adaptor.core.service.GlobalPropertyService;
 import com.ihub.asm360.adaptor.core.service.PayloadService;
/*
 * imports for aws clould watch
 */
 
 import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
 import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
 import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
 import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
 import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
 import java.util.Date;
 /*
 * The Class AWSServiceScheduler.
 */
@Component
@EnableScheduling
public class AWSServiceNowScheduler extends BaseScheduler<AwsResponse>{
	
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
	 * Instantiates a new AwsServiceNowscheduler.
	 */
	public AWSServiceNowScheduler() {
		super(Constants.Profile.AwsServiceNow.PROFILE_NAME);
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
		/*
		 * Add default Client for AmazonCloudWatch variable
		 */
		final AmazonCloudWatch cw =
			    AmazonCloudWatchClientBuilder.defaultClient();
		
        
	}
	
	/*
	 * method for getting datpoints for a specific intances
	 */
    public static void MonitoringInstance(AmazonCloudWatch client,String InstanceID,long offsetInMilliseconds,
			   Integer period,String metricName) {
		   
		   GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				   .withStartTime(new Date(new Date().getTime() - offsetInMilliseconds))
				   .withNamespace("AWS/EC2")
				   .withPeriod(period)
				   .withDimensions(new Dimension().withName("InstanceId").withValue(InstanceID))
				   .withMetricName(metricName)
				   .withStatistics("Average", "Maximum","Minimum","SampleCount","Sum")
				   .withEndTime(new Date());
		   
		   GetMetricStatisticsResult getMetricStatisticsResult = client.getMetricStatistics(request);
		   
		   System.out.println(getMetricStatisticsResult.getDatapoints());
		   
	}
	
	
    

}
