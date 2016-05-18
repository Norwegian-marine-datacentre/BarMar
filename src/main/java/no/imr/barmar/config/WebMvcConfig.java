package no.imr.barmar.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 *
 * @author endrem
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * Configures the content negotiation.
     *
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
//                .favorPathExtension(false)
//                .favorParameter(true)
                // ignoreAcceptHeader(true)
//                .parameterName("format").useJaf(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("html", MediaType.TEXT_HTML);
    }

    /**
     * Override the default converters as we cannot control them and adds json
     *
     * @param converters Default converters.
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(getMappingJacksonHttpMessageConverter());
    }

    /**
     * Create the json converter.
     *
     * @return The json converter.
     */
    @Bean(name = "mappingJacksonHttpMessageConverter")
    public HttpMessageConverter getMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setPrettyPrint(true);
        // converter.getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
        // false);
        return converter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/html/"); //for development for tomcat embeded in maven build
        //registry.addResourceHandler("/html/**").addResourceLocations("/html/");

    }

//    @Bean
//    public ViewResolver getViewResolver() {
//        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//        resolver.setPrefix("/WEB-INF/jsp/");
//        resolver.setSuffix(".jsp");
//        return resolver;
//    }
    
    @Bean
    public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {

        List<ViewResolver> resolvers = new ArrayList<ViewResolver>();

//        InternalResourceViewResolver r1 = new InternalResourceViewResolver();
//        r1.setPrefix("/WEB-INF/pages/");
//        r1.setSuffix(".jsp");
//        r1.setViewClass(JstlView.class);
//        resolvers.add(r1);

        JsonViewResolver r2 = new JsonViewResolver();
        resolvers.add(r2);

        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setViewResolvers(resolvers);
        resolver.setContentNegotiationManager(manager);
        return resolver;

    }
}