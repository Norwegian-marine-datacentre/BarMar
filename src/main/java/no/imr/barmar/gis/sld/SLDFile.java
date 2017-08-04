package no.imr.barmar.gis.sld;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.core.net.SyslogOutputStream;
import no.imr.barmar.ajax.controller.SortSubSpecies;
import no.imr.barmar.gis.sld.color.HSVtoRGB;
import no.imr.barmar.pojo.BarMarPojo;

/**
 * @author trondwe
 * @author endrem
 */
@Component
public class SLDFile {
	
	private static final int SCALE_ROUNDING = 1;
	
    private String sSLDFile = null;
    
    private String lowerBoundColor = "#999999"; // gray color
    
    @Autowired(required=true)
    protected LayerNameForSLD sldName; 
    
    @Autowired(required=true)
    protected SLDpojoSelectionRule singleOrAggregateSelectionRule;
    	
    public SLDFile(LayerNameForSLD sldName, SLDpojoSelectionRule singleOrAggregateSelectionRule) {
    	this.sldName = sldName;
    	this.singleOrAggregateSelectionRule = singleOrAggregateSelectionRule;
    }
    public SLDFile(){}
    
    /**
     * 
     * @param queryFishEx - assume object is null safe
     * @param areadisplay
     * @param parameterNames
     * @return
     */
    public String getSLDFile( BarMarPojo queryFishEx, Boolean areadisplay, String hueColor, boolean logarithmicScale ) {
    		
    	
        // List<Color> theColors = HSVtoRGB.makeColorScale(180.0f, Integer.parseInt("10"));

        sSLDFile = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        sSLDFile += "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd\">";
        sSLDFile += "<NamedLayer>";
        
        String layername = sldName.getLayerName(queryFishEx, areadisplay );
        sSLDFile += "<Name>"+layername+"</Name>";

        sSLDFile += getUserStyle(areadisplay, queryFishEx, hueColor, logarithmicScale );
        sSLDFile += "</NamedLayer>";
        sSLDFile += "</StyledLayerDescriptor>";
        
        return sSLDFile;
    }

    protected String getUserStyle(Boolean areadisplay, BarMarPojo queryFishEx, String hueColor, boolean logarithmicScale ) {

        String sUserStyle = "<UserStyle>";
        sUserStyle += "<FeatureTypeStyle>";
    	String firstParameter = queryFishEx.getParameter().get(0);
    	if ( !firstParameter.contains("Temperature") && !firstParameter.contains("Salinity") ) {
    		sUserStyle += getZeroRulePointOrAreaDisplay( queryFishEx, areadisplay );
    		if ( queryFishEx.getMinLegend() == 0.0f ) {
    			queryFishEx.setMinLegend( 0.1f );
    		}
    	}        
        if (areadisplay) {
            // HUE is between 0 and 360 according to wikipedia,
            // BUT java.lang color MAPS this to a value between 0 and 1
            // Number of steps is here set to 4 - this number may increase
            // when the SLD_BODY text is taken from a file instead
            // the buffer of the http-request is limited to a few thousand bytes
            sUserStyle += getColorRules( 0.3f, 10, queryFishEx, logarithmicScale );
        } else { // pointdisplay
            sUserStyle += getSteppedSizeRulePoints( 3, 2, 10, queryFishEx, hueColor, logarithmicScale );
        }
        sUserStyle += "</FeatureTypeStyle>";
        sUserStyle += "</UserStyle>";
        return sUserStyle;
    }

