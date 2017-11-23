package no.imr.barmar.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author trondwe
 * @author endrem
 */
@Controller
public class GetSLDController  {

	/**
	 * tmp sld files are stored at - /usr/share/tomcat5/temp/
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/getsld")
    public void getsld(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = (String) request.getParameter("file");
        if ( filename == null ) { // if .. someone is not interested in sld files
            return;
        }
        String directory = System.getProperty("java.io.tmpdir");
        String[] filesInDir = new File(directory).list();
        String tmpSldFilepath = System.getProperty("java.io.tmpdir").concat(System.getProperty("file.separator")).concat(filename);
        boolean found = false;
        for ( int i=0;( i < filesInDir.length && !found) ; i++) { // make sure files is local to directory
            found= ( filesInDir[i].equals(filename));
        }
        if (found) {
            File output = new File( tmpSldFilepath );
            writeSldToResponse( output, response );
        }
        return;
    }
	
	private void writeSldToResponse( File sldOutput, HttpServletResponse response ) throws IOException {
        BufferedReader inReader = new BufferedReader(new FileReader(sldOutput.getAbsoluteFile()));
        Writer respWriter = response.getWriter();

        try {
	        String thisLine;
	        StringBuffer sldXml = new StringBuffer();
	        while ((thisLine = inReader.readLine()) != null) { 
	        	sldXml.append(thisLine);
	        }      
        	respWriter.write( sldXml.toString() );
        } finally {
        	respWriter.close();
        	inReader.close();
        }
	}
}
