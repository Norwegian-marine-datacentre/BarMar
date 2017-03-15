package no.imr.barmar.controller;

import java.util.Map;

import org.junit.Test;

public class CreateSLDControllerTest {
	
	private CreateSLDController createSld = new CreateSLDController();
	
	public CreateSLDControllerTest() {
	}

	
	@Test
	public void createSldTest() throws Exception {

		String grid = "grid:BarMar";
		String[] parameters = {"Cod_survey_trawl_ecosystem_0-4cm","Cod_survey_trawl_ecosystem_5-9cm"};
		String time = "time:F";
		String depth = "depth:F";
		String displaytype = "displaytype:punktvisning";

		
		Map<String, Object> sld = createSld.createBarMarsld(grid, parameters, time, depth, displaytype);

	}
}
