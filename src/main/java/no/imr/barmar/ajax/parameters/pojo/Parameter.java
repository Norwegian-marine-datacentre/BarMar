package no.imr.barmar.ajax.parameters.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Parameter  implements Comparable<Parameter> {
	//@JsonIgnore
	private Integer id;
	@JsonIgnore
	private String name;
	private String metadataRef;
	private List<String> depths = new ArrayList<String>();
	private List<String> periods = new ArrayList<String>();
	
	@JsonIgnore
	private int yearOrLength;
	@JsonIgnore
	private String nameSubstringNotContainingDigit;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMetadataRef() {
		return metadataRef;
	}
	public void setMetadataRef(String metadataRef) {
		this.metadataRef = metadataRef;
	}
	public List<String> getDepths() {
		return depths;
	}
	public void setDepths(List<String> depths) {
		this.depths = depths;
	}
	public List<String> getPeriods() {
		return periods;
	}
	public void setPeriods(List<String> periods) {
		this.periods = periods;
	}
	
	public int getYearOrLength() {
		return yearOrLength;
	}
	public void setYearOrLength(int yearOrLength) {
		this.yearOrLength = yearOrLength;
	}
	public String getNameSubstringNotContainingDigit() {
		return nameSubstringNotContainingDigit;
	}
	public void setNameSubstringNotContainingDigit(String nameSubstringNotContainingDigit) {
		this.nameSubstringNotContainingDigit = nameSubstringNotContainingDigit;
	}
	@Override
	public int compareTo(Parameter p1) {
		int yearOrLength = this.getYearOrLength() - p1.getYearOrLength();
		int nameEquality = this.getNameSubstringNotContainingDigit().compareToIgnoreCase(p1.getNameSubstringNotContainingDigit()); 
		if ( nameEquality == 0) {
			return yearOrLength;
		}
		return nameEquality;
	}
}
