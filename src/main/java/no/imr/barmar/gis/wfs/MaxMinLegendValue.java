package no.imr.barmar.gis.wfs;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.imr.barmar.geoserver.UrlConsts;
import no.imr.barmar.gis.sld.StAXreader.StAXreaderException;
import no.imr.barmar.pojo.BarMarPojo;

/**
 * 
 * @author endrem
 */
@Component
public class MaxMinLegendValue {
	
	@Autowired
	private GetWFSList gwfs;
	
	/**
	 * Get max and min value from wfs postgis:temperature - NorMar or BarMar
	 * @param queryPojo
	 * @throws IOException
	 * @throws StAXreaderException
	 * @throws XMLStreamException
	 */
	public void setMaxMinLegendValuesFromWFS( BarMarPojo queryPojo, List<String> parameterNames ) throws IOException, StAXreaderException, XMLStreamException {
//		String param = queryPojo.getParameter(0);
//		queryPojo.setParameter(parameterNames);
        String urlRequest = setNorMarOrBarMarLayerName(queryPojo);
//        queryPojo.setParameter( Arrays.asList(param));
        
        List<String> maxvals = gwfs.getWFSList( "maxval", queryPojo, urlRequest );
        List<String> minvals = gwfs.getWFSList( "minval", queryPojo, urlRequest );        
        float max = getMaxvalue( maxvals );
        float min = getMinvalue( minvals );
        queryPojo.setMaxLegend( max );
        queryPojo.setMinLegend( min );
	}
	
	public void setMaxMinLegendValuesFromWFS( BarMarPojo queryPojo ) throws IOException, StAXreaderException, XMLStreamException { 
        String urlRequest = setNorMarOrBarMarLayerName( queryPojo );
        
        List<String> maxvals = gwfs.getWFSList( "maxval", queryPojo, urlRequest );
        List<String> minvals = gwfs.getWFSList( "minval", queryPojo, urlRequest );        
        float max = getMaxvalue( maxvals );
        float min = getMinvalue( minvals );
        queryPojo.setMaxLegend( max );
        queryPojo.setMinLegend( min );
	}	
	
	protected String setNorMarOrBarMarLayerName(BarMarPojo queryPojo) {
		String grid = queryPojo.getGrid(); 
		
        String urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.BARMAR_LEGEND_LAYER;
        if (grid.equals("NorMar")) {
        	urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.NORMAR_LEGEND_LAYER;
        }	
        return urlRequest;
	}
	
	protected float getMaxvalue( List<String> maxvals ) {
		Float max = Float.MIN_VALUE;
		if ( maxvals.size() == 0 ) {
			max = Float.MIN_VALUE;
		} else if ( maxvals.size() == 1 ) {
			max = stringToFloat( maxvals.get( 0 ), Float.MIN_VALUE );	
		} else {
			for ( String next : maxvals ) {
				Float nextVal = stringToFloat( next, Float.MIN_VALUE );
				if ( nextVal > max ) {
					max = nextVal;
				}
			}
		}
        return max; 
	}
	
	protected float getMinvalue( List<String> minvals ) {
		Float min = Float.MAX_VALUE;
		if ( minvals.size() == 0 ) {
			min = Float.MAX_VALUE;
		} else if ( minvals.size() == 1 ) {
			min = stringToFloat( minvals.get( 0 ), Float.MAX_VALUE );	
		} else {
			for ( String next : minvals ) {
				Float nextVal = stringToFloat( next, Float.MAX_VALUE );
				if ( nextVal < min ) {
					min = nextVal;
				}
			}
		}
        return min;
	}
	
	private Float stringToFloat( String val, Float exceptionValue ) {
		Float nextVal = null;
		try{
			nextVal = Float.parseFloat( val );
		} catch ( NumberFormatException e ) {
			nextVal = exceptionValue;
		}
		return nextVal;
	}
}
