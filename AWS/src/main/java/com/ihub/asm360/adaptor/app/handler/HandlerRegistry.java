/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.handler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ihub.asm360.adaptor.app.Constants;
import com.ihub.asm360.adaptor.core.handler.BaseHandler;

/**
 * The Class HandlerRegistry.
 */
@Service
public class HandlerRegistry {

	/** The register map. */
	private Map<String, BaseHandler> registerMap = new HashMap<>();

	@Autowired
	private EAGDynatraceHandler eagDynatraceHandler;
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		registerMap.put(Constants.Profile.EAGDynatrace.PROFILE_NAME, eagDynatraceHandler);
	}

	/**
	 * Gets the handler.
	 *
	 * @param name the name
	 * @return the handler
	 */
	public BaseHandler getHandler(String name) {
		return registerMap.get(name);
	}

}
