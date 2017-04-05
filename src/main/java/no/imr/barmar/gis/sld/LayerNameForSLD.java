package no.imr.barmar.gis.sld;

import org.springframework.stereotype.Component;

import no.imr.barmar.geoserver.UrlConsts;
import no.imr.barmar.pojo.BarMarPojo;

@Component
public class LayerNameForSLD {
    protected String layerName = null;
    
    public String getLayerName( BarMarPojo pojo, Boolean areadisplay ) {

    	Boolean isNorMar = pojo.getGrid().equals( UrlConsts.NORMAR ); 
    	
    	if ( areadisplay && !isNorMar ) {
    		layerName = UrlConsts.BARMAR_AREAVALUE;
    	} else if( !areadisplay && !isNorMar ) {
    		layerName = UrlConsts.BARMAR_POINTVALUE;
    	} else if ( areadisplay && isNorMar ) {
			layerName = UrlConsts.NORMAR_AREAVALUE;
    	} else if ( !areadisplay && isNorMar ) {
			layerName = UrlConsts.NORMAR_POINTVALUE;
    	}
    	return layerName;
    }
}
