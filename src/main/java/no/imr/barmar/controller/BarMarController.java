package no.imr.barmar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import no.imr.barmar.gis.geoserver.UrlConsts;
import no.imr.barmar.gis.wfs.GetWFSList;
import no.imr.barmar.pojo.BarMarPojo;
import no.imr.barmar.pojo.ParameterPojo;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Controller
public class BarMarController {

	private static final Logger logger = LoggerFactory.getLogger(BarMarController.class);
    
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

    @RequestMapping("/barmar2.json")
    public @ResponseBody Map getBarMarJson2( 
            @RequestParam(value = "grid", required=false) String grid, 
            @RequestParam(value="species", required=false) String species,
            @RequestParam(value="subSpecies", required=false) String subSpecie) throws IOException, Exception {
    	return getBarMarJson(grid, species, subSpecie);
    }
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
//        for ( String l : speciesSubgroups ) {
//        	System.out.println(l);
        	logger.error("Cod:"+speciesSubgroups.toString());
//        }
        barMarParameteres.put("species", getSpecies(speciesSubgroups));
        
        if ( species != null) {
            List<String> speciesSubgroup = getSpeciesSubgroups( speciesSubgroups, species);
            logger.error("speciesSubgroup length:"+speciesSubgroup.size());
            
            List<String> sortedSubGroups = sortSubSpecies( speciesSubgroup );
            
            barMarParameteres.put("speciesSubgroup", sortedSubGroups);
            
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

    
    protected List<String> sortSubSpecies( List<String> speciesSubgroup ) {
        Map<ParameterPojo, String> lengthSubgroup = new HashMap<ParameterPojo, String>();
        Map<ParameterPojo, String> ageSubgroup = new HashMap<ParameterPojo, String>();
        List<String> otherSubGroup = new ArrayList<String>();
        
        logger.error("records length:"+speciesSubgroup.size());
        int v = 0;
        for ( String aspecies : speciesSubgroup ) {
        	v++;
        	String dataType = ""; //cm, year or other
        	int indexOfDigitsForType = 0;
        	String[] result = aspecies.split("_");
            for (int x=4; x<result.length; x++) { //start reading length or year from 5th '_'
            	//logger.error("result[x]"+result[x]);
            	if ( result[x].contains("cm") ) {
            		dataType = "cm";
            		indexOfDigitsForType = aspecies.indexOf(result[x]);
//            		break;
            	} else if ( result[x].contains("yr") || result[x].contains("0-group") ) {
            		dataType = "yr";
            		indexOfDigitsForType = aspecies.indexOf(result[x]);
//            		break;
            	} else {
            		logger.error("what record is this?:"+result[x]);
            	}
            }
        	
//            logger.error("record:"+aspecies+" v:"+v);
        	if ( dataType.toLowerCase().contains( "cm" ) ) {
        		int alength = 0;
        		StringBuffer lengthAsStr = new StringBuffer();
        		boolean foundDigit = false;
        		String speciesName = "";
        		for ( int i=indexOfDigitsForType; i < aspecies.length(); i++ ) {
        			if ( Character.isDigit( aspecies.charAt(i) ) ) {
        				if ( foundDigit == false && i > 0) {
            				speciesName = aspecies.substring(0, i-1);
        				}
        					
        				foundDigit = true;
        				lengthAsStr.append( aspecies.charAt(i) );
        			} else if ( foundDigit == true ) {
        				break;
        			}
        		}
        		logger.error("cm:"+aspecies+" v:"+v);
    			alength = new Integer( lengthAsStr.toString() );
    			lengthSubgroup.put(new ParameterPojo(speciesName, alength), aspecies);
        	} else if ( dataType.toLowerCase().contains("yr") || dataType.toLowerCase().contains("0-group")) {
        		int alength = 0;
        		StringBuffer lengthAsStr = new StringBuffer();
        		boolean foundDigit = false;
        		String speciesName = "";
        		for ( int i=0; i < aspecies.length(); i++ ) {
        			if ( Character.isDigit( aspecies.charAt(i) ) ) {
        				if ( foundDigit == false && i > 0) {
            				speciesName = aspecies.substring(0, i-1);
        				}
        				foundDigit = true;
        				lengthAsStr.append( aspecies.charAt(i) );
        			}
        		}
        		logger.error("year:"+aspecies+" v:"+v);
    			alength = new Integer( lengthAsStr.toString() );
    			ageSubgroup.put(new ParameterPojo(speciesName, alength), aspecies);            		
        	} else {
        		logger.error("other:"+aspecies+" v:"+v);
        		otherSubGroup.add(aspecies);
        	}
        }
        logger.error("v:"+v);
        
        List<ParameterPojo> sortedKeys = new ArrayList<ParameterPojo>(lengthSubgroup.keySet());
        Collections.sort(sortedKeys);
        List<String> lengthSorted = new ArrayList<String>();
        for ( ParameterPojo pLength : sortedKeys ) {
        	lengthSorted.add( lengthSubgroup.get(pLength) );
        }

        List<ParameterPojo> sortedKeys2 = new ArrayList<ParameterPojo>(ageSubgroup.keySet());
        Collections.sort(sortedKeys2);
        List<String> ageSorted = new ArrayList<String>();
        for ( ParameterPojo pAge : sortedKeys2 ) {
        	ageSorted.add( ageSubgroup.get(pAge) );
        }
        List<String> sortedSubGroups = new ArrayList<String>();
        
        sortedSubGroups.addAll(lengthSorted);
        sortedSubGroups.addAll(ageSorted);
        sortedSubGroups.addAll(otherSubGroup);
        System.out.println("lengthSorted:"+lengthSorted.size());
        System.out.println("ageSorted:"+ageSorted.size());
        System.out.println("otherSubGroup:"+otherSubGroup.size());
        System.out.println("sortedSubGroups:"+sortedSubGroups.size());
        return sortedSubGroups;
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
//                logger.error("add:"+aSubgroup);
            } 
//            else {
//            	logger.error("!Cod:"+aSubgroup);
//            }
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
