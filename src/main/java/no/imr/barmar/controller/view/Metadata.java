package no.imr.barmar.controller.view;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Metadata {
	@JsonIgnore
	private String datasetName;
	@JsonIgnore
	private String geographicCoverage;
	private String metadata;
	@JsonIgnore
	private String datasetOriginator;
	@JsonIgnore
	private String datasetContact;
	@JsonIgnore
	private String datasetLastUpdated;
	
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	public String getGeographicCoverage() {
		return geographicCoverage;
	}
	public void setGeographicCoverage(String geographicCoverage) {
		this.geographicCoverage = geographicCoverage;
	}
	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	public String getDatasetOriginator() {
		return datasetOriginator;
	}
	public void setDatasetOriginator(String datasetOriginator) {
		this.datasetOriginator = datasetOriginator;
	}
	public String getDatasetContact() {
		return datasetContact;
	}
	public void setDatasetContact(String datasetContact) {
		this.datasetContact = datasetContact;
	}
	public String getDatasetLastUpdated() {
		return datasetLastUpdated;
	}
	public void setDatasetLastUpdated(String datasetLastUpdated) {
		this.datasetLastUpdated = datasetLastUpdated;
	}
}
