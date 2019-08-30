/**
 * This action copies all of the application processes from one application to another.
 */
package org.urbancode.ucadf.core.action.ucd.applicationProcess

import org.urbancode.ucadf.core.actionsrunner.UcAdfAction
import org.urbancode.ucadf.core.model.ucd.applicationProcess.UcdApplicationProcess
import org.urbancode.ucadf.core.model.ucd.applicationProcess.UcdApplicationProcessReplacement

// Copy application processes from one application to another.
// This will not delete additional processes that exist in the to application.
// If no specific process names are provided then all processes are copied.
class UcdCopyApplicationProcesses extends UcAdfAction {
	// Action properties.
	/** The from application name or ID. */
	String fromApplication
	
	/** The to application name or ID. */
	String toApplication
	
	List<String> processes = []
	List<UcdApplicationProcessReplacement> replaceList = []
	Boolean replaceProcess = true
	
	/**
	 * Runs the action.	
	 * @return The list of application from processes.
	 */
	@Override
	public List<UcdApplicationProcess> run() {
		// Validate the action properties.
		validatePropsExist()

		// Initialize a default replace list if none provided.
		if (replaceList.size() == 0) {
			replaceList = UcdApplicationProcessReplacement.getDefaultReplaceList(fromApplication, toApplication)
		}
		
		logInfo("Copying application processes from [$fromApplication] to [$toApplication]."		)
		List<UcdApplicationProcess> fromProcesses = actionsRunner.runAction([
			action: UcdGetApplicationProcesses.getSimpleName(),
			application: fromApplication
		])

		for (fromProcess in fromProcesses) {
			if (processes.size() == 0 || processes.contains(fromProcess.getName())) {
				actionsRunner.runAction([
					action: UcdCopyApplicationProcess.getSimpleName(),
					fromApplication: fromApplication,
					fromProcess: fromProcess.getName(),
					toApplication: toApplication, 
					replaceList: replaceList,
					replaceProcess: replaceProcess
				])
			}
		}
		
        return fromProcesses
	}
}