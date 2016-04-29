package no.imr.barmar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import no.imr.barmar.gis.geoserver.UrlConsts;
import no.imr.barmar.gis.wfs.GetWFSList;
import no.imr.barmar.pojo.BarMarPojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Controller
public class BarMarController {

    
    private String grid;
    private String gridValue;
    private String dataset;
    private String datasetValue;
    private String parameter;
    private String parameterValue;
    
    private final static String GRID = "grid";
    private final static String GRID_VALUE = "grid_value";
    private final static String DATASET = "dataset";
    private final static String DATASET_VALUE = "dataset_value";
    private final static String PARAMETER = "parameter";
    private final static String PARAMETER_VALUE = "parameter_value";
    
    private final static String GRIDS = "grids";
    private final static String DATASETS = "datasets";
    private final static String PARAMETERS = "parameters";
    private final static String METADATA = "metadata";
    
    @Autowired( required = true )
    private GetWFSList gwfs  = null;

    @RequestMapping("/barmar.json")
    public String getMareanoJson() throws IOException {
        return "dummy:json";
    }
    
    /**
     * FILLS THE COMBOBOX FOR GRIDS:
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/parameter.jsp")
    public ModelAndView parameter(HttpServletRequest request) throws Exception {
        
        grid = request.getParameter( GRID );
        gridValue = request.getParameter( GRID_VALUE );
        dataset = request.getParameter( DATASET );
        datasetValue = request.getParameter( DATASET_VALUE );
        parameter = request.getParameter( PARAMETER );
        parameterValue = request.getParameter( PARAMETER_VALUE );
                        
        ModelAndView mav = new ModelAndView("parameters");
        String urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_NAME_BARMAR; 

        List<String> grids = gwfs.getWFSList("gridname", null, urlRequest );
        mav.addObject(GRIDS, grids);

        List<String> speciesSubgroups = getSpeciesSubgroupFromWFSlist( urlRequest );
        addSpecies( mav, speciesSubgroups );
        addSpeciesSubgroups( mav, speciesSubgroups );
        periodDepthlayersAndMetadata( mav, urlRequest, request );
        return mav;
    }
    
    private void addSpecies( ModelAndView mav, List<String> speciesSubgroups ) throws Exception {
        if ( grid != null && gridValue != null ) {
            mav.addObject("grid_value_selected", gridValue );
            List<String> species = new ArrayList<String>();
            for(int i=0; i<speciesSubgroups.size(); i++) {
                String speciesName = getWFSSpeciesName( speciesSubgroups, i );
                if(!species.contains( speciesName )){
                    species.add( speciesName );
                }
            }
            mav.addObject(DATASETS, species);
        }
    }
    
    private void addSpeciesSubgroups( ModelAndView mav, List<String> speciesSubgroups ) throws Exception {
        if ( dataset != null ) {
            mav.addObject("dataset_value_selected", datasetValue);
            List <String> thisSpeciesSubgroup = new ArrayList<String>();
            for(int i=0; i<speciesSubgroups.size(); i++) {
                String speciesName = getWFSSpeciesName( speciesSubgroups, i );
                String speciesChoosen = datasetValue;
                if (speciesChoosen.equalsIgnoreCase( speciesName ) && !thisSpeciesSubgroup.contains( speciesName)) {
                    thisSpeciesSubgroup.add(speciesSubgroups.get(i));
                }
            }
            mav.addObject(PARAMETERS, thisSpeciesSubgroup);
        }
    }
    
    private List<String> getSpeciesSubgroupFromWFSlist( String urlRequest ) throws Exception {
        BarMarPojo pojo = new BarMarPojo(gridValue, "", new ArrayList<String>(0), new ArrayList<String>(0));
        return gwfs.getWFSList( "parametername", pojo, urlRequest );
    }
    
    private String getWFSSpeciesName( List<String> params, int i ) {
        String WFSSpeciesName = params.get( i );
        String[] theSpeciesNameWithAllSylables = WFSSpeciesName.split( "_" );
        String theFirstNameOfSpecie = theSpeciesNameWithAllSylables[0];
        return theFirstNameOfSpecie.toLowerCase();
    }
    
    private void periodDepthlayersAndMetadata( ModelAndView mav, String urlRequest, HttpServletRequest request ) throws Exception {
        if ( parameter != null ) {
            mav.addObject( "parameter_value_selected", parameterValue );
            BarMarPojo pojo = new BarMarPojo(gridValue, parameterValue, new ArrayList<String>(0),new ArrayList<String>(0));

            List<String> descriptions = gwfs.getWFSList("description", pojo, urlRequest);
            if ( descriptions.size() > 0 ) {
                mav.addObject(METADATA,descriptions.get(0));
                request.getSession().setAttribute(METADATA, descriptions.get(0));
            } else {
                request.getSession().setAttribute( METADATA, "Fant ingen metadata" );
            }
            
            urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_TIME;
            List<String> params = gwfs.getWFSList( "periodname", pojo, urlRequest );
            mav.addObject("periods", params);

            urlRequest = UrlConsts.BASE_URL_REQUEST + UrlConsts.GRID_PARAMETER_DEPTH;
            params = gwfs.getWFSList( "layername", pojo, urlRequest );
            mav.addObject("depthlayers", params);
        }
    }
}
