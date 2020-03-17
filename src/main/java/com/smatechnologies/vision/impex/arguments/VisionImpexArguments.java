package com.smatechnologies.vision.impex.arguments;

import com.beust.jcommander.Parameter;

public class VisionImpexArguments {

	public static final String OpConSystemUrlArgumentDescriptionMsg = "The url of the OpCon System (https://<name>:<port> or http://<name>:<port>)";
	public static final String OpConUserNameArgumentDescriptionMsg = "The name of the OpCon user that has OpCon-API privileges";
	public static final String OpConUserPasswordArgumentDescriptionMsg = "The password of the OpCon user that has OpCon-API privileges";
	public static final String InputDirectoryArgumentDescriptionMsg = "The name of the directory that contains the file information to import";
	public static final String OutputDirectoryArgumentDescriptionMsg = "The name of the directory where the exported file information will be saved";
	public static final String GroupNamesToExportArgumentDescriptionMsg = "The name(s) Groups to extract (multiple names should be comma seperated - default is all)";
	public static final String FileNameArgumentDescriptionMsg = "The name of the file containing the information to import";
	public static final String DebugArgumentDescriptionMsg = "Set program into debug mode)";


	@Parameter(names="-url", required=true, description = OpConSystemUrlArgumentDescriptionMsg)
	private String opConSystemUrl = null;
	
	@Parameter(names="-u", required=true, description = OpConUserNameArgumentDescriptionMsg)
	private String userName = null;

	@Parameter(names="-p", required=true, description = OpConUserPasswordArgumentDescriptionMsg)
	private String password = null;

	@Parameter(names="-idir", description = InputDirectoryArgumentDescriptionMsg)
	private String inputDirectory = null;
	
	@Parameter(names="-odir", description = OutputDirectoryArgumentDescriptionMsg)
	private String outputDirectory = null;

	@Parameter(names="-gn", description = GroupNamesToExportArgumentDescriptionMsg)
	private String groupNamesToExtract = null;

	@Parameter(names="-f", description = FileNameArgumentDescriptionMsg)
	private String fileName = null;

	@Parameter(names="-debug", description = DebugArgumentDescriptionMsg)
	private boolean debug = false;

	public String getOpConSystemUrl() {
		return opConSystemUrl;
	}

	public void setOpConSystemUrl(String opConSystemUrl) {
		this.opConSystemUrl = opConSystemUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getGroupNamesToExtract() {
		return groupNamesToExtract;
	}

	public void setGroupNamesToExtract(String groupNamesToExtract) {
		this.groupNamesToExtract = groupNamesToExtract;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
