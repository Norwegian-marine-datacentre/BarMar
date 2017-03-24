package no.imr.barmar.controller.view;

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

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import no.imr.barmar.controller.view.pojo.Metadata;
import no.imr.barmar.controller.view.pojo.Parameter;
import no.imr.barmar.controller.view.pojo.ParameterGroup;
import no.imr.barmar.pojo.BarMarPojo;

@Controller
public class BarMarControllerFromDb {

	@Autowired( required = true )
	protected SortSubSpecies sortSubSpecies;
	
	@Autowired( required = true )
	protected ParameterDao dao;
	
	Map<String, Object> barMarParameteres = null;

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
    		
    		Map<String, Metadata> metadataList = new HashMap<String, Metadata>();
	        List<Parameter> params = dao.getAllParametersAndMetadata( grid, metadataList );
	        for ( String metadataRef : metadataList.keySet() ) {
	        	barMarParameteres.put( metadataRef, metadataList.get(metadataRef) );
	        }
	        
	        List<String> speciesStr = new ArrayList<String>(30);
	        Map<String, List<Parameter>> speciesMap = new HashMap<String, List<Parameter>>();
        	for ( Parameter param : params ) {
        		String name = param.getName();
        		int end = name.indexOf( "_" );
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
        	for ( String speciesName : speciesStr) {

        		ParameterGroup pg = sortSubSpecies.groupSubSpeciesIntoLengthAgeOther( speciesMap.get(speciesName) );
        		barMarParameteres.put(speciesName, pg);
        	}
        	barMarParameteres.put( "species", speciesStr );
    	}
        return barMarParameteres;
    }
    
    
    @RequestMapping("/downloadData")
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
    	List<DownloadPojo> records = dao.downloadLayerRecords(pojo);

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
