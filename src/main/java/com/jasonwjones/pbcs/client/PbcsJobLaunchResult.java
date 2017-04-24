package com.jasonwjones.pbcs.client;

public interface PbcsJobLaunchResult {

	/**
	 * Status for job:
	 * 
	 * -1 = in progress
	 *  0 = success
	 *  1 = error
	 *  2 = cancel pending
	 *  3 = cancelled
	 *  4 = invalid parameter
	 *  Integer.MAX_VALUE = unknown
	 * @return
	 */
	public Integer getStatus();

	/**
	 * Typically "Completed" or "Error"
	 * 
	 * @return the descriptive status of result
	 */
	public String getDescriptiveStatus();

	/**
	 * Typically three digits
	 * 
	 * @return the job ID
	 */
	public Integer getJobId();

	public String getJobName();

	public void waitUntilFinished();

}
