package no.imr.barmar.gis.sld;

import org.junit.Test;

import no.imr.barmar.pojo.BarMarPojo;

public class LayerNameForSLDTest {

	private SLDFile sldFile = new SLDFile();
	
	public LayerNameForSLDTest() {
		sldFile.sldName = new LayerNameForSLD();
		sldFile.singleOrAggregateSelectionRule = new SLDpojoSelectionRule();
	}
	
	@Test
	public void getSLDFileTest() {

		BarMarPojo pojo = new BarMarPojo();
		pojo.setGrid("BarMar");
		boolean areadisplay = true;
		String hueColor = "FFFF";
		boolean logScale = false;
		
		sldFile.getSLDFile( pojo, areadisplay, hueColor, logScale );
		
	}
}
