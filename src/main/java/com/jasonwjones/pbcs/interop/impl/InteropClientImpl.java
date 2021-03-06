package com.jasonwjones.pbcs.interop.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.jasonwjones.pbcs.api.v3.HypermediaLink;
import com.jasonwjones.pbcs.api.v3.ServiceDefinitionWrapper;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.interop.InteropClient;

public class InteropClientImpl implements InteropClient {

	private static final Logger logger = LoggerFactory.getLogger(InteropClientImpl.class);
		
	private RestTemplate restTemplate;

	private String baseUrl;
		
	private PbcsServiceConfiguration serviceConfiguration;
	
	public InteropClientImpl(PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		logger.info("Initializing PBCS Interop API");
		this.serviceConfiguration = serviceConfiguration;
		this.baseUrl = serviceConfiguration.getScheme() + "://" + connection.getServer() + serviceConfiguration.getInteropRestApiPath(); // + defaultVersion; // + "/" + "applicationsnapshots";
		this.restTemplate = new RestTemplate(serviceConfiguration.createRequestFactory(connection));
		
		// post to base url brings back a JSON object with *links* such as to 
		// applicationsnapshots and others
		//ResponseEntity<String> checkApi = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion(), String.class);
		//System.out.println("Content: " + checkApi.getBody());
		
		//logger.info("Initialized");
//		ResponseEntity<String> snapshots = restTemplate.getForEntity(baseUrl + defaultVersion + "/applicationsnapshots", String.class);
//		System.out.println("Content: " + snapshots.getBody());
//
//		ResponseEntity<ApplicationSnapshotsWrapper> snaps = restTemplate.getForEntity(baseUrl+ defaultVersion + "/applicationsnapshots", ApplicationSnapshotsWrapper.class);
//		System.out.println("Snaps: " + snaps);
//
//		
//		// known types: LCM, EXTERNAL
//		String template = "%-20s %-30s %-20s %-20s%n";
//		System.out.printf(template, "Last Modified Time", "Name", "Type", "Size");
//		System.out.printf(template, "--------------------", "--------------------", "---", "---");
//
//		for (ApplicationSnapshot snapshot : snaps.getBody().getItems()) {
//			System.out.printf(template, snapshot.getLastModifiedTime(), snapshot.getName(), snapshot.getType(), snapshot.getSize());		
//		}
//		
		//Vision SS 14 Feb 2016
	}
	
	public void getApiVersions() {
		logger.info("Listing REST API versions");
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		System.out.println("Respo: " + response.getBody());
	}
	
	
	public List<ApplicationSnapshot> listFiles() {
		ResponseEntity<ApplicationSnapshotsWrapper> snaps = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots", ApplicationSnapshotsWrapper.class);		
		return Collections.unmodifiableList(snaps.getBody().getItems());
	}
	
	public ApplicationSnapshotInfo getFileInfo(String filename) {
		return null;
	}
	
	@Override
	public void uploadFile(String filename) {
		File fileToUpload = new File(filename);
		if (!fileToUpload.exists()) {
			logger.error("File {} does not exist");
			throw new PbcsClientException("File to upload does not exist: " + filename);
		} else {
			logger.info("Found local file");
		}

		try {
			String filenameOnly = SimpleFilenameUtils.getName(filename);
			logger.info("Source file {} will be {} on target", filename, filenameOnly);
			byte[] data = readFileToBytes(filename);
			String url = String.format("/applicationsnapshots/%s/contents?q={chunkSize:%d,isFirst:%b,isLast:%b}", filenameOnly, data.length, true, true);

			URI uri = UriComponentsBuilder.fromHttpUrl(this.baseUrl + serviceConfiguration.getInteropApiVersion() + url).build().toUri();
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<byte[]> entity = new HttpEntity<byte[]>(data, headers);
			headers.set("Content-Type", "application/octet-stream");
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			logger.info("Response: {}", response.getBody());
		} catch (IOException e) {
			throw new PbcsClientException("Couldn't read/upload file", e);
		}
	}
	
	// upload local txt to zip
	public void uploadFile(String remoteFilename, List<String> localFiles) {
		throw new RuntimeException("Not implemented yet");
	}
	
	@Override
	public File downloadFile(String filename) {
		return downloadFile(filename, filename);
	}
	
	// Should return a rich object:
	// File getFile()
	// extract(String targetDir)
	// etc.
	@Override
	public File downloadFile(String filename, String localFilename) {
		logger.info("Requested file download: {}", filename);
		// TODO Auto-generated method stub
		
		File localFile = new File(localFilename);
		if (localFile.isDirectory()) {
			logger.info("Will download {} to local folder {}", filename, localFile);
		} else {
			
		}
		
		String url = this.baseUrl + serviceConfiguration.getInteropApiVersion() + String.format("/applicationsnapshots/%s/contents", filename);
		//ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
		
		logger.info("Headers: " + response.getHeaders());
		
		// likely to be zip or csv (can it be xml?)
		String extension = response.getHeaders().get("fileExtension").get(0);
		
		logger.info("Canonical file extension for local file: {}", extension); 
		
//		System.out.println("Content-Type: " + response.getHeaders().getContentType());
//		System.out.println("Is JSON: " + response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
//		System.out.println("Code: " + response.getStatusCode().value());
//		System.out.println("Response: " + response.getBody());

		try {
			File outputFile = new File(localFilename);
			// TODO: add file extension if the target file does not end with it
			FileUtils.writeByteArrayToFile(outputFile, response.getBody());
			return outputFile;
		} catch (IOException e) {
			logger.error("Unable to write local file", e);
			throw new PbcsClientException("Unable to write downloaded file", e);
		}
	}

	// TODO: apparently PBCS REST API returns a JSON payload so we might need to 
	// switch to using the exchange() method to get the details
	public void deleteFile(String filename) {
		logger.info("Deleting {}", filename);
		restTemplate.delete(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots/{filename}", filename);
	}
	
	public void listServices() {
		logger.info("Listing services");
		ResponseEntity<ServiceDefinitionWrapper> response = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/services", ServiceDefinitionWrapper.class);
		System.out.println("Code: " + response.getStatusCode().value());
		System.out.println("Response: " + response.getBody());
		
		for (HypermediaLink link : response.getBody().getLinks()) {
			System.out.println(link);
		}
		
	}

//	public void e
	
	@Override
	public void LcmExport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void LcmImport() {
		// TODO Auto-generated method stub
		
	}
	
	private byte[] readFileToBytes(String filename) throws IOException {
		File fff = new File(filename);
	    FileInputStream fileInputStream = new FileInputStream(fff);
	    int byteLength = (int) fff.length(); //bytecount of the file-content
	    byte[] filecontent = new byte[byteLength];
	    fileInputStream.read(filecontent, 0, byteLength);
	    fileInputStream.close();
	    return filecontent;
	}
	
}
