package no.imr.barmar.controller.view.pojo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParameterGroup {
	private List<Parameter> length;
	private List<Parameter> age;
	private List<Parameter> other;
	
	public Map<String, Parameter> getLength() {
		Map<String, Parameter> m = new LinkedHashMap<String, Parameter>(); //linkedHashMap to preserve order of Parameter Comparotor in List
		for ( Parameter p : length ) {
			m.put(p.getName(), p);
		}
		return m;
	}
	public void setLength(List<Parameter> length) {
		this.length = length;
	}
	public Map<String, Parameter> getAge() {
		Map<String, Parameter> m = new LinkedHashMap<String, Parameter>();
		for ( Parameter p : age ) {
			m.put(p.getName(), p);
		}
		return m;
	}
	public void setAge(List<Parameter> age) {
		this.age = age;
	}
	public Map<String, Parameter> getOther() {
		Map<String, Parameter> m = new LinkedHashMap<String, Parameter>();
		for ( Parameter p : other ) {
			m.put(p.getName(), p);
		}
		return m;
	}
	public void setOther(List<Parameter> other) {
		this.other = other;
	}
}
