package no.imr.barmar.ajax.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import no.imr.barmar.ajax.pojo.UserInfo;
import no.imr.barmar.geoserver.UrlConsts;

@Component
public class UserDao implements UserDetailsService {
	
	@Autowired
	private UrlConsts urlConsts;
	
	private JdbcTemplate jdbcTemplate;
	
	private JdbcTemplate barmarJdbcTemplate;
	
	private JdbcTemplate normarJdbcTemplate;
	

	@Resource(name="barmarDataSource")
	public void setBarmarDataSource(DataSource dataSource) {
	    this.barmarJdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Resource(name="normarDataSource") 
	public void setNormarDataSource(DataSource dataSource) {
	    this.normarJdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void setDataSource( String gridName ) {
    	if ( gridName.equals( urlConsts.getNorMar() ) ) {
    		jdbcTemplate = normarJdbcTemplate;   	
    	} else {
    		jdbcTemplate = barmarJdbcTemplate;
    	}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
	
	public UserDetails loadUserByUsername( String username ) {
		String sql = "SELECT firstname, pwd FROM person WHERE firstname=?";

		setDataSource( "BarMar" );
		System.out.println("************** loadUserByUsername ************** username"+username);
		System.out.println("************** jdbc:"+jdbcTemplate+" sql:"+sql);
		System.out.println("************** jdbc barmar:"+barmarJdbcTemplate);
		System.out.println("************** jdbc normar:"+normarJdbcTemplate);
		
		UserDetails userInfo = null;
		try {
		userInfo = (UserDetails) jdbcTemplate.queryForObject(sql, new Object[] { username },
				new RowMapper<UserDetails>() {
					public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
						System.out.println("************** UserDetails-NAME **************"+rs.getString("firstname"));
						UserInfo user = new UserInfo();
						user.setUsername(rs.getString("firstname"));
						user.setPassword(rs.getString("pwd"));
						return user;
					}
				});
		} catch(Exception e) {System.out.println("******exception"+e.getMessage()); }
		System.out.println("********* userInfo: ************"+userInfo.getUsername()+" "+userInfo.getPassword());
		return userInfo;
	}
}
