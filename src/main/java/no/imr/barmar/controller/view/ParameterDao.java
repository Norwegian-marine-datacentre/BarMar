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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import no.imr.barmar.controller.view.pojo.Metadata;
import no.imr.barmar.controller.view.pojo.Parameter;
import no.imr.barmar.pojo.BarMarPojo;

@Component
public class ParameterDao {
	
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate jdbcTempleateList;
	
	private PeriodAndDepthHelper pAndD = new PeriodAndDepthHelper();
	
	@Resource(name="dataSource")
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	    this.jdbcTempleateList = new NamedParameterJdbcTemplate(dataSource);
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

				return rs.getString("depth");
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
				return rs.getString("periodname");
			}
		}, p.getId());
		p.setPeriods( periods );
    }
    
    public void getMaxMinTemperature( BarMarPojo pojo) {
    	MapSqlParameterSource map = new MapSqlParameterSource();
    	map.addValue("grid", pojo.getGrid());
    	map.addValue("names", pojo.getParameter().toArray(new String[pojo.getParameter().size()]));
    	map.addValue("depths", pojo.getDepth().toArray(new String[pojo.getDepth().size()]));
    	map.addValue("periods", pojo.getTime().toArray(new String[pojo.getTime().size()]));
    	
    	Map<String, Object> sqlParam = new HashMap<String, Object>();
    	sqlParam.put( "grid", pojo.getGrid() );
    	sqlParam.put( "names", pojo.getParameter() );
    	sqlParam.put( "depths", pojo.getDepth() );
    	sqlParam.put( "periods", pojo.getTime() );
    	System.out.println("param name list name:"+pojo.getParameter(0));
    	System.out.println("param name list depth:"+pojo.getDepth(0));
    	System.out.println("param name list period:"+pojo.getTime(0));
    	
    	String query = "SELECT * FROM ( SELECT max(p.maxval), min(p.minval) " +  
    	           "FROM grid g " +
    	           "INNER JOIN parameter_statistics p " + 
    	           "ON g.id=p.id_grid AND g.name=:grid " +
    		   "INNER JOIN parameter par " +
    	           "ON par.id=p.id_parameter AND par.name in (:names) " +
    	           "INNER JOIN vcell vc " +
    	           "ON vc.id = p.id_vcell and vc.name in (:depths) " +
    	           "INNER JOIN tcell tc " +
    	           "ON tc.id= p.id_tcell and tc.name in (:periods) ) as gridname";
    	 
		List<BarMarPojo> tmpPojos = jdbcTempleateList.query(query, sqlParam, new RowMapper<BarMarPojo>() {
			public BarMarPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
				BarMarPojo tmpPojo = new BarMarPojo();
				tmpPojo.setMaxLegend( rs.getFloat("max") );
				tmpPojo.setMinLegend( rs.getFloat("min") );
				return tmpPojo;
			}
		});
		BarMarPojo tmpPojo = tmpPojos.get(0);
		if ( tmpPojo != null ) {
			pojo.setMaxLegend( tmpPojo.getMaxLegend() );
			pojo.setMinLegend( tmpPojo.getMinLegend() );
		}
		System.out.println("maxMin:"+pojo.getMaxLegend());
    }
}