    protected String getSteppedSizeRulePoints(Integer minsymbolsize, Integer stepsymbolsize, Integer nstep, BarMarPojo queryFishEx, String hueColor, boolean logarithmicScale  ) {
        String stepRules = "";
        List<List<Float>> valueranges = legendRange( queryFishEx.getMinLegend(), queryFishEx.getMaxLegend(), nstep, logarithmicScale );
        Integer intervalsymbolsize = 0;
        Integer istep = -1;
        
        for (List<Float> valuerange : valueranges) {
            istep++;
            intervalsymbolsize = minsymbolsize + stepsymbolsize * istep;
            stepRules += getStepSizeRulePoint(
            		new BigDecimal( valuerange.get(0).toString() ).setScale(SCALE_ROUNDING, RoundingMode.HALF_UP).toPlainString(),
            		new BigDecimal( valuerange.get(1).toString() ).setScale(SCALE_ROUNDING, RoundingMode.HALF_UP).toPlainString(),
        			intervalsymbolsize.toString(), queryFishEx, hueColor  );
        }
        return stepRules;
    }

    protected String getStepSizeRulePoint( String minLocal, String maxLocal, String symbolsize, BarMarPojo queryFishEx, String hueColor ) {
        String sRule = "<Rule>";
        sRule += "<Title>" + minLocal + "-" + maxLocal + "</Title>";
        sRule += "<ogc:Filter>";
        
        sRule += singleOrAggregateSelectionRule.getSelectionRule( queryFishEx );
        
        sRule += "<ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + minLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyIsLessThan>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + maxLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsLessThan>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PointSymbolizer>";
        sRule += "<Graphic>";
        sRule += "<Mark>";
        sRule += "<WellKnownName>circle</WellKnownName>";
        sRule += "<Fill>";
        if (hueColor != null) {
        	sRule += "<CssParameter name=\"fill\">" + hueColor + "</CssParameter>"; 
        } else {
        sRule += "<CssParameter name=\"fill\">#FF0000</CssParameter>"; //RED
        }        
        sRule += "</Fill>";
        sRule += "</Mark>";
        sRule += "<Size>" + symbolsize + "</Size>";
        sRule += "</Graphic>";
        sRule += "</PointSymbolizer>";
        sRule += "</Rule>";
        return sRule;
    }

    protected String getZeroRulePointOrAreaDisplay( BarMarPojo queryFishEx, boolean areaDisplay ) {
        String sRule = "<Rule>";
        sRule += "<Title>0 - Zero</Title>";
        sRule += "<ogc:Filter>";
        
        sRule += singleOrAggregateSelectionRule.getSelectionRule( queryFishEx );
        
        sRule += "<ogc:PropertyIsEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>0</ogc:Literal>";
        sRule += "</ogc:PropertyIsEqualTo>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        
        if ( areaDisplay ) {
            sRule += "<PolygonSymbolizer>";
            sRule += "<Fill>";
            sRule += "<CssParameter name=\"fill\">"+lowerBoundColor+"</CssParameter>"; // Grey color
            sRule += "</Fill>";
            sRule += "</PolygonSymbolizer>"; 
        } else { // pointDisplay
	        sRule += "<PointSymbolizer>";
	        sRule += "<Graphic>";
	        sRule += "<Mark>";
	        sRule += "<WellKnownName>circle</WellKnownName>";
	        sRule += "<Fill>";
	        sRule += "<CssParameter name=\"fill\">"+lowerBoundColor+"</CssParameter>"; // Grey color
	        sRule += "</Fill>";
	        sRule += "</Mark>";
	        sRule += "<Size>3</Size>";
	        sRule += "</Graphic>";
	        sRule += "</PointSymbolizer>";
        }
        sRule += "</Rule>";
        return sRule;
    }

