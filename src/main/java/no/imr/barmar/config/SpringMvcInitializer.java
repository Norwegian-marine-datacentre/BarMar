package no.imr.barmar.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import no.imr.framework.logging.logback.initalize.InitalizeLogbackHandler;
import no.imr.framework.logging.slf4j.exceptions.LoggerInitalizationException;

public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringMvcInitializer.class);

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { WebMvcConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);

		FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encodingFilter",
				org.springframework.web.filter.CharacterEncodingFilter.class);
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

		try {
			InitalizeLogbackHandler.getInstance()
					.initalize(System.getProperty("catalina.base") + "/conf/barmar_logback_v1.xml", true);

		} catch (LoggerInitalizationException ex) {
			LOGGER.error("Logging initializaton failed.", ex);
		}
		LOGGER.info("Entering application.");

	}

}