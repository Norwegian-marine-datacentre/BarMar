package no.imr.barmar.gis.sld;

import java.util.List;

import no.imr.barmar.pojo.BarMarPojo;

import org.springframework.stereotype.Component;

/**
 * 
 * @author endrem
 */
@Component
public class SLDpojoSelectionRule {
	
	private String likeOperatorStart = "<ogc:PropertyIsLike escape=\"?\" singleChar=\"_\" wildCard=\"*\">";
	private String likeOperatorEnd = "</ogc:PropertyIsLike>";
	private String equalOperator = "<ogc:PropertyIsEqualTo>";
	private String equalOperatorEnd = "</ogc:PropertyIsEqualTo>";
	
	private String propertyStart;
	private String propertyEnd;
	
	public SLDpojoSelectionRule() {
		setPropertyEqualOp();
	}
	
	/**
	 * Default value
	 */
	public void setPropertyEqualOp() {
		propertyStart = equalOperator;
		propertyEnd = equalOperatorEnd;
	}
	
	public void setPropertyLikeOp() {
		propertyStart = likeOperatorStart;
		propertyEnd = likeOperatorEnd;	
	}
	
	public String getSelectionRule(BarMarPojo queryFishEx) {
		String selectionRule = "";
        if ( queryFishEx.getParameter().size() > 1 ) {
        	selectionRule = getAggregateParameterSelectionRule( queryFishEx );
        } else {
        	selectionRule = getSingleParameterSelectionRule( queryFishEx );
        }
        setPropertyEqualOp(); 
        return selectionRule;
	}

    protected String getSingleParameterSelectionRule( BarMarPojo queryPojo) {

        String sRule = "";
        sRule += "<ogc:And>";
        sRule += addSldRule( "gridname", queryPojo.getGrid() );
        sRule += addSldRule( "parametername", queryPojo.getParameter(0) );
        sRule += addAggregateSldRule( "depthlayername", queryPojo.getDepth() );
//        sRule += addSldRule( "periodname", queryPojo.getTime() );
        sRule += addAggregateSldRule( "periodname", queryPojo.getTime());    

        return sRule;
    }
    
    protected String getAggregateParameterSelectionRule( BarMarPojo queryPojo) {

        String sRule = "";
        sRule += "<ogc:And>";
        sRule += addSldRule( "gridname", queryPojo.getGrid() );
        
        sRule += addAggregateSldRule("parametername", queryPojo.getParameter());
//        List<String> paramNames = queryPojo.getParameter();
//        if ( paramNames.size() > 1) {
//        	sRule += "<ogc:Or>";
//        }
//        for ( String parameter : paramNames ) {
//        	sRule += addSldRule( "parametername", parameter );
//        }
//        if ( paramNames.size() > 1) {
//        	sRule +="</ogc:Or>";
//        }
        sRule += addAggregateSldRule( "depthlayername", queryPojo.getDepth() );
//        sRule += addSldRule( "periodname", queryPojo.getTime() );
        sRule += addAggregateSldRule( "periodname", queryPojo.getTime());        

        return sRule;
    } 
    
    private String addSldRule( String propertyName, String propertyValue  ) {
    	String sldRule = "";
    	if ( !propertyValue.equals("") ) {
    		sldRule = propertyStart;
	    	sldRule += "<ogc:PropertyName>"+ propertyName + "</ogc:PropertyName>";
	    	sldRule += "<ogc:Literal>" + propertyValue + "</ogc:Literal>";
	    	sldRule += propertyEnd;
    	}
	    return sldRule;
    }
    
    private String addAggregateSldRule( String propertyName, List<String> propertyValue ) {
    	String sRule = "";
    	
        if ( propertyValue.size() > 1) {
        	sRule += "<ogc:Or>";
        }
        for ( int i=0; i< propertyValue.size(); i++ ) {
        	sRule += addSldRule( propertyName, propertyValue.get(i) );
        }
        if ( propertyValue.size() > 1) {
        	sRule +="</ogc:Or>";
        }
        
    	return sRule;
    }
}
