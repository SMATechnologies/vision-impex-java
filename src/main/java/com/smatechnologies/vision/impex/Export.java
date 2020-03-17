package com.smatechnologies.vision.impex;

import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.impl.ExportImpl;
import com.smatechnologies.vision.impex.interfaces.IVisionImpexConstants;
import com.smatechnologies.vision.impex.interfaces.IExport;
import com.smatechnologies.vision.impex.util.Utilities;

public class Export {

	private static final String SeperatorLineMsg =                    "---------------------------------------------------------------------";
	private static final String MissingOutputDirectoryMsg =           "Output directory argument (-odir) missing for Export function";
	
	private static final String ProgramNameAndVersionMsg =            "Export                         : Version {0}";
	private static final String DisplayOpConUrlArgumentMsg =          "-url  (OpCon System URL)       : {0}";
	private static final String DisplayUserNameArgumentMsg =          "-u    (OpCon User Name)        : {0}";
	private static final String DisplayGroupNamesArgumentMsg =        "-gn   (Group Names to export)  : {0}";
	private static final String DisplayOutputDirectoryArgumentMsg =   "-odir (Output Directory)       : {0}";
	private static final String DisplayDebugArgumentMsg =             "-debug                         : {0}";
	
	private static final String CompletedProcessingMsg = "Request Completed with Status ({0})";
	
    private final static Logger LOG = LoggerFactory.getLogger(Export.class);
	
	public static void main(String[] args) {

		IExport _IExport = new ExportImpl(); 
		VisionImpexArguments _VisionImpexArguments = new VisionImpexArguments();
		JCommander jcVisionImpexArguments = null;
		
		Utilities _Utilities = new Utilities();
		
		boolean success = false;
		
		try {
    		// get the arguments
			jcVisionImpexArguments = JCommander.newBuilder()
					.addObject(_VisionImpexArguments)
					.build();
			jcVisionImpexArguments.parse(args);

			if(_VisionImpexArguments.getOutputDirectory() == null) {
				LOG.error(SeperatorLineMsg);
				LOG.error(MessageFormat.format(ProgramNameAndVersionMsg, IVisionImpexConstants.SOFTWARE_VERSION));
				LOG.error(SeperatorLineMsg);
				LOG.error(MissingOutputDirectoryMsg);
				LOG.error(SeperatorLineMsg);
				System.exit(1);
			}
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(ProgramNameAndVersionMsg, IVisionImpexConstants.SOFTWARE_VERSION));
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(DisplayOpConUrlArgumentMsg, _VisionImpexArguments.getOpConSystemUrl()));
			LOG.info(MessageFormat.format(DisplayUserNameArgumentMsg, _VisionImpexArguments.getUserName()));
			LOG.info(MessageFormat.format(DisplayOutputDirectoryArgumentMsg, _VisionImpexArguments.getOutputDirectory()));
			if(_VisionImpexArguments.getGroupNamesToExtract() != null) {
				LOG.info(MessageFormat.format(DisplayGroupNamesArgumentMsg, _VisionImpexArguments.getGroupNamesToExtract()));
			} else {
				LOG.info(MessageFormat.format(DisplayGroupNamesArgumentMsg, "all"));
			}
			LOG.info(MessageFormat.format(DisplayDebugArgumentMsg, _VisionImpexArguments.isDebug()));
			LOG.info(SeperatorLineMsg);
			success = _IExport.processRequest(_VisionImpexArguments);
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(CompletedProcessingMsg, success));
			LOG.info(SeperatorLineMsg);
			if(!success) {
				System.exit(1);
			}
		} catch (com.beust.jcommander.ParameterException pe) {
			jcVisionImpexArguments.usage();
			System.exit(1);
		} catch (Exception ex) {
			LOG.error(_Utilities.getExceptionDetails(ex));
			System.exit(1);
		}
		System.exit(0);
	} // END : main

}
