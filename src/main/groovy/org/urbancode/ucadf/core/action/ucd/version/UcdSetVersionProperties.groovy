/**
 * This action sets version properties.
 */
package org.urbancode.ucadf.core.action.ucd.version

import javax.ws.rs.client.Entity
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import org.urbancode.ucadf.core.actionsrunner.UcAdfAction
import org.urbancode.ucadf.core.model.ucd.exception.UcdInvalidValueException
import org.urbancode.ucadf.core.model.ucd.property.UcdProperty
import org.urbancode.ucadf.core.model.ucd.system.UcdSession

import com.fasterxml.jackson.annotation.JsonProperty

import groovy.json.JsonBuilder

class UcdSetVersionProperties extends UcAdfAction {
	// Action properties.
	/** The component name or ID. */
	String component

	/** The version name or ID. */
	String version
	
	/** The list of properties. */	
	@JsonProperty("properties")
	List<UcdProperty> ucdProperties
	
	/**
	 * Runs the action.	
	 */
	@Override
	public Object run() {
		// Validate the action properties.
		validatePropsExist()
		
		// Process each property.
		for (ucdProperty in ucdProperties) {
			setVersionProperty(ucdProperty)
		}
	}
	
	// Set version property value.
	public setVersionProperty(final UcdProperty ucdProperty) {
		logInfo("Setting component [$component] version [$version] property [${ucdProperty.getName()}] secure [${ucdProperty.getSecure()}]" + (ucdProperty.getSecure() ? "." : " value [${ucdProperty.getValue()}]."))
		
		WebTarget target
		Response response

		if (ucdSession.isUcdVersion(UcdSession.UCDVERSION_70)) {
			target = ucdSession.getUcdWebTarget().path("/cli/version/versionProperties")
			logDebug("target=$target")

			Map data = [
				component : component,
				version : version,
				name : ucdProperty.getName(), 
				isSecure : ucdProperty.getSecure(), 
				value : ucdProperty.getValue()
			]

			JsonBuilder jsonBuilder = new JsonBuilder(data)
			
			response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(jsonBuilder.toString()))
		} else {
			target = ucdSession.getUcdWebTarget().path("/cli/version/versionProperties")
				.queryParam("component", component)
				.queryParam("version", version)
				.queryParam("name", ucdProperty.getName())
				.queryParam("value", ucdProperty.getValue())
				.queryParam("isSecure", ucdProperty.getSecure())
			logDebug("target=$target")
			
			response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(""))
		}
		
		if (response.getStatus() == 200) {
			logInfo("Property [${ucdProperty.getName()}] set.")
		} else {
            throw new UcdInvalidValueException(response)
		}
	}	
}