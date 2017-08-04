package no.imr.barmar.gis.sld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.imr.barmar.geoserver.UrlConsts;
import no.imr.barmar.pojo.BarMarPojo;

@Component
public class LayerNameForSLD {
	
	@Autowired
	private UrlConsts urlConsts;
	
    protected String layerName = null;
    
    public String getLayerName( BarMarPojo pojo, Boolean areadisplay ) {

    	Boolean isNorMar = pojo.getGrid().equals( urlConsts.getNorMar() ); 
    	
    	if ( areadisplay && !isNorMar ) {
    		layerName = urlConsts.getBarMarArea();
    	} else if( !areadisplay && !isNorMar ) {
    		layerName = urlConsts.getBarMarPoint();
    	} else if ( areadisplay && isNorMar ) {
			layerName = urlConsts.getNorMarArea();
    	} else if ( !areadisplay && isNorMar ) {
			layerName = urlConsts.getNorMarPoint();
    	}
    	return layerName;
    }
}
