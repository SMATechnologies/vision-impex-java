package com.smatechnologies.vision.impex.interfaces;

import java.util.List;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.model.VisionAction;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;

public interface IVisionActions {

	public List<VisionAction> getActions(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments) throws Exception;
	public boolean setActions(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments, List<VisionAction> importActions) throws Exception;
	
}
