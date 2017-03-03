package no.imr.barmar.gis.sld;

import java.util.List;

import org.springframework.stereotype.Component;

import no.imr.barmar.geoserver.UrlConsts;
import no.imr.barmar.pojo.BarMarPojo;

@Component
public class LayerNameForSLD {
    protected String layerName = null;
    
    public String getLayerName( BarMarPojo pojo, Boolean areadisplay ) {
//    	List<String> parameterNames = pojo.getParameter();
    	List<String> times = pojo.getTime();
    	List<String> depths = pojo.getDepth();
    	Boolean isNorMar = pojo.getGrid().equals( UrlConsts.NORMAR );
    	
    	if ( areadisplay && !isNorMar ) {
    		layerName = UrlConsts.BARMAR_AREAVALUE_AGGREGATED; //UrlConsts.BARMAR_AREAVALUE;
    	} else if( !areadisplay && !isNorMar ) {
    		layerName = UrlConsts.BARMAR_POINTVALUE;
    	} else if ( areadisplay && isNorMar ) {
//    		if ( parameterNames.size()  > 1 ) {
    		if( times.size() > 1 || depths.size() > 1) {
    			layerName = UrlConsts.NORMAR_AREAVALUE_AGGREGATED;
    		} else {
    			layerName = UrlConsts.NORMAR_AREAVALUE;
    		}
    	} else if ( !areadisplay && isNorMar ) {
    		//if ( parameterNames.size()  > 1 ) {
    		if( times.size() > 1 /* || depths.size() > 1*/ ) {//TODO: add aggregate search over depths
    			layerName = UrlConsts.NORMAR_POINTVALUE_AGGREGATED;
    		} else {
    			layerName = UrlConsts.NORMAR_POINTVALUE;
    		}
    	}
    	return layerName;
    }
}
