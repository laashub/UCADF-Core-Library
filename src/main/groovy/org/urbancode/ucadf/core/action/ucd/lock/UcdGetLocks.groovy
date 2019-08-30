/**
 *  This action returns a list of locks.
 */
package org.urbancode.ucadf.core.action.ucd.lock

import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import org.urbancode.ucadf.core.actionsrunner.UcAdfAction
import org.urbancode.ucadf.core.model.ucd.exception.UcdInvalidValueException
import org.urbancode.ucadf.core.model.ucd.lock.UcdLock

class UcdGetLocks extends UcAdfAction {
	// Action properties.
	/** (Optional) If specified then gets locks with names that match this regular expression. */
	String match = ""
	
	/**
	 * Runs the action.	
	 * @return The list of lock objects.
	 */
	@Override
	public List<UcdLock> run() {
		// Validate the action properties.
		validatePropsExist()

		List<UcdLock> ucdReturnLocks = []
		
		logInfo("Getting locks.")
	
		WebTarget target = ucdSession.getUcdWebTarget().path("/rest/lock/lock")
		logDebug("target=$target")
		
		Response response = target.request().get()
		if (response.getStatus() == 200) {
			List<UcdLock> ucdLocks = response.readEntity(new GenericType<List<UcdLock>>(){})

			if (match) {
				ucdReturnLocks = ucdLocks.findAll {
					(it.getLockName() ==~ match)
				}
			} else {
				ucdReturnLocks = ucdLocks
			}
		} else {
			throw new UcdInvalidValueException(response)
		}
		
		return ucdReturnLocks
	}
}