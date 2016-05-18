package no.imr.barmar.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * 
 * @author endrem
 */
public class BarMarControllerTest {

    @Autowired( required = true )
    private BarMarController barmarController;
    
    @Test
    public void getUnitTest() {
        return;
    }
    
//    @Test
    public void getGrids() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setParameter("grid", "gridname");
//      req.setParameter("grid_value", "FishExChange");
        req.setParameter("grid_value", "BarMar");
        Map mav = barmarController.getBarMarJson("BarMar", "Cod", "");
        assertsForGridAndSpecies( mav );
    }
    
//    @SuppressWarnings("unchecked")
//    @Test
    public void getSpeciesSubgroupForCod() throws Exception {
//        MockHttpServletRequest req = new MockHttpServletRequest();
//        req.setParameter("grid", "gridname");
//        req.setParameter("grid_value", "BarMar");
//        req.setParameter("dataset", "datasetname");
//        req.setParameter("dataset_value", "cod");
        Map mav = barmarController.getBarMarJson("BarMar", "Cod", "");
        
        assertsForGridAndSpecies( mav );
        
        String datasets_value = (String) mav.get( "dataset_value_selected" );
        assertEquals( "cod", datasets_value );
        
        List<String> parameters = (List<String>) mav.get( "parameters" );
        for ( String speciesSubgroup : parameters ) {
//          System.out.println(speciesSubgroup);
            assertTrue( speciesSubgroup.toLowerCase().contains( "cod" ) );
        }
    }
    
//    @Test
    public void getParameterdummy() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setParameter("grid", "gridname");
        req.setParameter("grid_value", "FishExChange");
//      req.setParameter("grid_value", "BarMar");
//        ModelAndView mav = barmarController.parameterdummy( req );
//        Map<String, Object> mavMap = mav.getModel();
//        assertsForGridAndSpecies( mavMap );     
    }
    
    @SuppressWarnings(value="unchecked")
    private void assertsForGridAndSpecies( Map<String, Object> mavMap ) {
        List<String> grids = (List<String>) mavMap.get("grids");
        assertNotNull( grids );
        
        String grid_value_selected = (String) mavMap.get("grid_value_selected");
        assertEquals( "BarMar", grid_value_selected );
        
        List<String> datasets = (List<String>) mavMap.get("datasets");
        for ( String aSpecies : datasets ) {
//          System.out.println(aSpecies);
            assertNotNull( aSpecies );
        }
    }
}
