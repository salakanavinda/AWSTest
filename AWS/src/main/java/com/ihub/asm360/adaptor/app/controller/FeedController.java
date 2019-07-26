/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.RateLimiter;
import com.ihub.asm360.adaptor.app.Constants;
import com.ihub.asm360.adaptor.app.handler.HandlerRegistry;
import com.ihub.asm360.adaptor.core.entity.Profile;
import com.ihub.asm360.adaptor.core.handler.BaseHandler;
import com.ihub.asm360.adaptor.core.request.IncidentRequest;
import com.ihub.asm360.adaptor.core.service.ProfileService;

/**
 * The Class FeedController.
 */

@RestController
@RequestMapping("/feed")
public class FeedController {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

	/** The profile service. */
	@Autowired
	private ProfileService profileService;

	/** The handler registry. */
	@Autowired
	private HandlerRegistry handlerRegistry;

	/** The incident rate limiter. */
	// allowing 10 requests per second
	private RateLimiter incidentRateLimiter = RateLimiter.create(10.0);

	/**
	 * Feed V 1.
	 *
	 * @param apiToken the api token
	 * @param request the request
	 * @return the response entity
	 */
	@PostMapping(value = "/incident", headers = Constants.ApiVersion.V1)
	public ResponseEntity<Void> feedV1(@RequestHeader(value = Constants.RequestHeader.API_TOKEN) String apiToken,
			@RequestBody IncidentRequest request) {

		incidentRateLimiter.acquire();

		HttpStatus httpStatus = HttpStatus.OK;

		LOGGER.debug("Received incident for feedV1, {}", request);

		Profile profile = profileService.getByToken(apiToken);

		if (profile != null) {

			if (profile.isEnabled()) {

				LOGGER.debug("Found the profile, {}", profile);

				String name = profile.getName();

				BaseHandler handler = handlerRegistry.getHandler(name);

				if (handler != null) {

					handler.handle(request);

				} else {
					LOGGER.debug("No handler registed for profile, {}", name);

					httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				}

			} else {

				LOGGER.debug("Profile is disabled, {}", profile);

				httpStatus = HttpStatus.UNAUTHORIZED;
			}
		} else {
			LOGGER.debug("No profile for the token, {}", apiToken);
			httpStatus = HttpStatus.UNAUTHORIZED;
		}

		return new ResponseEntity<Void>(httpStatus);
	}
}
