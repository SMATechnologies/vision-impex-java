package com.smatechnologies.vision.impex.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.visionactions.VisionActionsCriteria;
import com.smatechnologies.opcon.restapiclient.model.VisionAction;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.interfaces.IVisionActions;

public class VisionActionsImpl implements IVisionActions {

	private static final String RetrievingVisionActionsMsg =       "Retrieving Vision Actions";
	private static final String ImportingVisionActionsMsg =        "Importing Vision Actions";
	private static final String NoVisionActionsToImportMsg =       "No Vision Actions to import";

	private final static Logger LOG = LoggerFactory.getLogger(VisionActionsImpl.class);

	private Hashtable<String, VisionAction> htblExistingActions = new Hashtable<String, VisionAction>();

	public List<VisionAction> getActions(
			OpconApi opconApi,
			VisionImpexArguments _VisionImpexArguments
			) throws Exception {
		
		LOG.info(RetrievingVisionActionsMsg);
		VisionActionsCriteria criteria = new VisionActionsCriteria();
		List<VisionAction> visionActions = opconApi.visionActions().get(criteria);
		if(_VisionImpexArguments.isDebug()) {
			for(VisionAction vact : visionActions) { 
				LOG.info("action (" + vact.getName() + ") extracted");
			}
		}
		return visionActions;
	}

	public boolean setActions(
			OpconApi opconApi,
			VisionImpexArguments _VisionImpexArguments,
			List<VisionAction> importActions
			) throws Exception {

		boolean result = false;
		
		LOG.info(ImportingVisionActionsMsg);
		if(!importActions.isEmpty()) {
			htblExistingActions.clear();
			// get existing actions
			List<VisionAction> actions = getActions(opconApi, _VisionImpexArguments);
			for(VisionAction action : actions) {
				String actionKey = action.getName().replaceAll("[/:.*?\\s]", "_");
				VisionAction existingAction = htblExistingActions.get(actionKey);
				if(existingAction == null) {
					if(_VisionImpexArguments.isDebug()) {
						LOG.info("DEBUG : Adding action (" + action.getName() + ") as key (" + actionKey + ")" );
					}
					htblExistingActions.put(actionKey,  action);
				}
			}
			List<VisionAction> additionalActions 	= new ArrayList<VisionAction>();
			for(VisionAction importAction : importActions) {
				String importActionKey = importAction.getName().replaceAll("[/:.*?\\s]", "_");
				VisionAction existingAction = htblExistingActions.get(importActionKey);
				if(existingAction != null) {
					htblExistingActions.put(importActionKey,  importAction);
				} else {
					additionalActions.add(importAction);
				}
			}
			// update existing actions
			Set<String> keys = htblExistingActions.keySet();
			for(String key : keys) {
				VisionAction action = htblExistingActions.get(key);
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : Updating action (" + action.getName() + ")" );
				}
				opconApi.visionActions().put(action);
			}
			// add additional actions
			for(VisionAction action : additionalActions) {
				if(_VisionImpexArguments.isDebug()) {
					LOG.info("DEBUG : adding action (" + action.getName() + ")" );
				}
				opconApi.visionActions().post(action);
			}
			result = true;
		} else {
			LOG.info(NoVisionActionsToImportMsg);
			result = true;
		}
		return result;
	}

}
