package no.imr.barmar.gis.sld;

import org.junit.Test;

public class SLDFileTest {
	
	@Test
	public void makeValueRangesTest() {
		SLDFile sld = new SLDFile();
		sld.legendRange(0f,100f,10, true);	
	}

}
