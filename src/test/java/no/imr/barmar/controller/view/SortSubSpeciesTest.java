package no.imr.barmar.controller.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * 
 * @author endrem
 */

public class SortSubSpeciesTest {
	
	private SortSubSpecies s = new SortSubSpecies(); 
    
    @Test
    public void testSortSubSpecies() {

    	Parameter p = new Parameter();
    	p.setName("Cod_survey_trawl_Svalbard_0-4cm");
    	Parameter p1 = new Parameter();
    	p1.setName("Cod_survey_trawl_Svalbard_15-19cm");    	
    	Parameter p2 = new Parameter();
    	p2.setName("Cod_survey_trawl_winter_100-104cm");
    	List<Parameter> sortedSubspecies = new ArrayList<Parameter>();
    	sortedSubspecies.add(p);
    	sortedSubspecies.add(p1);
    	sortedSubspecies.add(p2);
    	
    	ParameterGroup pg = s.groupSubSpeciesIntoLengthAgeOther( sortedSubspecies );
    	System.out.println("pg:"+pg.toString());
    }
    
}
