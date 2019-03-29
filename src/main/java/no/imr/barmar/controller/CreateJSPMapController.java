package no.imr.barmar.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.jasperreports.engine.JRException;

/**
*
* @author endrem
*/
@Controller
public class CreateJSPMapController {
	
	@Autowired
	private GetMapHelper mapHelper;
	
	@RequestMapping(value = "/createjpg", method = RequestMethod.POST)
    public void  createjpgreport( 
    		@RequestParam("width") Integer width,
    		@RequestParam("height") Integer height,
			@RequestParam("bbox") String bbox,
			@RequestParam("sld") String sld,
			@RequestParam("layer") String layer,
			@RequestParam("viewparams") String viewparams,
			@RequestParam("metadataRef") String metadataRef,
			HttpServletRequest request,
            HttpServletResponse resp) throws IOException, JRException, UnsupportedEncodingException {

    	String url = mapHelper.createBaseLayerUrl( width, height, bbox);
    	String secondLayer = mapHelper.createFishExchangeLayer( width, height, bbox, layer, sld, viewparams );
        String legendUrl = mapHelper.createFishExchangeLegend( layer, sld);
        
        
        BufferedImage baseLayer = ImageIO.read( new URL(url) );
        BufferedImage second = ImageIO.read( new URL(secondLayer) );
        BufferedImage legendImg = ImageIO.read( new URL(legendUrl) );

        BufferedImage theMapImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = (Graphics2D) theMapImage.getGraphics();
        g2d.drawImage(baseLayer, 0, 0, null);
        g2d.drawImage(second, 0, 0, null);
        g2d.drawImage(legendImg, width - legendImg.getWidth(), height - legendImg.getHeight(), null);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ImageIO.write( theMapImage, "jpg", baos );
    	baos.flush();
    	byte[] imageInByte = baos.toByteArray();
    	baos.close();
    	
        resp.getOutputStream().write(imageInByte);
        resp.getOutputStream().close();
	}
	

}
