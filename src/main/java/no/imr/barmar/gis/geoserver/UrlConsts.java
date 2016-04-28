package no.imr.barmar.gis.geoserver;

public class UrlConsts {
	
	// Change url to change test environment - also change in comboboxNorMar.js 
	// And use your local ip address when testing - e.g. http://10.1.9.221/geodata/nordicSeaMarineAtlas.html
	public final static String MAIN_URL = "http://maps.imr.no/geoserver/wms?";
//	public final static String MAIN_URL = "http://geb-test.imr.no/geoserver/wms?";
	
	
	public final static String SERVICE = "service=WFS";
	public final static String VERSION = "version=1.0.0";
	public final static String REQUEST_TYPE = "request=GetFeature";
	
    public final static String BASE_URL_REQUEST = MAIN_URL + SERVICE + "&" + VERSION + "&" + REQUEST_TYPE + "&";
    
    public final static String TYPE = "service=WMS&version=1.1.1&request=GetLegendGraphic&";
    public final static String TYPE2 = "&width=22&height=24&format=image/png";
    
    public final static String GRID_PARAMETER_TIME = "typeName=postgis:grid_parameter_time";
    public final static String GRID_PARAMETER_DEPTH = "typeName=postgis:grid_parameter_depth";

    public final static String GRID_PARAMETER_NAME_BARMAR = "typeName=postgis:grid_parameter_name";
    public final static String GRID_PARAMETER_NAME_NORMAR =  "typeName=postgis:grid_parameter_name_normar";
    
    public final static String NORMAR_LEGEND_LAYER = "typeName=postgis:temperature_maxminNormar";
    public final static String BARMAR_LEGEND_LAYER = "typeName=postgis:temperature_maxmin";
    
    public final static String BARMAR_AREAVALUE = "postgis:areavalue";
    public final static String BARMAR_POINTVALUE = "postgis:pointvalue";
    

    
    public final static String NORMAR_AREAVALUE_AGGREGATED = "postgis:areavalueAggregate"; //NORMAR_AREAVALUE; //"test:areavalueNormar2"; 
    public final static String NORMAR_POINTVALUE_AGGREGATED = "postgis:pointvalueAggregate"; //NORMAR_POINTVALUE; //"test:pointvalueNormar2";
    
    public final static String NORMAR_AREAVALUE = "postgis:areavalueNormar"; //"postgis:areavalueNormarOneParam";  //NORMAR_AREAVALUE_AGGREGATED;
    public final static String NORMAR_POINTVALUE = "postgis:pointvalueNormar"; //"postgis:pointvalueNormarOneParam";    //NORMAR_POINTVALUE_AGGREGATED;
    
    public final static String NORMAR = "NorMar";
    
    
}
