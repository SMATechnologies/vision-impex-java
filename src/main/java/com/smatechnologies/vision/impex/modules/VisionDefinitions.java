package com.smatechnologies.vision.impex.modules;

import java.util.ArrayList;
import java.util.List;

import com.smatechnologies.opcon.restapiclient.model.MasterVisionWorkspace;
import com.smatechnologies.opcon.restapiclient.model.VisionAction;
import com.smatechnologies.opcon.restapiclient.model.VisionFrequency;

public class VisionDefinitions {

	private List<VisionAction> actions = new ArrayList<VisionAction>();
	private List<VisionFrequency> frequencies = new ArrayList<VisionFrequency>();
	private List<MasterVisionWorkspace> workspaces = new ArrayList<MasterVisionWorkspace>();
	
	public List<VisionAction> getActions() {
		return actions;
	}
	
	public void setActions(List<VisionAction> actions) {
		this.actions = actions;
	}
	
	public List<VisionFrequency> getFrequencies() {
		return frequencies;
	}
	
	public void setFrequencies(List<VisionFrequency> frequencies) {
		this.frequencies = frequencies;
	}
	
	public List<MasterVisionWorkspace> getWorkspaces() {
		return workspaces;
	}
	
	public void setWorkspaces(List<MasterVisionWorkspace> workspaces) {
		this.workspaces = workspaces;
	}
	
}
