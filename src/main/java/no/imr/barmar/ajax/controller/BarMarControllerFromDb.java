package no.imr.barmar.ajax.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import no.imr.barmar.ajax.dao.ParameterDao;
import no.imr.barmar.ajax.parameters.pojo.Metadata;
import no.imr.barmar.ajax.parameters.pojo.Parameter;
import no.imr.barmar.ajax.parameters.pojo.ParameterGroup;
import no.imr.barmar.ajax.pojo.DownloadPojo;
import no.imr.barmar.pojo.BarMarPojo;

@Controller
public class BarMarControllerFromDb {

	@Autowired( required = true )
	public SortSubSpecies sortSubSpecies;
	
	@Autowired
	private ParameterDao dao = null;

	private Map<String, Object> barMarParameteres = null;
	private Map<String, Metadata> metadataList = null;
	
	public ParameterDao getParameterDao(String gridName) {
		if ( dao.getJdbcTemplate() == null ) {
			dao.setDataSource( gridName );
		} 
		return dao;
	}
	
	public void setParameterDao( ParameterDao dao) {
		this.dao = dao;
	}
	
	public String getMetadataRef( String metadataRef ) {
		Metadata metadata =  metadataList.get( metadataRef );
		if ( metadata != null ) {
			return metadata.getMetadata();	
		}
		return metadataRef + " not found.";
	}

    @RequestMapping("/barmarDb.update")
    public ModelAndView updateBarMarJsonFromDb( 
            @RequestParam(value = "grid", required=false) String grid) throws IOException, Exception {
    	
    	barMarParameteres = null;
    	return new ModelAndView("redirect:/BarMar/barmar.html");
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
    @RequestMapping("/barmarDb.json")
    public @ResponseBody Map<String, Object> getBarMarJsonFromDb( 
            @RequestParam(value = "grid", required=false) String grid) throws IOException, Exception {
        
    	if ( barMarParameteres == null ) {
    		barMarParameteres = new HashMap<String, Object>(900); //900 current num of parameters
    		
    		metadataList = new HashMap<String, Metadata>();
	        List<Parameter> params = getParameterDao(grid).getAllParametersAndMetadata( grid, metadataList );
	        for ( String metadataRef : metadataList.keySet() ) {
	        	barMarParameteres.put( metadataRef, metadataList.get(metadataRef) );
	        }
	        
	        List<String> speciesStr = new ArrayList<String>(30);
	        Map<String, List<Parameter>> speciesMap = new HashMap<String, List<Parameter>>();
        	for ( Parameter param : params ) {
        		String name = param.getName();
        		int end = name.indexOf( "_" );
        		if ( end > 0 ) {
		            String firstUnderscore = param.getName().substring(0, end);
		            if ( !speciesStr.contains( firstUnderscore ) ) {
		            	speciesStr.add(firstUnderscore);
		            }
	            	List<Parameter> subspecies = (List<Parameter>) speciesMap.get(firstUnderscore);
	            	if ( subspecies == null ) {
	            		subspecies = new ArrayList<Parameter>();
	            		subspecies.add(param);
	            		speciesMap.put(firstUnderscore, subspecies);
	            	} else {
	            		subspecies.add( param );
	            	}
        		}
            }
        	
        	Map<String, String> sea2DataMappingNames = null;
        	if ( grid.equals( "BarMar" ) ) {
        		sea2DataMappingNames= getParameterDao(grid).getNewParameterMap();
        	} else { // for NorMar
        		sea2DataMappingNames = new HashMap<String, String>();
        		for ( String speciesName : speciesStr) {
        			List<Parameter> paramsForGivenSpecies = speciesMap.get(speciesName);
        			for ( Parameter p : paramsForGivenSpecies) {
        				sea2DataMappingNames.put( p.getName(), p.getName() );
        			}
        		}
        	}
        	List<String> sea2DataSpeciesStr = new ArrayList<String>();
        	barMarParameteres.put( "tooltipMap", sea2DataMappingNames );
        	for ( String speciesName : speciesStr) {
        		List<Parameter> paramsForGivenSpecies = speciesMap.get(speciesName);
        		List<Parameter> paramsForSea2Data = new ArrayList<Parameter>();
        		for ( Parameter p : paramsForGivenSpecies ) {
        			for (String basename : sea2DataMappingNames.keySet()) {
        				if ( p.getName().startsWith( basename ) && !sea2DataMappingNames.get( basename ).equals("")) {
        					//System.out.println(p.getName());
        					paramsForSea2Data.add(p);
        		            if ( !sea2DataSpeciesStr.contains( speciesName ) ) {
        		            	sea2DataSpeciesStr.add(speciesName);
        		            	//System.out.println("spescies list:"+speciesName);
        		            }
        				}
        			}
        		}
        		ParameterGroup pg = sortSubSpecies.groupSubSpeciesIntoLengthAgeOther( paramsForSea2Data );
        		barMarParameteres.put(speciesName, pg);
        	}
        	barMarParameteres.put( "species", sea2DataSpeciesStr /*speciesStr*/ );
    	}
        return barMarParameteres;
    }
    
    @RequestMapping("/downloadData")
//    @Secured({"IMR_USER"})
    public void downloadRecords(
    		@RequestParam("grid") String grid,
    		@RequestParam("parameter[]") String[] parameters,
    		@RequestParam("time[]") String[] time, 
    		@RequestParam("depth[]") String[] depth,
    		HttpServletResponse response) throws Exception {
    	
    	BarMarPojo pojo = new BarMarPojo();
    	pojo.setGrid(grid);
    	pojo.setParameter(Arrays.asList(parameters));
    	pojo.setTime( Arrays.asList(time));
    	pojo.setDepth( Arrays.asList(depth));
    	List<DownloadPojo> records = getParameterDao(grid).downloadLayerRecords(pojo);

    	CsvMapper mapper = new CsvMapper();
    	CsvSchema schema = mapper.schemaFor(DownloadPojo.class).withColumnSeparator(';').withHeader();
    	String csv =  mapper.writer(schema).writeValueAsString(records);
    	
        response.setHeader("Content-Disposition","attachment;filename="+grid+"_"+parameters+"_"+time+"_"+depth+".csv");
        ServletOutputStream out = response.getOutputStream();
        out.println(csv);
        out.flush();
        out.close();
    }		
}
