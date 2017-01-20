package no.imr.barmar.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


/**
 * 
 * @author endrem
 */

public class BarMarControllerTest {
    
    @Test
    public void testSortSubSpecies() {
    	String[] cods = new String[]{"Cod_survey_trawl_Svalbard_100-104cm", "Cod_survey_trawl_winter_100-104cm"};
    	List<String> sortedSubspecies = Arrays.asList(cods);
    	
    	BarMarController b = new BarMarController();
    	b.sortSubSpecies( sortedSubspecies );
    }
    
}
