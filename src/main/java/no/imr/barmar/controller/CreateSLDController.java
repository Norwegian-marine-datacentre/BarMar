package no.imr.barmar.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import no.imr.barmar.controller.view.ParameterDao;
import no.imr.barmar.gis.sld.SLDFile;
import no.imr.barmar.gis.wfs.MaxMinLegendValue;
import no.imr.barmar.pojo.BarMarPojo;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Controller
public class CreateSLDController {

	private final static String PUNKTVISNING = "punktvisning";
    
    @Autowired
    protected MaxMinLegendValue maxMinHelper;
    
    @Autowired( required = true )
    protected SLDFile sldFile;
    
	@Autowired( required = true )
	protected ParameterDao dao;

	/**
	 * Writes SLD to file and stores it in temp directory
	 * 
	 * @param queryFishEx
	 * @param areadisplay
	 * @return filename of temp file
	 * @throws Exception
	 */
	private String writeSldToResponse(BarMarPojo queryFishEx, boolean areadisplay) throws Exception {
        String sld = sldFile.getSLDFile( queryFishEx, areadisplay );
        String filename = "sld_".concat(String.valueOf(Math.random() * 10000 % 1000)).concat(".sld");
        writeSldFileToTmpdir( sld, filename );
        return filename;
	}
	
	@RequestMapping("/createBarMarsld")
    public @ResponseBody Map<String, Object> createBarMarsld(
    		@RequestParam("grid") String grid,
    		@RequestParam("parameter[]") String[] parameters,
    		@RequestParam("time") String time, //todo: time[]
    		@RequestParam("depth") String depth, //todo: depth[]
    		@RequestParam("displaytype") String displaytype) throws Exception {

        boolean areadisplay = isAreadisplay( displaytype );
        
        BarMarPojo queryFishEx = new BarMarPojo( grid, Arrays.asList(parameters), Arrays.asList(depth), Arrays.asList(time) );
        dao.getMaxMinTemperature(queryFishEx);
        
        String filename = writeSldToResponse(queryFishEx, areadisplay);
        
        Map<String, Object> maxMinLegendValues = new HashMap<String, Object>();
        maxMinLegendValues.put("min", queryFishEx.getMinLegend());
        maxMinLegendValues.put("max", queryFishEx.getMaxLegend());
        maxMinLegendValues.put("filename", filename);
        
        return maxMinLegendValues;
	}
	
	private boolean isAreadisplay( String displaytype ) {
        if (displaytype.contains(PUNKTVISNING)) {
            return false;
        } else {
            return true;
        }		
	}
	
	private void writeSldFileToTmpdir( String sld, String filename ) throws IOException {
        File output = new File(System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(filename));
        OutputStream fos = new FileOutputStream(output);
        fos.write(sld.getBytes());
        fos.close();		
	}
}
