package com.jasonwjones.pbcs.client;

import java.util.List;
import java.util.Map;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;

/**
 * Models a PBCS application such as a Planning or an HFM application.
 * 
 * @author jasonwjones
 *
 */
public interface PbcsApplication {

	/**
	 * Whether the application supports decision packages
	 * 
	 * @return true if DPs are supported, false otherwise
	 */
	public boolean isDpEnabled();

	/**
	 * Get the name of the application
	 * 
	 * @return the name of the application
	 */
	public String getName();

	/**
	 * Gets the product type. Possible values: HFM, HP
	 * 
	 * @return the product type
	 */
	public String getType();

	public List<PbcsJobDefinition> getJobDefinitions();

	public List<PbcsJobDefinition> getJobDefinitions(PbcsJobType jobType);

	/**
	 * Fetches the status of a job with the given ID
	 * 
	 * @param jobId the ID of the job
	 * @return a job status
	 */
	public PbcsJobStatus getJobStatus(Integer jobId);

	/**
	 * Launches a business rule on the application, providing no additional
	 * parameters
	 * 
	 * @param ruleName the name of the business rule exactly as it appears in
	 *            the application
	 * @return a job launch result
	 */
	public PbcsJobLaunchResult launchBusinessRule(String ruleName);

	/**
	 * Launches a business rule on the application, providing additional
	 * parameters
	 * 
	 * @param ruleName the name of the business rule exactly as it appears in
	 *            the application
	 * @param parameters the parameters to pass along
	 * @return a job launch result
	 */
	public PbcsJobLaunchResult launchBusinessRule(String ruleName, Map<String, String> parameters);

	public void launchRuleSet(String ruleSetName);

	public void launchDataImport(String dataImportName);

	public PbcsJobLaunchResult importMetadata(String metadataImportName, String dataFile);

	public PbcsJobLaunchResult importMetadata(String metadataImportName);

	public PbcsJobLaunchResult exportData(String exportName);

	public PbcsJobLaunchResult refreshCube();

	/**
	 * Refreshes the cube with the refresh name. If the refresh name listed in
	 * your cube doesn't work, try using 'CubeRefresh'. During testing this
	 * seemed to work even when the refresh name was actually different.
	 * 
	 * @param cubeRefreshName the CUBE_REFRESH name
	 * @return a job launch result
	 */
	public PbcsJobLaunchResult refreshCube(String cubeRefreshName);

	// public PbcsJobLaunchResult refreshCubeSynchronous(String cubeRefreshName,
	// SyncProps props);

	public PbcsMemberProperties addMember(String dimensionName, String memberName, String parentName);

	public PbcsMemberProperties getMember(String dimensionName, String memberName);

	public void getUserPreferences();

	// /**
	// * Not implemented (stubbed out for future implementation). Also needed:
	// * Planning Units
	// */
	// TODO: Plan Type Map, copy data from ASO/BSO or vice versa
	// params:
	// {"jobType":"PLAN_TYPE_MAP","jobName":"MapReporting","parameters":{"cubeLinkName":"name","clearData":true}}
	// public void launchPlanTypeMap();
	// TODO: Launch Metadata Import
	// TODO: Export Metadata

	public void exportMetadata(String jobName, String exportFileName);

	/**
	 * Exports a data slice from the cube.
	 * 
	 * @param planType the plan type to export from
	 * @param dataSlice the export data slice definition
	 * @return a data slice object (pov, headers, rows) of the results
	 */
	public DataSlice exportDataSlice(String planType, ExportDataSlice dataSlice);

}