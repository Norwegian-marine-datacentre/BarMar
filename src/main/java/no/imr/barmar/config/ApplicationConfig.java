package no.imr.barmar.config;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author kjetilf
 */
@Configuration
@EnableWebMvc
public class ApplicationConfig extends WebMvcConfigurerAdapter {


    @Autowired
    ApplicationContext context;

    /**
    * Configuration object for communicating with property data.
    *
    * @return Configuration object containg properties.
    * @throws ConfigurationException Error during instansiation.
    */
    @Bean
    public org.apache.commons.configuration.Configuration configuration() throws ConfigurationException, Exception {
        org.apache.commons.configuration.PropertiesConfiguration configuration;
        try {
            configuration= new org.apache.commons.configuration.PropertiesConfiguration(System.getProperty("catalina.base") + "/conf/barmar.properties");
        }catch (Exception e) {
            //only for testing
            Resource r = context.getResource("classpath:barmar.properties");
            configuration = new org.apache.commons.configuration.PropertiesConfiguration(r.getFile());              
        }
        return configuration;
    }
}