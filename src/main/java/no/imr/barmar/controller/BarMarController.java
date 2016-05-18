package no.imr.barmar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import no.imr.barmar.gis.geoserver.UrlConsts;
import no.imr.barmar.gis.wfs.GetWFSList;
import no.imr.barmar.pojo.BarMarPojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Controller
public class BarMarController {

    
//    private String grid;
//    private String gridValue;
//    private String dataset;
//    private String datasetValue;
//    private String parameter;
//    private String parameterValue;
//    
//    private final static String GRID = "grid";
//    private final static String GRID_VALUE = "grid_value";
//    private final static String DATASET = "dataset";
//    private final static String DATASET_VALUE = "dataset_value";
//    private final static String PARAMETER = "parameter";
//    private final static String PARAMETER_VALUE = "parameter_value";
//    
//    private final static String GRIDS = "grids";
//    private final static String DATASETS = "datasets";
//    private final static String PARAMETERS = "parameters";
//    private final static String METADATA = "metadata";
    
    private final static String urlRequestParameterName = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_NAME_BARMAR;
    private final static String urlRequestMetadata = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_NAME_BARMAR;
    private final static String urlRequestPeriodName = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_TIME;;
    private final static String urlRequestDepthName = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_DEPTH;
    
    @Autowired( required = true )
    private GetWFSList gwfs  = null;

    /**
     * FILLS THE COMBOBOX FOR GRIDS:
     * @param grid
     * @param species
     * @param subSpecie
     * @return
     * @throws IOException
     * @throws Exception
     */
    @RequestMapping("/barmar.json")
    public @ResponseBody Map getBarMarJson( 
            @RequestParam(value = "grid", required=false) String grid, 
            @RequestParam(value="species", required=false) String species,
            @RequestParam(value="subSpecies", required=false) String subSpecie) throws IOException, Exception {
        
        Map<String, Object> barMarParameteres = new HashMap<String, Object>(1); //object is List<String> or String
        
        List<String> grids = gwfs.getWFSList("gridname", null, urlRequestParameterName );
        barMarParameteres.put("grid", grids);
        
        List<String> speciesSubgroups = getSpeciesSubgroupFromWFSlist( grid, urlRequestParameterName );        
        barMarParameteres.put("species", getSpecies(speciesSubgroups));
        
        if ( species != null) {
            List<String> speciesSubgroup = getSpeciesSubgroups( speciesSubgroups, species);
            barMarParameteres.put("speciesSubgroup", speciesSubgroup);
        }
        
        if ( subSpecie != null) {
            BarMarPojo pojo = new BarMarPojo(grid, subSpecie, new ArrayList<String>(0),new ArrayList<String>(0));

            List<String> descriptions = gwfs.getWFSList("description", pojo, urlRequestMetadata);
            if ( descriptions.size() > 0 ) {
                barMarParameteres.put("metadata", descriptions.get(0));
            } else {
                barMarParameteres.put("metadata", "No metadata found for selected parameteres");
            }
            
            List<String> period = gwfs.getWFSList( "periodname", pojo, urlRequestPeriodName );
            barMarParameteres.put("periods", period);

            List<String> depths = gwfs.getWFSList( "layername", pojo, urlRequestDepthName );
            barMarParameteres.put("depth", depths);
        }
        return barMarParameteres;
    }
   
    
    /**
     * FILLS THE COMBOBOX FOR GRIDS:
     * @param request
     * @return
     * @throws Exception
     */
//    @RequestMapping("/barmarParameters.json")
//    public ModelAndView  parameter(HttpServletRequest request) throws Exception {
//        
//        grid = request.getParameter( GRID );
//        gridValue = request.getParameter( GRID_VALUE );
//        dataset = request.getParameter( DATASET );
//        datasetValue = request.getParameter( DATASET_VALUE );
//        parameter = request.getParameter( PARAMETER );
//        parameterValue = request.getParameter( PARAMETER_VALUE );
//                        
//        ModelAndView mav = new ModelAndView("parameters");
//        String urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_NAME_BARMAR; 
//
//        List<String> grids = gwfs.getWFSList("gridname", null, urlRequest );
//        mav.addObject(GRIDS, grids);
//
//        List<String> speciesSubgroups = getSpeciesSubgroupFromWFSlist( urlRequest );
//        addSpecies( mav, speciesSubgroups );
//        addSpeciesSubgroups( mav, speciesSubgroups );
//        periodDepthlayersAndMetadata( mav, urlRequest, request );
//        return mav;
//    }
    
    private List<String> getSpecies( List<String> speciesSubgroups ) throws Exception {
        List<String> species = new ArrayList<String>();
        for(int i=0; i<speciesSubgroups.size(); i++) {
            String speciesName = getWFSSpeciesName( speciesSubgroups, i );
            if(!species.contains( speciesName )){
                species.add( speciesName );
            }
        }
        return species;
    }
    
    private List<String> getSpeciesSubgroups( List<String> speciesSubgroups, String speciesPrefix ) throws Exception {
        List <String> thisSpeciesSubgroup = new ArrayList<String>();
        for( String aSubgroup : speciesSubgroups ) {
            if ( aSubgroup.startsWith(speciesPrefix)) {
                thisSpeciesSubgroup.add( aSubgroup );
            }
        }
        return thisSpeciesSubgroup;
    }
    
    private List<String> getSpeciesSubgroupFromWFSlist( String gridValue, String urlRequest ) throws Exception {
        BarMarPojo pojo = new BarMarPojo(gridValue, "", new ArrayList<String>(0), new ArrayList<String>(0));
        return gwfs.getWFSList( "parametername", pojo, urlRequest );
    }
    
    private String getWFSSpeciesName( List<String> params, int i ) {
        String WFSSpeciesName = params.get( i );
        String[] theSpeciesNameWithAllSylables = WFSSpeciesName.split( "_" );
        String theFirstNameOfSpecie = theSpeciesNameWithAllSylables[0];
        return theFirstNameOfSpecie;
    }
    
//    private void periodDepthlayersAndMetadata( ModelAndView mav, String urlRequest, HttpServletRequest request ) throws Exception {
//        if ( parameter != null ) {
//            mav.addObject( "parameter_value_selected", parameterValue );
//            BarMarPojo pojo = new BarMarPojo(gridValue, parameterValue, new ArrayList<String>(0),new ArrayList<String>(0));
//
//            List<String> descriptions = gwfs.getWFSList("description", pojo, urlRequest);
//            if ( descriptions.size() > 0 ) {
//                mav.addObject(METADATA,descriptions.get(0));
//                request.getSession().setAttribute(METADATA, descriptions.get(0));
//            } else {
//                request.getSession().setAttribute( METADATA, "No metadata found for selected parameteres" );
//            }
//            
//            urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_TIME;
//            List<String> params = gwfs.getWFSList( "periodname", pojo, urlRequest );
//            mav.addObject("periods", params);
//
//            urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_DEPTH;
//            params = gwfs.getWFSList( "layername", pojo, urlRequest );
//            mav.addObject("depthlayers", params);
//        }
//    }
}
