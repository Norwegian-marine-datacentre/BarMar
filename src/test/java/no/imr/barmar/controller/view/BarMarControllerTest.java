package no.imr.barmar.controller.view;

import java.util.Map;

import org.junit.Test;

import no.imr.barmar.gis.sld.SLDpojoSelectionRule;
import no.imr.barmar.gis.wfs.GetWFSList;
import no.imr.barmar.gis.wfs.GetWFSParameterList;

public class BarMarControllerTest {

	private BarMarController controller = new BarMarController();
	
	private GetWFSList gwfs  = new GetWFSList(new GetWFSParameterList(), new SLDpojoSelectionRule());
	
	private SortSubSpecies sortSubSpecies = new SortSubSpecies();
	
	
	public BarMarControllerTest() {
		controller.gwfs = gwfs;
		controller.sortSubSpecies = sortSubSpecies;
	}
	//Url to test:
	// http://localhost:8081/BarMar/barmar.json?grid=BarMar&species=Cod&subSpecies[]=Cod_survey_trawl_ecosystem_0-4cm
	@Test
	public void multipleParametersTest() throws Exception {
		String grid = "BarMar";
		String species = "Cod";
		String subSpecies[] = {"Cod_survey_trawl_ecosystem_0-4cm", "Cod_survey_trawl_ecosystem_5-9cm"};
		Map<String, Object> m = controller.getBarMarJson(grid, species, subSpecies);
		System.out.println(m.toString());
	}
	
		
}
