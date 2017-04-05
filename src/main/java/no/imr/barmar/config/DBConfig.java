package no.imr.barmar.config;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author kjetilf
 */
@org.springframework.context.annotation.Configuration
public class DBConfig {
    
	@Autowired
	private Configuration configuration;

	@Bean(destroyMethod = "close")
	public DataSource barmarDataSource() throws PropertyVetoException {
		com.mchange.v2.c3p0.ComboPooledDataSource dataSource = new com.mchange.v2.c3p0.ComboPooledDataSource();
		
		dataSource.setDriverClass(configuration.getString("barmar.jdbc.driver"));
		dataSource.setJdbcUrl(configuration.getString("barmar.jdbc.url"));
		dataSource.setUser(configuration.getString("barmar.jdbc.user"));
		dataSource.setPassword(configuration.getString("barmar.jdbc.password"));
		dataSource.setMaxPoolSize(configuration.getInt("barmar.jdbc.maxPoolSize"));
		dataSource.setMinPoolSize(configuration.getInt("barmar.jdbc.minPoolSize"));
		dataSource.setAcquireIncrement(configuration.getInt("barmar.jdbc.acquireIncrement"));
		dataSource.setIdleConnectionTestPeriod(configuration.getInt("barmar.jdbc.idleConnectionTestPeriod"));
		return dataSource;
	}
	
	@Bean(destroyMethod = "close")
	public DataSource normarDataSource() throws PropertyVetoException {
		com.mchange.v2.c3p0.ComboPooledDataSource dataSource = new com.mchange.v2.c3p0.ComboPooledDataSource();
		
		dataSource.setDriverClass(configuration.getString("normar.jdbc.driver"));
		dataSource.setJdbcUrl(configuration.getString("normar.jdbc.url"));
		dataSource.setUser(configuration.getString("normar.jdbc.user"));
		dataSource.setPassword(configuration.getString("normar.jdbc.password"));
		dataSource.setMaxPoolSize(configuration.getInt("normar.jdbc.maxPoolSize"));
		dataSource.setMinPoolSize(configuration.getInt("normar.jdbc.minPoolSize"));
		dataSource.setAcquireIncrement(configuration.getInt("normar.jdbc.acquireIncrement"));
		dataSource.setIdleConnectionTestPeriod(configuration.getInt("normar.jdbc.idleConnectionTestPeriod"));
		return dataSource;
	}	

	@Bean
	public String getSchema() {
		return configuration.getString("schema");
	}
}
