package com.smatechnologies.vision.impex.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.visionfrequencies.VisionFrequenciesCriteria;
import com.smatechnologies.opcon.restapiclient.jackson.DefaultObjectMapperProvider;
import com.smatechnologies.opcon.restapiclient.model.VisionFrequency;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.interfaces.IVisionFrequency;

public class VisionFrequencyImpl implements IVisionFrequency {

	private static final String RetrievingVisionFrequenciesMsg =       "Retrieving Vision Frequencies";
	private static final String ImportingVisionFrequenciesMsg =        "Importing Vision Frequencies";
	private static final String NoVisionFrequenciesToImportMsg =       "No Vision Frequencies to import";

	private final static Logger LOG = LoggerFactory.getLogger(VisionFrequencyImpl.class);
	private DefaultObjectMapperProvider _DefaultObjectMapperProvider = new DefaultObjectMapperProvider();
	
	private Hashtable<String, VisionFrequency> htblExistingFrequencies = new Hashtable<String, VisionFrequency>();


	public List<VisionFrequency> getFrequencies(
			OpconApi opconApi,
			VisionImpexArguments _VisionImpexArguments
			) throws Exception {
		
		LOG.info(RetrievingVisionFrequenciesMsg);
		VisionFrequenciesCriteria criteria = new VisionFrequenciesCriteria();
		List<VisionFrequency> visionFrequencies = opconApi.visionFrequencies().get(criteria);
		if(_VisionImpexArguments.isDebug()) {
			for(VisionFrequency vfreq : visionFrequencies) { 
				LOG.info("frequency (" + vfreq.getName() + ") extracted");
			}
		}
		return visionFrequencies;
	}

	public boolean setFrequencies(
			OpconApi opconApi, 
			VisionImpexArguments _VisionImpexArguments, 
			List<VisionFrequency> importFrequencies
			) throws Exception {
		
		boolean result = false;
		
		LOG.info(ImportingVisionFrequenciesMsg);
		if(!importFrequencies.isEmpty()) {
			htblExistingFrequencies.clear();
			// get existing frequency
			List<VisionFrequency> frequencies = getFrequencies(opconApi, _VisionImpexArguments);
			for(VisionFrequency frequency : frequencies) {
				if(!frequency.getName().equalsIgnoreCase("All Days")) {
					String frequencyKey = frequency.getName().replaceAll("[/:.*?\\s]", "_");
					VisionFrequency existingFrequency = htblExistingFrequencies.get(frequencyKey);
					if(existingFrequency == null) {
						LOG.debug("Adding frequency (" + frequency.getName() + ") as key (" + frequencyKey + ")" );
						htblExistingFrequencies.put(frequencyKey,  frequency);
					}
				}
			}
			List<VisionFrequency> additionalFrequencies = new ArrayList<VisionFrequency>();
			for(VisionFrequency importFrequency : importFrequencies) {
				if(!importFrequency.getName().equalsIgnoreCase("All Days")) {
					String importFrequencyKey = importFrequency.getName().replaceAll("[/:.*?\\s]", "_");
					VisionFrequency existingFrequency = htblExistingFrequencies.get(importFrequencyKey);
					if(existingFrequency != null) {
						htblExistingFrequencies.put(importFrequencyKey,  importFrequency);
					} else {
						additionalFrequencies.add(importFrequency);
					}
				}
			}
			// update existing frequencies
			Set<String> keys = htblExistingFrequencies.keySet();
			for(String key : keys) {
				VisionFrequency frequency = htblExistingFrequencies.get(key);
				LOG.debug("Updating frequency (" + frequency.getName() + ")" );
				LOG.debug("frequency " + _DefaultObjectMapperProvider.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(frequency));
				opconApi.visionFrequencies().put(frequency);
			}
			// add additional actions
			for(VisionFrequency frequency : additionalFrequencies) {
				LOG.debug("adding frequency (" + frequency.getName() + ")" );
				LOG.debug("frequency " + _DefaultObjectMapperProvider.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(frequency));
				opconApi.visionFrequencies().post(frequency);
			}
			result = true;
		} else {
			LOG.info(NoVisionFrequenciesToImportMsg);
			result = true;
		}
		return result;
	}
	
	
	
	
}
