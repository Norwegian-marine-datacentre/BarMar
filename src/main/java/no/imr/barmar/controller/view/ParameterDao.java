package no.imr.barmar.controller.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ParameterDao {
	
	private JdbcTemplate jdbcTemplate;
	
	private PeriodAndDepthHelper pAndD = new PeriodAndDepthHelper();
	
	@Resource(name="dataSource")
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * GET ALL GRIDS
	 * 
	 * @return list of grids
	 */
	public List<String> getAllGridNames() {
		return jdbcTemplate.query("select id, name from grid;", 
				new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("name");
			}
		});
	}
	
	/**
	 * Query runs in about 1300ms
	 * 
	 * GET ALL PARAMETERS AND METADATA
	 * 
	 * @param gridName
	 * @return
	 */
    public List<Parameter> getAllParametersAndMetadata(String gridName, final Map<String, Metadata> metadataList) {
		    	

		return jdbcTemplate.query(
				"SELECT p.id, p.name AS parametername, m.dataset_name AS datasetname, m.geographic_coverage AS geographiccoverage, "+
						"m.summary AS description, m.originator AS datasetoriginator, m.contact AS datasetcontact, m.lastupdated AS datasetlastupdated "+
				   "FROM grid g, parameter p, metadata m "+
				  "WHERE m.id_grid = g.id AND m.id_parameter = p.id AND g.name=? "+
				  "ORDER BY g.name, p.name;",
				new RowMapper<Parameter>() {
					
	    	Map<String, String> descriptionsAndKey = new HashMap<String, String>();
	    	
			public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
				Parameter p = new Parameter();
				p.setId( rs.getInt( "id" ) );
				p.setName( rs.getString( "parametername" ) );
				
				String description = rs.getString( "description" );
				if ( descriptionsAndKey.get(description) == null ) {
					String parameterForeignKeyToMetadata = "metadata_"+p.getId();
					descriptionsAndKey.put( description, parameterForeignKeyToMetadata );
					
					Metadata m = new Metadata();
					m.setDatasetName( rs.getString( "datasetname" ) );
					m.setGeographicCoverage( rs.getString("geographiccoverage") );
					m.setMetadata( description );
					m.setDatasetOriginator( rs.getString("datasetoriginator") );
					m.setDatasetContact( rs.getString( "datasetcontact" ) );
					m.setDatasetLastUpdated( rs.getString( "datasetlastupdated" ) );
					metadataList.put( parameterForeignKeyToMetadata, m);
				}
				p.setMetadataRef( descriptionsAndKey.get(description) );

				ParameterDao.this.setDepthAndPeriod( p );
				return p;
			}
		}, gridName); 
	}
    
    protected void setDepthAndPeriod( Parameter p) {
    	setDepth(p);
    	setPeriod(p);
    }
    
    protected void setDepth( Parameter p ) {
		List<String> depths = jdbcTemplate.query(
				"SELECT vc.name AS depth "+
						"FROM parameter_vcell vt, vcell vc, parameter p, grid g " +
						"WHERE vt.id_vcell = vc.id AND vt.id_parameter = p.id AND vt.id_grid = g.id and p.id=? AND vc.name != 'W' "+
						"ORDER BY vc.name != 'F'", 
				new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {

				return pAndD.getDepth( rs.getString("depth") );
			}
		}, p.getId());
		p.setDepths(depths);    	
    }
    
    protected void setPeriod( Parameter p ) {
		List<String> periods = jdbcTemplate.query(
				"SELECT tc.name AS periodname " +
				   "FROM parameter_tcell pt, tcell tc, parameter p " +
				  "WHERE pt.id_tcell = tc.id AND pt.id_parameter = p.id AND p.id=? AND tc.name != 'W' " +
				  "ORDER BY p.name, tc.name;",
				new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return pAndD.getPeriod( rs.getString("periodname") );
			}
		}, p.getId());
		p.setPeriods( periods );
    }
}
