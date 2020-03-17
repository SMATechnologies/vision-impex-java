package com.smatechnologies.vision.impex.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.mastervisionworkspaces.MasterVisionWorkspaceCriteria;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionCard;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionGroupCard;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionTagCard;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionWorkspace;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.interfaces.IMasterVisionWorkspace;

public class MasterVisionWorkspaceImpl implements IMasterVisionWorkspace {
	
	private static final String RetrievingVisionWorkspacesMsg =       "Retrieving Vision Workspaces";
	private static final String ImportingVisionWorkspacesMsg =        "Importing Vision Workspaces";
	private static final String NoVisionWorkspacesToImportMsg =       "No Vision Workspaces to import";

	private final static Logger LOG = LoggerFactory.getLogger(MasterVisionWorkspaceImpl.class);

	private ObjectMapper _ObjectMapper = new ObjectMapper();

	private Hashtable<String, MasterVisionWorkspace> htblExistingWorkspaces = new Hashtable<String, MasterVisionWorkspace>();

	public List<MasterVisionWorkspace> getWorkspaces(
			OpconApi opconApi,
			VisionImpexArguments _VisionImpexArguments
			) throws Exception {
		
		LOG.info(RetrievingVisionWorkspacesMsg);
		MasterVisionWorkspaceCriteria criteria = new MasterVisionWorkspaceCriteria();
		List<MasterVisionWorkspace> visionWorkspaces = opconApi.masterVisionWorkspaces().get(criteria);
		if(_VisionImpexArguments.isDebug()) {
			for(MasterVisionWorkspace visionWorkspace : visionWorkspaces) { 
				LOG.info("DEBUG : workspace (" + visionWorkspace.getName() + ")");
				for(MasterVisionGroupCard vgroupcard : visionWorkspace.getMasterVisionGroupCards()) { 
					LOG.info("DEBUG : vgroupcard (" + vgroupcard.getName() + ")");
					List<MasterVisionTagCard> vgrouptags = vgroupcard.getTagChildren();
					for(MasterVisionTagCard vtagcard : vgrouptags) { 
						LOG.info("DEBUG : vtagcard   (" + vtagcard.getName() + ")");
					}
				}
				for(MasterVisionCard vcard : visionWorkspace.getMasterVisionCards()) { 
					LOG.info("DEBUG : vcard      (" + vcard.getName() + ")");
				}
				for(MasterVisionTagCard vtagcard : visionWorkspace.getMasterVisionTagCards()) { 
					LOG.info("DEBUG : vtagcard   (" + vtagcard.getName() + ")");
				}
			}
		}
		return visionWorkspaces;
	}

	public boolean setWorkspaces(
			OpconApi opconApi, 
			VisionImpexArguments _VisionImpexArguments,
			List<MasterVisionWorkspace> importWorkspaces
			) throws Exception {
		
		boolean result = false;
		
		LOG.info(ImportingVisionWorkspacesMsg);
		if(!importWorkspaces.isEmpty()) {
			htblExistingWorkspaces.clear();
			// get existing workspaces
			List<MasterVisionWorkspace> workspaces = getWorkspaces(opconApi, _VisionImpexArguments);
			for(MasterVisionWorkspace workspace : workspaces) {
				String workspaceKey = workspace.getName().replaceAll("[/:.*?\\s]", "_");
				MasterVisionWorkspace existingWorkspace = htblExistingWorkspaces.get(workspaceKey);
				if(existingWorkspace == null) {
					if(_VisionImpexArguments.isDebug()) {
						LOG.info("DEBUG : Adding workspace (" + workspace.getName() + ") as key (" + workspaceKey + ")" );
					}
					htblExistingWorkspaces.put(workspaceKey,  workspace);
				}
			}
			List<MasterVisionWorkspace> additionalWorkspaces = new ArrayList<MasterVisionWorkspace>();
			for(MasterVisionWorkspace importWorkspace : importWorkspaces) {
				String importWorkspaceKey = importWorkspace.getName().replaceAll("[/:.*?\\s]", "_");
				MasterVisionWorkspace existingWorkspace = htblExistingWorkspaces.get(importWorkspaceKey);
				if(existingWorkspace != null) {
					MasterVisionWorkspace mergedWorkspace = mergeWorkspace(_VisionImpexArguments, existingWorkspace, importWorkspace);
					htblExistingWorkspaces.put(importWorkspaceKey,  mergedWorkspace);
				} else {
					additionalWorkspaces.add(importWorkspace);
				}
			}
			// update existing workspaces
			Set<String> keys = htblExistingWorkspaces.keySet();
			for(String key : keys) {
				MasterVisionWorkspace workspace = htblExistingWorkspaces.get(key);
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : Updating workspace (" + workspace.getName() + ")" );
				}
				String jsonData = _ObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(workspace);
				LOG.info("DEBUG : merged workspace " + jsonData);
				opconApi.masterVisionWorkspaces().post(workspace);
			}
			// add additional workspaces
			for(MasterVisionWorkspace workspace : additionalWorkspaces) {
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : adding workspace (" + workspace.getName() + ")" );
				}
				String jsonData = _ObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(workspace);
				LOG.info("DEBUG : new workspace " + jsonData);
				opconApi.masterVisionWorkspaces().post(workspace);
			}
			result = true;
		} else {
			LOG.info(NoVisionWorkspacesToImportMsg);
			result = true;
		}
		return result;
	}

	private MasterVisionWorkspace mergeWorkspace(
			VisionImpexArguments _VisionImpexArguments,
			MasterVisionWorkspace existing, 
			MasterVisionWorkspace update
			) throws Exception {
		
		Hashtable<String, MasterVisionCard> htblExistingCards = new Hashtable<String, MasterVisionCard>();
		
		htblExistingCards.clear();
		for(MasterVisionCard masterVisionCard : existing.getMasterVisionCards()) {
			String cardKey = masterVisionCard.getName().replaceAll("[/:.*?\\s]", "_");
			MasterVisionCard existingCard = htblExistingCards.get(cardKey);
			if(existingCard == null) {
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : Adding existing card (" + masterVisionCard.getName() + ") as key (" + cardKey + ")" );
				}
				htblExistingCards.put(cardKey, masterVisionCard);
			}
		}
		for(MasterVisionCard masterVisionCard : update.getMasterVisionCards()) {
			String cardKey = masterVisionCard.getName().replaceAll("[/:.*?\\s]", "_");
			MasterVisionCard updateCard = htblExistingCards.get(cardKey);
			if(updateCard == null) {
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : Merge : Adding card (" + masterVisionCard.getName() + ") as key (" + cardKey + ")" );
				}
				htblExistingCards.put(cardKey, masterVisionCard);
			} else {
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : Merge : replacing card (" + masterVisionCard.getName() + ") as key (" + cardKey + ")" );
				}
				htblExistingCards.put(cardKey, masterVisionCard);
			}
		}
		List<MasterVisionCard> updatedList = new ArrayList<MasterVisionCard>();
		Set<String> keys = htblExistingCards.keySet();
		for(String key : keys) {
			MasterVisionCard card = htblExistingCards.get(key);
			if(_VisionImpexArguments.isDebug()) {
				LOG.info("DEBUG : Merge adding (" + card.getName() + ") to updated list" );
			}
			updatedList.add(card);
		}
		existing.setMasterVisionCards(updatedList);
		return existing;
	}
	
}
