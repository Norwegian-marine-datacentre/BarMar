package no.imr.barmar.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.imr.barmar.geoserver.UrlConsts;

@Component
public class GetMapHelper {
	
	@Autowired
	private UrlConsts urlConsts;
	
    /*
     * Example layer and viewparams:
     * layers=barmarPointvalueAggregate&
     * viewparams=agridname:'BarMar';parameter_ids:'300';depthlayername:'F';periodname:'Y2004';aggregationfunc:'avg'
	*/
    protected String createFishExchangeLayer(
    		Integer width, Integer height, String bbox, String layer, String sld, String viewparams) 
    				throws UnsupportedEncodingException {

    	String fishEx = urlConsts.getMainUrl(); 
    	fishEx = fishEx.concat("REQUEST=GetMap");
    	fishEx = fishEx.concat("&SERVICE=WMS");
    	fishEx = fishEx.concat("&FORMAT=image/png");
    	fishEx = fishEx.concat("&CRS=EPSG:3575");
        //fishEx = fishEx.concat("&srs=" + request.getParameter("srs"));
    	fishEx = fishEx.concat("&transparent=true");
        fishEx = fishEx.concat("&version=1.3.0");
        fishEx = fishEx.concat("&width=" + width);
        fishEx = fishEx.concat("&height=" + height);
        fishEx = fishEx.concat("&SLD=" + sld);
        fishEx = fishEx.concat("&bbox=" + bbox);
        fishEx = fishEx.concat("&layers=").concat(layer);
        
        String viewparamsEncoded = URLEncoder.encode(viewparams, "UTF-8");
        fishEx = fishEx.concat("&viewparams=").concat(viewparamsEncoded);        
        return fishEx;
    }
    
    protected String createBaseLayerUrl(Integer width, Integer height, String bbox) {

		String url = "http://wms.geonorge.no/skwms1/wms.europa?" + 
			"VERSION=1.1.1" + 
			"&SERVICE=WMS" + 
			"&REQUEST=GetMap" + 
			"&SRS=EPSG:3575" + 
			"&STYLES=" + 
			"&TRANSPARENT=true" + 
			"&FORMAT=image/png" + 
			"&LAYERS=Land,Vmap0Land,Vmap0Kystkontur,Vmap0Hoydepunkt,Vmap0Elver,Vmap0Hoydekontur,Vmap0MyrSump,Vmap0Innsjo,Vmap0Sletteland,Vmap0Dyrketmark,Vmap0Skog,Vmap0Bebyggelse,Vmap0AdministrativeGrenser,Vmap0Isbre" + 
			"&WIDTH=" + width +
			"&HEIGHT=" + height + 
			"&BBOX=" + bbox;
		return url;
	}
	
    protected String createFishExchangeLegend( String layer, String sld ) {
//      String legendUrl = "http://maps.imr.no/geoserver/wms?service=WMS&version=1.1.1&request=GetLegendGraphic&layer=test:pointvalue&width=22&height=24&format=image/png";
//      String legendUrl = UrlConsts.MAIN_URL_WMS + UrlConsts.TYPE + "layer=test:pointvalue" + UrlConsts.TYPE2;
      String legendUrl = urlConsts.getMainUrl() + "service=WMS&version=1.1.1&request=GetLegendGraphic&" + "layer=" + layer + "&width=22&height=24&format=image/png";
      legendUrl = legendUrl.concat("&SLD=" + sld);
      return legendUrl;
    }
}
