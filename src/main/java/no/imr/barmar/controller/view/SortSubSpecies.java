package no.imr.barmar.controller.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SortSubSpecies {
	
	private static final Logger logger = LoggerFactory.getLogger(SortSubSpecies.class);

    protected ParameterGroup groupSubSpeciesIntoLengthAgeOther( List<Parameter> speciesSubgroup ) {
    	
        List<Parameter> lengthSubgroup = new ArrayList<Parameter>();
        List<Parameter> ageSubgroup = new ArrayList<Parameter>();
        List<Parameter> otherSubGroup = new ArrayList<Parameter>();
        
        for ( int j=0; j < speciesSubgroup.size(); j++) {
        	Parameter param = speciesSubgroup.get(j);
        	String aspecies = param.getName();

        	String dataType = "";   // cm, year or other
        	int indexOfDigitsForType = 0;
        	String[] result = aspecies.split("_");
            for (int x=4; x<result.length; x++) { //start reading length or year from 5th '_'
            	if ( result[x].contains("cm") ) {
            		dataType = "cm";
            		indexOfDigitsForType = aspecies.indexOf(result[x]);
            		param.setNameSubstringNotContainingDigit(param.getName().substring(0,indexOfDigitsForType));
            		break;
            	} else if ( result[x].contains("yr") || result[x].contains("0-group") ) {
            		dataType = "yr";
            		indexOfDigitsForType = aspecies.indexOf(result[x]);
            		param.setNameSubstringNotContainingDigit(param.getName().substring(0,indexOfDigitsForType));
            		break;
            	} else {
            		logger.error("Undefined record type not added to GUI:"+result[x]);
            	}
            }
        	
        	if ( dataType.toLowerCase().contains( "cm" ) ) {
        		int alength = 0;
        		StringBuffer lengthAsStr = new StringBuffer();
        		boolean foundDigit = false;
        		for ( int i=indexOfDigitsForType; i < aspecies.length(); i++ ) {
        			if ( Character.isDigit( aspecies.charAt(i) ) ) {
        				foundDigit = true;
        				lengthAsStr.append( aspecies.charAt(i) );
        			} else if ( foundDigit == true ) {
        				break;
        			}
        		}
    			alength = new Integer( lengthAsStr.toString() );
    			param.setYearOrLength(alength);
    			lengthSubgroup.add( param );
        	} else if ( dataType.toLowerCase().contains("yr") || dataType.toLowerCase().contains("0-group")) {
        		int alength = 0;
        		StringBuffer lengthAsStr = new StringBuffer();
        		boolean foundDigit = false;
        		for ( int i=0; i < aspecies.length(); i++ ) {
        			if ( Character.isDigit( aspecies.charAt(i) ) ) {
        				foundDigit = true;
        				lengthAsStr.append( aspecies.charAt(i) );
        			} else if ( foundDigit == true ) {
        				break;
        			}
        		}
    			alength = new Integer( lengthAsStr.toString() );
    			param.setYearOrLength(alength);
    			ageSubgroup.add( param );
        	} else {
        		otherSubGroup.add( param );
        	}
        }
        Collections.sort( lengthSubgroup );
        Collections.sort( ageSubgroup );
        
        ParameterGroup pg = new ParameterGroup();
        pg.setLength( lengthSubgroup );
        pg.setAge( ageSubgroup );
        pg.setOther( otherSubGroup );
        
        return pg;
    }
}
