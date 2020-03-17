package com.smatechnologies.vision.impex.impl;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.opcon.restapiclient.WsException;
import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.OpconApiProfile;
import com.smatechnologies.opcon.restapiclient.jackson.DefaultObjectMapperProvider;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.interfaces.IMasterVisionWorkspace;
import com.smatechnologies.vision.impex.interfaces.IVisionActions;
import com.smatechnologies.vision.impex.interfaces.IVisionFrequency;
import com.smatechnologies.vision.impex.interfaces.IImport;
import com.smatechnologies.vision.impex.modules.VisionDefinitions;
import com.smatechnologies.vision.impex.util.Utilities;
import com.smatechnologies.vision.impex.ws.WsClientBuilder;

public class ImportImpl implements IImport {

	private static final String DebugUrlMsg =       "DEBUG : Connecting to API ({0})";
	
	private static final String UrlFormat = "{0}/api";

	
	private final static Logger LOG = LoggerFactory.getLogger(ExportImpl.class);
	private DefaultObjectMapperProvider _DefaultObjectMapperProvider = new DefaultObjectMapperProvider();
	@SuppressWarnings("unused")
	private Utilities _Utilities = new Utilities();

	private IVisionActions _IVisionActions = new VisionActionsImpl();
	private IVisionFrequency _IVisionFrequency = new VisionFrequencyImpl();
	private IMasterVisionWorkspace _IMasterVisionWorkspace = new MasterVisionWorkspaceImpl();
	
	public boolean processRequest(
			VisionImpexArguments _VisionImpexArguments
			) throws Exception {
		
		boolean result = false;

		try {
			// create client connection
			String url = MessageFormat.format(UrlFormat,_VisionImpexArguments.getOpConSystemUrl());
			if(_VisionImpexArguments.isDebug()) {
				LOG.info(MessageFormat.format(DebugUrlMsg, url));
			}
	
			OpconApiProfile profile = new OpconApiProfile(url);
			OpconApi opconApi = getClient(_VisionImpexArguments, profile);
			// get import data
			VisionDefinitions visionDefinitions = readFile(_VisionImpexArguments);
			if(_VisionImpexArguments.isDebug()) {
				String jsonData = _DefaultObjectMapperProvider.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(visionDefinitions);
				LOG.info("DEBUG : definitions to be imported (" + jsonData + ")");
			}
			
			// update actions
			boolean importActionsResult = _IVisionActions.setActions(opconApi, _VisionImpexArguments, visionDefinitions.getActions());
			if(!importActionsResult) {
				return false;
			}
			// update frequencies
			boolean importFrequenciesResult = _IVisionFrequency.setFrequencies(opconApi, _VisionImpexArguments, visionDefinitions.getFrequencies());
			if(!importFrequenciesResult) {
				return false;
			}
			// update workspaces
			boolean importWorkspacesResult = _IMasterVisionWorkspace.setWorkspaces(opconApi, _VisionImpexArguments, visionDefinitions.getWorkspaces());
			if(!importWorkspacesResult) {
				return false;
			}
			result = true;
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return result;
	}
	
	private OpconApi getClient(
			VisionImpexArguments _VisionImpexArguments,
			OpconApiProfile profile
			) throws Exception {
		
		OpconApi opconApi;
				
		try {
			Client client = WsClientBuilder.get()
		            .configureDefaultClientBuilder(defaultClientBuilder -> defaultClientBuilder.setTrustAllCert(true))
		            .setDebug(true)
		            .setFailOnUnknownProperties(true)
		            .build();

			opconApi = new OpconApi(client, profile, new OpconApi.OpconApiListener() {

		        @Override
		        public void onFailed(WsException e) {
		            e.printStackTrace();
		        }
		    });
			opconApi.login(_VisionImpexArguments.getUserName(), _VisionImpexArguments.getPassword());
			
		} catch (KeyManagementException | NoSuchAlgorithmException | WsException e) {
		    throw new Exception(e);
		}
		return opconApi;
	}	// END : getClient

	private VisionDefinitions readFile(
			VisionImpexArguments _VisionDeployArguments 
			) throws Exception {

		VisionDefinitions visionDefinitions = new VisionDefinitions();
		String fileName = null;
		
		try  {
			fileName = _VisionDeployArguments.getInputDirectory() + File.separator + _VisionDeployArguments.getFileName();
			visionDefinitions =	_DefaultObjectMapperProvider.getObjectMapper().readValue(new FileInputStream(fileName), VisionDefinitions .class);
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return visionDefinitions;
	}

	
	
}
