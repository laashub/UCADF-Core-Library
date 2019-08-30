/**
 * This class is used to import applications.
 */
package org.urbancode.ucadf.core.model.ucd.application

import org.urbancode.ucadf.core.model.ucd.applicationProcess.UcdApplicationProcessImport
import org.urbancode.ucadf.core.model.ucd.component.UcdComponentImport
import org.urbancode.ucadf.core.model.ucd.componentTemplate.UcdComponentTemplateImport
import org.urbancode.ucadf.core.model.ucd.environment.UcdEnvironmentImport
import org.urbancode.ucadf.core.model.ucd.genericProcess.UcdGenericProcessImport
import org.urbancode.ucadf.core.model.ucd.importExport.UcdImport
import org.urbancode.ucadf.core.model.ucd.property.UcdPropDef
import org.urbancode.ucadf.core.model.ucd.property.UcdPropSheet
import org.urbancode.ucadf.core.model.ucd.status.UcdStatus
import org.urbancode.ucadf.core.model.ucd.tag.UcdTag

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class UcdApplicationImport extends UcdImport {
	/** The application processes to import. */
	List<UcdApplicationProcessImport> processes
	
	/** The components to import. */
	List<UcdComponentImport> components
	
	/** The environments to import. */
	List<UcdEnvironmentImport> environments
	
	/** The generic processes to import. */
	List<UcdGenericProcessImport> genericProcesses
	
	/** The flag to enforce complete snapshots. */
	Boolean enforceCompleteSnapshots
	
	/** The property sheet. */
	UcdPropSheet propSheet
	
	/** The template property sheet. */
	UcdPropSheet templatePropSheet

	/** The list of statuses. */
	List<UcdStatus> statuses
	
	/** The list of tags. */
	List<UcdTag> tags

	// Constructors.
	UcdApplicationImport() {
	}
	
	public filterEnvironments(String match) {
		if (match) {
			List envList = []
			for (UcdEnvironmentImport environment in environments) {
				if (environment.name ==~ match) {
					envList.add(environment)
				}
			}
			environments = envList
		}
	}	
	
	// Get the generic process imports used by the application, each of the application's components, and each of the component templates
	public Map<String, UcdGenericProcessImport> getAllGenericProcessImports(
		final String compMatch = "", 
		final String compTempMatch = "", 
		final String genProcessMatch = "") {
		
		// Get the generic processes used by the application
		Map<String, UcdGenericProcessImport> genProcessImports = getGenericProcessImports(genProcessMatch)

		// Process each of the components used by the application
		for (compImport in getComponentImports(compMatch).values()) {
			genProcessImports += compImport.getGenericProcessImports(genProcessMatch)
			
			// Process the component template used by the component
			UcdComponentTemplateImport compTempImport = compImport.componentTemplate
			if (compTempImport && compTempImport.getName() ==~ compTempMatch) {
				genProcessImports += compTempImport.getGenericProcessImports(compTempImport.genericProcesses, genProcessMatch)
			}
		}
		
		return genProcessImports
	}
	
	// Get the application's component imports	
	public Map<String, UcdComponentImport> getComponentImports(final String compMatch) {
		// Process each of the components used by the application
		Map<String, UcdComponentImport> compImports = new TreeMap()
		for (UcdComponentImport compImport in components) {
			if (compImport.getName() ==~ compMatch) {
				compImports.put(compImport.getName(), compImport)
			}
		}
		
		return compImports
	}
	
	// Get the component template imports used by each of the application's components
	public Map<String, UcdComponentTemplateImport> getComponentTemplateImports(
		final String compMatch, 
		final String compTempMatch) {
		
		// Process each of the components used by the application
		Map<String, UcdComponentTemplateImport> compTempImports = new TreeMap()
		for (compImport in getComponentImports(compMatch).values()) {
			// Process the component template used by the component
			UcdComponentTemplateImport compTempImport = compImport.getComponentTemplate()
			if (compTempImport && compTempImport.getName() ==~ compTempMatch) {
				compTempImports.put(compTempImport.getName(), compTempImport)
			}
		}
		
		return compTempImports
	}
	
	public Map<String, UcdGenericProcessImport> getGenericProcessImports(final String match = "") {
		return getGenericProcessImports(genericProcesses, match)
	}

	// Replace application process HTTP property definitions temporarily	
	public Map<String, List<UcdPropDef>> replaceProcessHttpPropDefs() {
		Map<String, List<UcdPropDef>> replacedProcessPropDefs = new TreeMap()
		for (process in processes) {
			List<UcdPropDef> replacedPropDefs = process.replaceHttpPropDefs(name, process.name)
			if (replacedPropDefs.size() > 0) {
				replacedProcessPropDefs[process.name] = replacedPropDefs
			}
		}
		
		return replacedProcessPropDefs
	}
}