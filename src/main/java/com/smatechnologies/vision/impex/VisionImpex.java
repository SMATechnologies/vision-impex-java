package com.smatechnologies.vision.impex;

import java.net.URL;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.smatechnologies.vision.impex.arguments.VisionImpexArguments;
import com.smatechnologies.vision.impex.enums.TaskType;
import com.smatechnologies.vision.impex.impl.ExportImpl;
import com.smatechnologies.vision.impex.impl.ImportImpl;
import com.smatechnologies.vision.impex.interfaces.IExport;
import com.smatechnologies.vision.impex.interfaces.IImport;
import com.smatechnologies.vision.impex.interfaces.IVisionImpexConstants;
import com.smatechnologies.vision.impex.util.Utilities;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.util.StatusPrinter;

public class VisionImpex {

	private static final String SeperatorLineMsg =                    "---------------------------------------------------------------------";
	private static final String MissingOutputDirectoryMsg =           "Output directory argument (-odir) missing for Export function";
	private static final String MissingInputDirectoryMsg =            "Input directory argument (-idir) missing for Import function";
	private static final String MissingInputFileMsg =                 "Input file argument (-if) missing for Import function";
	
	private static final String ProgramNameAndVersionMsg =            "VisionImpex                    : Version {0}";
	private static final String DisplayOpConUrlArgumentMsg =          "-url  (OpCon System URL)       : {0}";
	private static final String DisplayTaskArgumentMsg =              "-t    (task)                   : {0}";
	private static final String DisplayUserNameArgumentMsg =          "-u    (OpCon User Name)        : {0}";
	private static final String DisplayGroupNamesArgumentMsg =        "-gn   (Group Names to export)  : {0}";
	private static final String DisplayOutputDirectoryArgumentMsg =   "-odir (Output Directory)       : {0}";
	private static final String DisplayInputDirectoryArgumentMsg =   "-idir (Output Directory)        : {0}";
	private static final String DisplayInputFileArgumentMsg =         "-f    (file)                   : {0}";
	private static final String DisplayDebugArgumentMsg =             "-debug                         : {0}";
	
	private static final String CompletedProcessingMsg = "Request Completed with Status ({0})";
	
    private final static Logger LOG = LoggerFactory.getLogger(VisionImpex.class);

    
	public static void main(String[] args) {
		VisionImpex _VisionImpex = new VisionImpex();
		IExport _IExport = new ExportImpl(); 
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
			_VisionImpex.setLogger(_VisionImpexArguments.isDebug());

			LOG.info(SeperatorLineMsg);
			LOG.info(MessageFormat.format(ProgramNameAndVersionMsg, IVisionImpexConstants.SOFTWARE_VERSION));
			LOG.info(MessageFormat.format(DisplayTaskArgumentMsg, _VisionImpexArguments.getTask()));
			LOG.info(SeperatorLineMsg);

			TaskType taskType = TaskType.valueOf(_VisionImpexArguments.getTask());
			
			switch (taskType) {
			
			case cexport :
				if(_VisionImpexArguments.getOutputDirectory() == null) {
					LOG.error(MissingOutputDirectoryMsg);
					LOG.error(SeperatorLineMsg);
					System.exit(1);
				}
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
				break;
				
			case cimport :
				if(_VisionImpexArguments.getInputDirectory() == null) {
					LOG.error(MissingInputDirectoryMsg);
					LOG.error(SeperatorLineMsg);
					System.exit(1);
				}
				if(_VisionImpexArguments.getFileName() == null) {
					LOG.error(MissingInputFileMsg);
					LOG.error(SeperatorLineMsg);
					System.exit(1);
				}
				LOG.info(MessageFormat.format(DisplayOpConUrlArgumentMsg, _VisionImpexArguments.getOpConSystemUrl()));
				LOG.info(MessageFormat.format(DisplayUserNameArgumentMsg, _VisionImpexArguments.getUserName()));
				LOG.info(MessageFormat.format(DisplayInputDirectoryArgumentMsg, _VisionImpexArguments.getOutputDirectory()));
				LOG.info(MessageFormat.format(DisplayInputFileArgumentMsg, _VisionImpexArguments.getFileName()));
				LOG.info(MessageFormat.format(DisplayDebugArgumentMsg, _VisionImpexArguments.isDebug()));
				LOG.info(SeperatorLineMsg);
				success = _IImport.processRequest(_VisionImpexArguments);
				break;
			
			}
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

private void setLogger(
		boolean isDebug
		) throws Exception {
	

	    //Debug mode
	    if(isDebug) {
	        System.setProperty(IVisionImpexConstants.LogBackConstant.LEVEL_STDOUT_KEY, IVisionImpexConstants.LogBackConstant.LEVEL_DEBUG_VALUE);
	        System.setProperty(IVisionImpexConstants.LogBackConstant.LEVEL_FILE_KEY, IVisionImpexConstants.LogBackConstant.LEVEL_DEBUG_VALUE);
	        System.setProperty(IVisionImpexConstants.LogBackConstant.STDOUT_PATTERN_KEY, IVisionImpexConstants.LogBackConstant.STDOUT_PATTERN_DEBUG_VALUE);
	    } else {
	        System.clearProperty(IVisionImpexConstants.LogBackConstant.LEVEL_STDOUT_KEY);
	        System.clearProperty(IVisionImpexConstants.LogBackConstant.LEVEL_FILE_KEY);
	        System.clearProperty(IVisionImpexConstants.LogBackConstant.STDOUT_PATTERN_KEY);
	    }
	
	    //Restart logback
	    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
	
	    ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
	    URL url = contextInitializer.findURLOfDefaultConfigurationFile(true);
	
	    JoranConfigurator joranConfigurator = new JoranConfigurator();
	    joranConfigurator.setContext(loggerContext);
	    loggerContext.reset();
	    joranConfigurator.doConfigure(url);
	
	    StatusPrinter.printIfErrorsOccured(loggerContext);
	}

}
