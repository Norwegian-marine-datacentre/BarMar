package no.imr.barmar.controller.view.pojo;

import java.util.ArrayList;
import java.util.List;

public class Species {
	private String name;
	private List<Parameter> parameteres = new ArrayList<Parameter>(900);
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Parameter> getParameteres() {
		return parameteres;
	}
	public void setParameteres(List<Parameter> parameteres) {
		this.parameteres = parameteres;
	}
	public void addParameters( Parameter parametere) {
		parameteres.add(parametere);
	}
}
