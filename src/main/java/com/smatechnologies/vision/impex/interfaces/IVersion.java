package com.smatechnologies.vision.impex.interfaces;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;

public interface IVersion {
	
	public boolean getVersion(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments) throws Exception;

}
