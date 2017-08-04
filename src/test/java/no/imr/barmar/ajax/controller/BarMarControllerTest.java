package no.imr.barmar.ajax.controller;

import org.junit.Test;

import no.imr.barmar.ajax.controller.BarMarControllerFromDb;

public class BarMarControllerTest {

	
	
	public BarMarControllerTest() {

	}
	//Url to test:
	// http://localhost:8081/BarMar/barmar.json?grid=BarMar&species=Cod&subSpecies[]=Cod_survey_trawl_ecosystem_0-4cm
//	@Test
	public void multipleParametersTest() throws Exception {
		String grid = "BarMar";
		String species = "Cod";
		String subSpecies[] = {"Cod_survey_trawl_ecosystem_0-4cm", "Cod_survey_trawl_ecosystem_5-9cm"};

	}
	
	@Test
	public void downloadRecordsTest() throws Exception{
		String[] params = {"cod"};
		String[] depth = {"F"};
		String[] period = {"F"};
				
		new BarMarControllerFromDb().downloadRecords("BarMar", params, depth, period, null);
	}
}
