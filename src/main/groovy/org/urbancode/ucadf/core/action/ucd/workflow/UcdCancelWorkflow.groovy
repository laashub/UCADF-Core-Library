/**
 * This action cancels a workflow.
 */
package org.urbancode.ucadf.core.action.ucd.workflow

import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import org.urbancode.ucadf.core.actionsrunner.UcAdfAction
import org.urbancode.ucadf.core.model.ucd.exception.UcdInvalidValueException

class UcdCancelWorkflow extends UcAdfAction {
	// Action properties.
	/** The workflow ID. */
	String workflow
	
	/**
	 * Runs the action.	
	 */
	@Override
	public Object run() {
		// Validate the action properties.
		validatePropsExist()

        logInfo("Cancelling workflow [$workflow].")
		
        WebTarget target = ucdSession.getUcdWebTarget().path("/rest/workflow/{workflowId}/cancel")
			.resolveTemplate("workflowId", workflow)
        logDebug("target=$target")
		
        Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(""))
        if (response.getStatus() == 200) {
            logInfo("Workflow [$workflow] cancelled.")
        } else {
            throw new UcdInvalidValueException(response)
        }
	}
}