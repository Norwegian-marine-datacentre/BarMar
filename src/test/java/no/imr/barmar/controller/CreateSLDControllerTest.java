package no.imr.barmar.controller;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import no.imr.barmar.gis.sld.LayerNameForSLD;
import no.imr.barmar.gis.sld.SLDFile;
import no.imr.barmar.gis.sld.SLDpojoSelectionRule;
import no.imr.barmar.gis.wfs.GetWFSList;
import no.imr.barmar.gis.wfs.GetWFSParameterList;
import no.imr.barmar.gis.wfs.MaxMinLegendValue;

public class CreateSLDControllerTest {
	
	private CreateSLDController createSld = new CreateSLDController();
	
	public CreateSLDControllerTest() {
		createSld.gwfs = new GetWFSList( new GetWFSParameterList(), new SLDpojoSelectionRule() );
		createSld.maxMinHelper = new MaxMinLegendValue( createSld.gwfs );
		createSld.sldFile = new SLDFile( new LayerNameForSLD(), new SLDpojoSelectionRule() );		
	}

	
	@Test
	public void createSldTest() throws Exception {

		String grid = "grid:BarMar";
		String[] parameters = {"Cod_survey_trawl_ecosystem_0-4cm","Cod_survey_trawl_ecosystem_5-9cm"};
		String time = "time:F";
		String depth = "depth:F";
		String displaytype = "displaytype:punktvisning";
		MockHttpServletResponse resp = new MockHttpServletResponse();

		
		createSld.createBarMarsld(grid, parameters, time, depth, displaytype, resp);

	}
}
