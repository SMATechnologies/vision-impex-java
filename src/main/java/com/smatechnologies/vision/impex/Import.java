package com.smatechnologies.vision.impex;

import java.text.MessageFormat;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.impl.ImportImpl;
import com.smatechnologies.vision.impex.interfaces.IVisionImpexConstants;
import com.smatechnologies.vision.impex.interfaces.IImport;
import com.smatechnologies.vision.impex.util.Utilities;

public class Import {

	private static final String SeperatorLineMsg =                    "---------------------------------------------------------------------";
	private static final String MissingInputDirectoryMsg =            "Input directory argument (-idir) missing for Import function";
	private static final String MissingInputFileMsg =                 "Input file argument (-if) missing for Import function";
	
	private static final String ProgramNameAndVersionMsg =            "Import                         : Version {0}";
	private static final String DisplayOpConUrlArgumentMsg =          "-url  (OpCon System URL)       : {0}";
	private static final String DisplayUserNameArgumentMsg =          "-u    (OpCon User Name)        : {0}";
	private static final String DisplayInputDirectoryArgumentMsg =    "-idir (input directory)        : {0}";
	private static final String DisplayInputFileArgumentMsg =         "-f    (file)                   : {0}";
	private static final String DisplayDebugArgumentMsg =             "-debug                         : {0}";
	
	private static final String CompletedProcessingMsg = "Request Completed with Status ({0})";
	
    private final static Logger LOG = LoggerFactory.getLogger(Import.class);
	
	public static void main(String[] args) {

		IImport _IImport = new ImportImpl(); 
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

			if(_VisionImpexArguments.getInputDirectory() == null) {
				LOG.error(SeperatorLineMsg);
				LOG.error(MessageFormat.format(ProgramNameAndVersionMsg, IVisionImpexConstants.SOFTWARE_VERSION));
				LOG.error(SeperatorLineMsg);
				LOG.error(MissingInputDirectoryMsg);
				LOG.error(SeperatorLineMsg);
				System.exit(1);
			}
			if(_VisionImpexArguments.getFileName() == null) {
				LOG.error(SeperatorLineMsg);
				LOG.error(MessageFormat.format(ProgramNameAndVersionMsg, IVisionImpexConstants.SOFTWARE_VERSION));
				LOG.error(SeperatorLineMsg);
				LOG.error(MissingInputFileMsg);
				LOG.error(SeperatorLineMsg);
				System.exit(1);
			}
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(ProgramNameAndVersionMsg, IVisionImpexConstants.SOFTWARE_VERSION));
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(DisplayOpConUrlArgumentMsg, _VisionImpexArguments.getOpConSystemUrl()));
			LOG.info(MessageFormat.format(DisplayUserNameArgumentMsg, _VisionImpexArguments.getUserName()));
			LOG.info(MessageFormat.format(DisplayInputDirectoryArgumentMsg, _VisionImpexArguments.getOutputDirectory()));
			LOG.info(MessageFormat.format(DisplayInputFileArgumentMsg, _VisionImpexArguments.getFileName()));
			LOG.info(MessageFormat.format(DisplayDebugArgumentMsg, _VisionImpexArguments.isDebug()));
			LOG.info(SeperatorLineMsg);
			success = _IImport.processRequest(_VisionImpexArguments);
			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(CompletedProcessingMsg, success));
			LOG.info(SeperatorLineMsg);
			if(!success) {
				System.exit(1);
			}
		} catch (com.beust.jcommander.ParameterException pe) {
			LOG.error(_Utilities.getExceptionDetails(pe));
			jcVisionImpexArguments.usage();
			System.exit(1);
		} catch (Exception ex) {
			LOG.error(_Utilities.getExceptionDetails(ex));
			System.exit(1);
		}
		System.exit(0);
	} // END : main

}