    protected String getColorRules(float hue, Integer nstep, BarMarPojo queryFishEx, boolean logarithmicScale ) {
        
    	String colorRules = "";
        List<String> colors  = HSVtoRGB.makeHexColorScaleFromColorToColor(0.6f, 0.9f);
        List<List<Float>> valueranges = legendRange(queryFishEx.getMinLegend(), queryFishEx.getMaxLegend(), nstep, logarithmicScale);

        int i = 0;
        for (List<Float> valuerange : valueranges) {
        	if (i + 1 == valueranges.size() ) {
                colorRules += getColorRuleLastElement(
                		new BigDecimal( valuerange.get(0) ).setScale(SCALE_ROUNDING, RoundingMode.HALF_UP).toPlainString(), 
                		new BigDecimal( valuerange.get(1) ).setScale(SCALE_ROUNDING, RoundingMode.HALF_UP).toPlainString(),  
                		colors.get(i++), queryFishEx);
        	} else {
        		colorRules += getColorRule(
                		new BigDecimal( valuerange.get(0) ).setScale(SCALE_ROUNDING, RoundingMode.HALF_UP).toPlainString(), 
                		new BigDecimal( valuerange.get(1) ).setScale(SCALE_ROUNDING, RoundingMode.HALF_UP).toPlainString(), 
        				colors.get(i++), queryFishEx);
        	}
        }
        return colorRules;
    }

    protected String getColorRule( String minLocal, String maxLocal, String hexcolor, BarMarPojo queryFishEx ) {

        String sRule = "<Rule>";
        sRule += "<Title>" + minLocal + "-" + maxLocal + "</Title>";
        sRule += "<ogc:Filter>";
        
        sRule += singleOrAggregateSelectionRule.getSelectionRule( queryFishEx );
        
        sRule += "<ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + minLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyIsLessThan>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + maxLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsLessThan>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PolygonSymbolizer>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#" + hexcolor + "</CssParameter>";
        sRule += "</Fill>";
        sRule += "</PolygonSymbolizer>";
        sRule += "</Rule>";
        return sRule;
    }

    protected String getColorRuleLastElement( String minLocal, String maxLocal, String hexcolor, BarMarPojo queryFishEx ) {

        String sRule = "<Rule>";
        sRule += "<Title>" + minLocal + "-" + maxLocal + "</Title>";
        sRule += "<ogc:Filter>";
        
        sRule += singleOrAggregateSelectionRule.getSelectionRule( queryFishEx );
        
        sRule += "<ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + minLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsGreaterThanOrEqualTo>";
        sRule += "<ogc:PropertyIsLessThanOrEqualTo>";
        sRule += "<ogc:PropertyName>value</ogc:PropertyName>";
        sRule += "<ogc:Literal>" + maxLocal + "</ogc:Literal>";
        sRule += "</ogc:PropertyIsLessThanOrEqualTo>";
        sRule += "</ogc:And>";
        sRule += "</ogc:Filter>";
        sRule += "<PolygonSymbolizer>";
        sRule += "<Fill>";
        sRule += "<CssParameter name=\"fill\">#" + hexcolor + "</CssParameter>";
        sRule += "</Fill>";
        sRule += "</PolygonSymbolizer>";
        sRule += "</Rule>";
        return sRule;
    }
    
    protected List<List<Float>> legendRange(Float minvalue, Float maxvalue, Integer nstep, boolean logarithmicScale) {

        List<List<Float>> thelist = new ArrayList<List<Float>>();

        if ( logarithmicScale ) {
        	float maxLn = (float)Math.log( maxvalue );
        	float lnStep = maxLn / nstep;       	
        	float bottomRange = minvalue;
        	for ( int i =0; i < nstep; i++) {
        		List<Float> range = new ArrayList<Float>();
        		range.add( bottomRange );
        		if ( i+1 == nstep ) {
        			range.add(maxvalue);
        		} else {
        			bottomRange = (float)Math.exp( lnStep * (i+1) ); 
        			range.add( bottomRange );
        		}        		
        		thelist.add(range);
        	}
        } else {
            Float step = (maxvalue - minvalue) / nstep;            
		    Float value = minvalue;
		    for (int i = 0; i < nstep; i++) {
		    	List<Float> range = new ArrayList<Float>();
		    	range.add(value);
		    	if ( i+1 == nstep ) { //make sure last step includes maxvalue
		            range.add(maxvalue);
		    	} else {
		    		range.add(value + step);
		    	}
				thelist.add(range);
				value = value + step;
		    }
        }
        return thelist;
    }
}
