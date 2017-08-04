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

import no.imr.barmar.ajax.dao.ParameterDao;
import no.imr.barmar.gis.sld.SLDFile;
import no.imr.barmar.pojo.BarMarPojo;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Controller
public class CreateSLDController {

	private final static String PUNKTVISNING = "punktvisning";
    
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
	private String writeSldToResponse(BarMarPojo queryFishEx, boolean areadisplay, String hueColor, boolean logarithmicScale) throws Exception {
        String sld = sldFile.getSLDFile( queryFishEx, areadisplay, hueColor, logarithmicScale );
        String filename = "sld_".concat(String.valueOf(Math.random() * 10000 % 1000)).concat(".sld");
        writeSldFileToTmpdir( sld, filename );
        return filename;
	}
	
	@RequestMapping("/createBarMarsld")
    public @ResponseBody Map<String, Object> createBarMarsld(
    		@RequestParam("grid") String grid,
    		@RequestParam("parameter[]") String[] parameters,
    		@RequestParam("time[]") String[] time, 
    		@RequestParam("depth[]") String[] depth, 
    		@RequestParam("displaytype") String displaytype,
    		@RequestParam("aggregationfunc") String aggregationFunc,
    		@RequestParam("logscale") String logscale) throws Exception {

        BarMarPojo queryFishEx = new BarMarPojo( grid, Arrays.asList(parameters), Arrays.asList(depth), Arrays.asList(time) );
        dao.getMaxMinTemperature(queryFishEx, aggregationFunc);

        boolean areadisplay = !displaytype.contains(PUNKTVISNING);
        String stdDevCoefficientColor = null; 
        if ( aggregationFunc.equals( "relative_std_dev" )) {
        	stdDevCoefficientColor = "#B2EC5D"; //Inchworm green 
        }
        boolean logarithmicScale = logscale.equals("logarithm");
        String filename = writeSldToResponse(queryFishEx, areadisplay, stdDevCoefficientColor, logarithmicScale);
        
        Map<String, Object> maxMinLegendValues = new HashMap<String, Object>();
        maxMinLegendValues.put("filename", filename);
        
        return maxMinLegendValues;
	}
	
	private void writeSldFileToTmpdir( String sld, String filename ) throws IOException {
        File output = new File(System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(filename));
        OutputStream fos = new FileOutputStream(output);
        fos.write(sld.getBytes());
        fos.close();		
	}
}
