package no.imr.barmar.geoserver;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UrlConsts {
	
    @Autowired
    private Configuration configuration;
	
	// Change url to change test environment - also change in comboboxNorMar.js 
	// And use your local ip address when testing - e.g. http://10.1.9.221/geodata/nordicSeaMarineAtlas.html
    public final static String mainUrl = "https://maps.imr.no/geoserver/wms?";
    //public final static String mainUrl = "http://geb-test.nodc.no:8080/geoserver/wms?";
    //public final static String MAIN_URL = "http://10.1.9.138:8080/geoserver/wms?";
	//private String mainUrl = "http://geb-test.nodc.no/geoserver/wms?";//configuration.getString("imr.geoserver");
	private String normar = "NorMar";
	private String barmarArea = "postgis:barmarAreavalueAggregate";
	private String barmarPoint = "postgis:barmarPointvalueAggregate";
	private String normarArea = "postgis:normarAreavalueAggregate";
	private String normarPoint = "postgis:normarPointvalueAggregate";

    
	public String getMainUrl() {
		return mainUrl;
	}    
	public String getNorMar() {
		return normar;
	}
	public String getBarMarArea() {
		return barmarArea;
	}
	public String getBarMarPoint() {
		return barmarPoint;
	}
	public String getNorMarArea() {
		return normarArea;
	}
	public String getNorMarPoint() {
		return normarPoint;
	}	
}
