package no.imr.barmar.gis.wfs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.imr.barmar.gis.sld.SLDpojoSelectionRule;
import no.imr.barmar.gis.sld.StAXreader.StAXreaderException;
import no.imr.barmar.pojo.BarMarPojo;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Component
public class GetWFSList {
    
    private static final Logger logger = LoggerFactory.getLogger(GetWFSList.class);

    @Autowired 
    private GetWFSParameterList gfs;
    
    @Autowired
    private SLDpojoSelectionRule sldFromMap;
    
    public GetWFSList() {}
    
    public GetWFSList(GetWFSParameterList gfs, SLDpojoSelectionRule sldFromMap) {
    	this.gfs = gfs;
    	this.sldFromMap = sldFromMap;
    }
    
    private String filterStart = "<ogc:Filter xmlns:ogc=\"http://ogc.org\" xmlns:gml=\"http://www.opengis.net/gml\">";
    private String andOperatorStart = "<ogc:And>";
    private String andOperatorEnd = "</ogc:And>";
    private String likeOperatorStart = "<ogc:PropertyIsLike escape=\"?\" singleChar=\"_\" wildCard=\"*\">";
    private String propertyStart = "<ogc:PropertyName>";
    private String propertyEnd = "</ogc:PropertyName>";
    private String valueStart = "<ogc:Literal>";
    private String valueEnd = "</ogc:Literal>";
    private String likeOperatorEnd = "</ogc:PropertyIsLike>";
    private String filterEnd = "</ogc:Filter>"; 
    
    public List<String> getWFSList(
            String searchProperty,  
            BarMarPojo pojo,
            String mainUrl) throws UnsupportedEncodingException, StAXreaderException, XMLStreamException, IOException {

        String propertyName = "propertyName=" + searchProperty;
        String urlRequest = mainUrl + "&" + propertyName;

        if ( pojo != null ) {
            sldFromMap.setPropertyLikeOp();
            String filter = filterStart;
            filter += sldFromMap.getSelectionRule( pojo);
            filter += andOperatorEnd + filterEnd;
            if (filter != null) {
                filter = URLEncoder.encode(filter, "UTF-8");
                urlRequest = urlRequest + "&filter=" + filter;
            }
        }
        logger.debug("wfs url:"+urlRequest);
        System.out.println("wfs url:"+urlRequest);
        gfs.readXML(urlRequest, searchProperty);
        List<String> params = gfs.getList();
        gfs.deleteList();
        return params;
    }
    
    
    private String createSLD(Map<String, String> inputvalues) {
//      pojoRules.getSelectionRule(norMarSingleDataset, null);

        String filter = null;

        if (inputvalues != null) {
            List<String> keys = new ArrayList<String>(inputvalues.keySet());
            if (keys.size() > 1) {
                filter = filterStart;
                filter += andOperatorStart;
                for (String filter_property : keys) {
                    String value = inputvalues.get(filter_property);
                    filter += likeOperatorStart + propertyStart + filter_property + propertyEnd + valueStart
                            + value + valueEnd + likeOperatorEnd;

                }
                filter += andOperatorEnd;
                filter += filterEnd;
            } else {
                filter = filterStart + likeOperatorStart + propertyStart + keys.get(0) + propertyEnd + valueStart
                        + inputvalues.get(keys.get(0)) + valueEnd + likeOperatorEnd + filterEnd;
            }
        }
        return filter;
    }
}

