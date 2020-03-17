package com.smatechnologies.vision.impex.interfaces;

import java.util.List;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.model.VisionFrequency;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;

public interface IVisionFrequency {
	
	public List<VisionFrequency> getFrequencies(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments) throws Exception;
	public boolean setFrequencies(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments, List<VisionFrequency> importFrequencies) throws Exception;

}
