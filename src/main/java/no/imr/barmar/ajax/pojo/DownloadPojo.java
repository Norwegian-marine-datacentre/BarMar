package no.imr.barmar.ajax.pojo;

public class DownloadPojo {
	private Integer id;
	private String geogridcell;
	private String geopoint;
	private String gridname;
	private String gridcellname;
	private String parametername;
	private String depthlayername;
	private String periodname;
	private float value;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGeogridcell() {
		return geogridcell;
	}
	public void setGeogridcell(String geogridcell) {
		this.geogridcell = geogridcell;
	}
	public String getGeopoint() {
		return geopoint;
	}
	public void setGeopoint(String geopoint) {
		this.geopoint = geopoint;
	}
	public String getGridname() {
		return gridname;
	}
	public void setGridname(String gridname) {
		this.gridname = gridname;
	}
	public String getGridcellname() {
		return gridcellname;
	}
	public void setGridcellname(String gridcellname) {
		this.gridcellname = gridcellname;
	}
	public String getParametername() {
		return parametername;
	}
	public void setParametername(String parametername) {
		this.parametername = parametername;
	}
	public String getDepthlayername() {
		return depthlayername;
	}
	public void setDepthlayername(String depthlayername) {
		this.depthlayername = depthlayername;
	}
	public String getPeriodname() {
		return periodname;
	}
	public void setPeriodname(String periodname) {
		this.periodname = periodname;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
}
