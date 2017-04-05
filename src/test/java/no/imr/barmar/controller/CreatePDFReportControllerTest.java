package no.imr.barmar.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CreatePDFReportControllerTest {

	@Test
    public void createpdfreportTest () throws Exception {
		HttpServletRequest req = new MockHttpServletRequest();
		HttpServletResponse resp = new MockHttpServletResponse();
		
		CreatePDFReportController pdf = new CreatePDFReportController();
		pdf.createpdfreport(
	    		200, 200, "bbox", "sld", "layer", 
	    		"agridname:'BarMar';parameter_ids:'214';depthlayername:'F';periodname:'F';aggregationfunc:'avg'", req, resp);

	}

}
