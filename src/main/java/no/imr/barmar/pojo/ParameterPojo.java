package no.imr.barmar.pojo;

import java.util.Comparator;

public class ParameterPojo implements Comparable<ParameterPojo> {
	
	private int yearOrLength;
	private String name;
	
	public ParameterPojo(String name, int yearOrLenght) {
		setName(name);
		setYearOrLength(yearOrLenght);
	}
	
	public int getYearOrLength() {
		return yearOrLength;
	}


	public void setYearOrLength(int yearOrLength) {
		this.yearOrLength = yearOrLength;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(ParameterPojo p1) {
		int nameEquality = this.getName().compareToIgnoreCase(p1.getName());
		if ( nameEquality == 0 )
			return this.getYearOrLength() - p1.getYearOrLength();
		return nameEquality;
	}
	
}
