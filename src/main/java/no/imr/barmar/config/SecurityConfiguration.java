package no.imr.barmar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import no.imr.barmar.ajax.dao.UserDao;
import no.imr.barmar.config.rest.RESTAuthenticationFailureHandler;
import no.imr.barmar.config.rest.RESTAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDao userdao;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return userdao;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {		
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userdao);
//        authProvider.setPasswordEncoder(encoder());
        auth.authenticationProvider(authProvider);
	}  

    @Autowired
    private RESTAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RESTAuthenticationSuccessHandler authenticationSuccessHandler;	
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	System.out.println("**************configure():access:"+http.authorizeRequests().antMatchers("downloadData/**").access("hasAuthority('IMR_USER')").toString()+"**************");
    	
    	http.authorizeRequests().antMatchers("/downloadData/**").hasAuthority("IMR_USER");
    	http.csrf().disable();  
    	
//    	http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().successHandler(authenticationSuccessHandler);
        http.formLogin().failureHandler(authenticationFailureHandler);
        
    }
}
