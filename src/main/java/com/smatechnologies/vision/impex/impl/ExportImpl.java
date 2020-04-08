package com.smatechnologies.vision.impex.impl;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import javax.ws.rs.client.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.smatechnologies.opcon.restapiclient.WsException;
import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.OpconApiProfile;
import com.smatechnologies.opcon.restapiclient.jackson.DefaultObjectMapperProvider;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionCard;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionWorkspace;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.interfaces.IMasterVisionWorkspace;
import com.smatechnologies.vision.impex.interfaces.IVisionActions;
import com.smatechnologies.vision.impex.interfaces.IExport;
import com.smatechnologies.vision.impex.interfaces.IVisionFrequency;
import com.smatechnologies.vision.impex.modules.VisionDefinitions;
import com.smatechnologies.vision.impex.util.Utilities;
import com.smatechnologies.vision.impex.ws.WsClientBuilder;

public class ExportImpl implements IExport {
	
	private static final String DebugUrlMsg =       "Connecting to API ({0})";
	private static final String DeployFileNameMsg = "Deploy File ({0}) created";
	
	private static final String UrlFormat = "{0}/api";
	private static final String COMMA = ",";
	private SimpleDateFormat exportDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

	
	private final static Logger LOG = LoggerFactory.getLogger(ExportImpl.class);
	private Utilities _Utilities = new Utilities();
	private DefaultObjectMapperProvider _DefaultObjectMapperProvider = new DefaultObjectMapperProvider();

	private IVisionFrequency _IVisionFrequency = new VisionFrequencyImpl();
	private IVisionActions _IVisionActions = new VisionActionsImpl();
	private IMasterVisionWorkspace _IMasterVisionWorkspace = new MasterVisionWorkspaceImpl();
	
	private Hashtable<String, String> htblGroupsToExtract = new Hashtable<String, String>();
	

	public boolean processRequest(
			VisionImpexArguments _VisionImpexArguments
			) throws Exception {
		
		boolean success = false;
		String url = MessageFormat.format(UrlFormat,_VisionImpexArguments.getOpConSystemUrl());
		htblGroupsToExtract.clear();
		if(_VisionImpexArguments.getGroupNamesToExtract() != null) {
			String[] groups = _Utilities.tokenizeParameters(_VisionImpexArguments.getGroupNamesToExtract(), false, COMMA);
			for(String group : groups) {
				String key = group.replaceAll("[/:.*?\\s]", "_");
				String existingKey = htblGroupsToExtract.get(key);
				if(existingKey == null) {
					LOG.debug("Adding group (" + group + ") as key (" + key + ")" );
					htblGroupsToExtract.put(key,  group);
				}
			}
		}
		// create client connection
		LOG.debug(MessageFormat.format(DebugUrlMsg, url));

		OpconApiProfile profile = new OpconApiProfile(url);
		OpconApi opconApi = getClient(_VisionImpexArguments, profile);
		
		
		try {
			VisionDefinitions visionDefinitions = new VisionDefinitions();
			visionDefinitions.setFrequencies(_IVisionFrequency.getFrequencies(opconApi, _VisionImpexArguments));
			visionDefinitions.setActions(_IVisionActions.getActions(opconApi, _VisionImpexArguments));
			List<MasterVisionWorkspace> workspaces = _IMasterVisionWorkspace.getWorkspaces(opconApi, _VisionImpexArguments);
			if(_VisionImpexArguments.getGroupNamesToExtract() == null) {
				visionDefinitions.setWorkspaces(workspaces);
			} else {
				List<MasterVisionWorkspace> updatedWorkspaces = new ArrayList<MasterVisionWorkspace>();
				List<MasterVisionCard> updatedMcards = new ArrayList<MasterVisionCard>();
				for (MasterVisionWorkspace workspace : workspaces) {
					for(MasterVisionCard mcard : workspace.getMasterVisionCards()) {
						String groupKey = mcard.getName().replaceAll("[/:.*?\\s]", "_");
						LOG.debug("group to check (" + groupKey + ")");
						String exists = htblGroupsToExtract.get(groupKey);
						if(exists != null) {
							// we need to add this group
							updatedMcards.add(mcard);
						}
					}
					workspace.setMasterVisionCards(updatedMcards);
					updatedWorkspaces.add(workspace);
				}
				visionDefinitions.setWorkspaces(updatedWorkspaces);
			}
			LOG.debug("visionDefinitions " + _DefaultObjectMapperProvider.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(visionDefinitions));
			String deployFileName =  writeFile(visionDefinitions, _VisionImpexArguments.getOutputDirectory());
			LOG.info(MessageFormat.format(DeployFileNameMsg, deployFileName));
			success = true;
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return success;
	}
	
	private OpconApi getClient(
			VisionImpexArguments _VisionDeployArguments,
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
			opconApi.login(_VisionDeployArguments.getUserName(), _VisionDeployArguments.getPassword());
			
		} catch (KeyManagementException | NoSuchAlgorithmException | WsException e) {
		    throw new Exception(e);
		}
		return opconApi;
	}	// END : getClient

	private String writeFile(
			VisionDefinitions visionDefinitions, 
			String outputDirectory
			) throws Exception {

		String fileName = null;
		
		try  {
			GregorianCalendar gc = new GregorianCalendar();
			String fileDateTimeStamp = exportDateFormat.format(gc.getTimeInMillis());
			fileName = outputDirectory + File.separator + "VISION_EXTRACT_" + fileDateTimeStamp + ".json";
			ObjectWriter _ObjectWriter = _DefaultObjectMapperProvider.getObjectMapper().writer(new DefaultPrettyPrinter());
			_ObjectWriter.writeValue(new File(fileName), visionDefinitions);
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		return fileName;
	}

}
