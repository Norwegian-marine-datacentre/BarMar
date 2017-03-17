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
    	
    	String query = "SELECT avg(v.value) as average_value " +
    			"FROM grid g, hcell h, value v, parameter p, valuexvcell vxv, valuextcell vxt, tcell tc, vcell vc " +  
    	        "WHERE h.id_grid = g.id AND v.id_hcell = h.id AND v.id_parameter = p.id AND vxt.id_value = v.id AND vxt.id_tcell = tc.id AND vxv.id_value = v.id AND vxv.id_vcell = vc.id " +
    	        "AND g.name=:grid AND p.name in (:names) AND vc.name in (:depths) AND tc.name in (:periods) " +
    	        "GROUP BY h.geoshape";
    	 
		List<Float> avgValues = jdbcTempleateList.query(query, sqlParam, new RowMapper<Float>() {
			public Float mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getFloat("average_value");
			}
		});
		float max = 0;
		float min = 0;
		for ( Float aAvg : avgValues ) {
			if ( aAvg > max ) max = aAvg;
			if ( aAvg < min ) min = aAvg;
		}
		pojo.setMaxLegend( max );
		pojo.setMinLegend( min );
    }
    
    public List<DownloadPojo> downloadLayerRecords( BarMarPojo pojo ) {
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
    	
    	String query = "SELECT v.id, ST_AsText(ST_Force_2D(h.geoshape)) as geogridcell, ST_AsText(ST_Force_2D(h.geopoint)) as geopoint, g.name as gridname, " +
    			"h.name as gridcellname, p.name as parametername, vc.name as depthlayername, tc.name as periodname, v.value " +
    			"FROM grid g, hcell h, value v, parameter p, valuexvcell vxv, valuextcell vxt, tcell tc, vcell vc " +  
    	        "WHERE h.id_grid = g.id AND v.id_hcell = h.id AND v.id_parameter = p.id AND vxt.id_value = v.id AND vxt.id_tcell = tc.id AND vxv.id_value = v.id AND vxv.id_vcell = vc.id " +
    	        "AND g.name=:grid AND p.name in (:names) AND vc.name in (:depths) AND tc.name in (:periods) " +
    	        "order BY h.name, v.value";
    	 
    	System.out.println("query:"+query);
    	System.out.println("sqlParam:"+sqlParam.toString());
    	
		List<DownloadPojo> downloadPojos = jdbcTempleateList.query(query, sqlParam, new RowMapper<DownloadPojo>() {
			public DownloadPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
				DownloadPojo pojo = new DownloadPojo();
				pojo.setId( rs.getInt("id") );
				pojo.setGeogridcell( rs.getString("geogridcell") );
				pojo.setGeopoint( rs.getString("geopoint") );
				pojo.setGridname( rs.getString("gridname") );
				pojo.setGridcellname( rs.getString("gridcellname") );
				pojo.setParametername( rs.getString("parametername") );
				pojo.setDepthlayername( rs.getString("depthlayername") );
				pojo.setPeriodname( rs.getString("periodname") );
				pojo.setValue( rs.getFloat("value") );

				return pojo;
			}
		});
		System.out.println("resultset:"+downloadPojos.size());
		return downloadPojos;
    }
}
