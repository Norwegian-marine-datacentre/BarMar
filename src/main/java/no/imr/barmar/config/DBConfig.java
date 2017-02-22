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
	public DataSource dataSource() throws PropertyVetoException {
		com.mchange.v2.c3p0.ComboPooledDataSource dataSource = new com.mchange.v2.c3p0.ComboPooledDataSource();
		
		dataSource.setDriverClass(configuration.getString("jdbc.driver"));
		dataSource.setJdbcUrl(configuration.getString("jdbc.url"));
		dataSource.setUser(configuration.getString("jdbc.user"));
		dataSource.setPassword(configuration.getString("jdbc.password"));
		dataSource.setMaxPoolSize(configuration.getInt("jdbc.maxPoolSize"));
		dataSource.setMinPoolSize(configuration.getInt("jdbc.minPoolSize"));
		dataSource.setAcquireIncrement(configuration.getInt("jdbc.acquireIncrement"));
		dataSource.setIdleConnectionTestPeriod(configuration.getInt("jdbc.idleConnectionTestPeriod"));
		return dataSource;
	}

	@Bean
	public String getSchema() {
		return configuration.getString("schema");
	}
}
