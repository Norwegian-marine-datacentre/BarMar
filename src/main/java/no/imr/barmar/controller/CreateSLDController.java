package no.imr.barmar.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import no.imr.barmar.gis.sld.SLDFile;
import no.imr.barmar.gis.wfs.GetWFSList;
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
	
    @Autowired( required = true )
    private GetWFSList gwfs;
    
    @Autowired
    private MaxMinLegendValue maxMinHelper;
    
    @Autowired( required = true )
    private SLDFile sldFile;
    	
	@RequestMapping("/createsld")
    public void createsld(
    		@RequestParam("grid") String grid,
    		@RequestParam("parameter") String parameter,
    		@RequestParam("time") String time,
    		@RequestParam("depth") String depth,
    		@RequestParam("displaytype") String displaytype,  
            HttpServletResponse resp) throws Exception {
		

        boolean areadisplay = isAreadisplay( displaytype );
        
        BarMarPojo queryFishEx = new BarMarPojo( grid, Arrays.asList(parameter), Arrays.asList(depth), Arrays.asList(time) );
        maxMinHelper.setMaxMinLegendValuesFromWFS( queryFishEx, null );
        
        writeSldToResponse(queryFishEx, areadisplay, resp);
    }	
	
	private void writeSldToResponse(BarMarPojo queryFishEx, boolean areadisplay, HttpServletResponse resp) throws Exception {
        String sld = sldFile.getSLDFile( queryFishEx, areadisplay );
        String filename = "sld_".concat(String.valueOf(Math.random() * 10000 % 1000)).concat(".sld");
        writeSldFileToTmpdir( sld, filename );
        writeFilenameToResponse( resp, filename );		
	}
	
	@RequestMapping("/createBarMarsld")
    public void createBarMarsld(
    		@RequestParam("grid") String grid,
    		@RequestParam("parameter[]") String[] parameters,
    		@RequestParam("time") String time,
    		@RequestParam("depth") String depth,
    		@RequestParam("displaytype") String displaytype,  
            HttpServletResponse resp) throws Exception {

        boolean areadisplay = isAreadisplay( displaytype );
        
        BarMarPojo queryFishEx = new BarMarPojo( grid, Arrays.asList(parameters), Arrays.asList(depth), Arrays.asList(time) );
        maxMinHelper.setMaxMinLegendValuesFromWFS( queryFishEx, null );
        
        writeSldToResponse(queryFishEx, areadisplay, resp);
	}

	
    public void createNorMarsld(String grid, List<String> parameter,
    		List<String> time, List<String> depth, String displaytype,  
            HttpServletResponse resp) throws Exception {

        boolean areadisplay = isAreadisplay( displaytype );
        
        BarMarPojo queryFishEx = new BarMarPojo( grid, parameter, depth, time );
        maxMinHelper.setMaxMinLegendValuesFromWFS(queryFishEx);
        
        String sld = sldFile.getSLDFile( queryFishEx, areadisplay);
        String filename = "sld_".concat(String.valueOf(Math.random() * 10000 % 1000)).concat(".sld");
        writeSldFileToTmpdir( sld, filename );
        writeFilenameToResponse( resp, filename );
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
	
	private void writeFilenameToResponse( HttpServletResponse resp, String filename ) throws IOException {
        resp.setContentType("text/plain");
        OutputStream out = resp.getOutputStream();
        out.write(filename.getBytes());
        out.close();
	}
}
