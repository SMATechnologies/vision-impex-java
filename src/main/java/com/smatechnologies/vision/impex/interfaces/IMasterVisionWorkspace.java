package com.smatechnologies.vision.impex.interfaces;

import java.util.List;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.model.MasterVisionWorkspace;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;

public interface IMasterVisionWorkspace {

	public List<MasterVisionWorkspace> getWorkspaces(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments) throws Exception;
	public boolean setWorkspaces(OpconApi opconApi, VisionImpexArguments _VisionImpexArguments, List<MasterVisionWorkspace> importWorkspaces) throws Exception;
	
}
