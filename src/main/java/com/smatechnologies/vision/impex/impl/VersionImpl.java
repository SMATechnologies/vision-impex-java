package com.smatechnologies.vision.impex.impl;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.model.Version;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.interfaces.IVersion;

public class VersionImpl implements IVersion {
	
	private static final String OpConAPIVersionMsg =       "OpCon-API Version {0}";

	private final static Logger LOG = LoggerFactory.getLogger(VersionImpl.class);

	public boolean getVersion(
			OpconApi opconApi,
			VisionImpexArguments _VisionImpexArguments
			) throws Exception {
		
		boolean success = false;
		
		Version version = opconApi.getVersion();
		LOG.info(MessageFormat.format(OpConAPIVersionMsg, version.getOpConRestApiProductVersion()));
		success = true;
		return success;
	}

}
