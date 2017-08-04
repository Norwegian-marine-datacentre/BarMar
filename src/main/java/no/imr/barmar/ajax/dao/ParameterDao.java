package no.imr.barmar.ajax.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import no.imr.barmar.ajax.parameters.pojo.Metadata;
import no.imr.barmar.ajax.parameters.pojo.Parameter;
import no.imr.barmar.ajax.pojo.DownloadPojo;
import no.imr.barmar.geoserver.UrlConsts;
import no.imr.barmar.pojo.BarMarPojo;

@Component
public class ParameterDao {
	
	@Autowired
	private UrlConsts urlConsts;
	
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate jdbcTempleateList;
	
	private JdbcTemplate barmarJdbcTemplate;
	private NamedParameterJdbcTemplate barmarJdbcTempleateList;
	
	private JdbcTemplate normarJdbcTemplate;
	private NamedParameterJdbcTemplate normarJdbcTempleateList;
	
	private String DESIMAL_PRECISION = "0";
	
	@Resource(name="barmarDataSource")
	public void setBarmarDataSource(DataSource dataSource) {
	    this.barmarJdbcTemplate = new JdbcTemplate(dataSource);
	    this.barmarJdbcTempleateList = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Resource(name="normarDataSource") 
	public void setNormarDataSource(DataSource dataSource) {
	    this.normarJdbcTemplate = new JdbcTemplate(dataSource);
	    this.normarJdbcTempleateList = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public void setDataSource( String gridName ) {
    	if ( gridName.equals( urlConsts.getNorMar() ) ) {
    		jdbcTemplate = normarJdbcTemplate;
    		jdbcTempleateList = normarJdbcTempleateList;    	
    	} else {
    		jdbcTemplate = barmarJdbcTemplate;
    		jdbcTempleateList = barmarJdbcTempleateList;	
    	}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}
	
	/**
	 * GET ALL GRIDS
	 * 
	 * @return list of grids
	 */
	private List<String> getAllGridNames() {
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
				  "WHERE m.id_grid = g.id AND m.id_parameter = p.id AND g.name=? AND p.publish=true "+
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
						"WHERE vt.id_vcell = vc.id AND vt.id_parameter = p.id AND vt.id_grid = g.id and p.id=? AND vc.name != 'W' and vc.name != 'F' "+
						"ORDER BY vc.name != 'F'", 
				new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {

				return rs.getString("depth");
			}
		}, p.getId());
		
		String paramName = p.getName(); 
		if ( ( paramName.contains("Temperature") || paramName.contains("Salinity") )&& depths.size() == 2 ) { // remove aggregate all (F) if only one datapoint
			String periodA = depths.get(0);
			String periodB = depths.get(1);
			if ( periodA.equals("F") ) depths.remove(periodA);
			if ( periodB.equals("F") ) depths.remove(periodB);
		}
		p.setDepths(depths);    	
    }
    
    protected void setPeriod( Parameter p ) {
    	String month = "SELECT tc.name  AS periodname ";
    	String quarter = "SELECT 'Q' || tc.year || '-' || tc.quarter AS periodname ";
    	String whereQueryRest = 				
				   "FROM parameter_tcell pt, tcell tc, parameter p " +
				  "WHERE pt.id_tcell = tc.id AND pt.id_parameter = p.id AND p.id=? AND tc.name != 'W' AND pt.publish=true " ;
						   
    	String orderBy = "ORDER BY p.name, tc.name;";
    	String query = "";
    	if ( p.getName().contains("Temperature") || p.getName().contains("Salinity")) { 
    		query = month + whereQueryRest + " and tc.name != 'F' " + orderBy;
    	} else {
    		query = month + whereQueryRest + orderBy;
    	}
    	
		List<String> periods = jdbcTemplate.query(
				query,
				new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("periodname");
			}
		}, p.getId());
		if ( periods.size() == 2 ) { // remove aggregate all (F) if only one datapoint
			String periodA = periods.get(0);
			String periodB = periods.get(1);
			if ( periodA.equals("F") ) periods.remove(periodA);
			if ( periodB.equals("F") ) periods.remove(periodB);
		}
		p.setPeriods( periods );
    }
    
    public void getMaxMinTemperature( BarMarPojo pojo, String aggregationFunc) {
    	
    	Map<String, Object> sqlParam = new HashMap<String, Object>();
    	sqlParam.put( "grid", pojo.getGrid() );
    	sqlParam.put( "names", pojo.getParameter() );
    	sqlParam.put( "depths", pojo.getDepth() );
    	sqlParam.put( "periods", pojo.getTime() );
    	
    	String selectMean = "SELECT round( cast( avg(v.value) as numeric), "+DESIMAL_PRECISION+") as average_value "; 
    	String stdDevCoefficient =
    			//avoid divide by 0
    			"SELECT (CASE stddev_samp(v.value) OVER (PARTITION BY h.name) WHEN 0 THEN 0 "+
    					"ELSE round( cast( (stddev_samp(v.value) OVER (PARTITION BY h.name) ) / ( avg(v.value) OVER (PARTITION BY h.name) ) as numeric), "+DESIMAL_PRECISION+") END) as average_value ";
    	String selectFullRange = "SELECT round( cast( v.value  as numeric), "+DESIMAL_PRECISION+") as average_value ";
    	String RestOfQuery = 
    			"FROM grid g, hcell h, value v, parameter p, valuexvcell vxv, valuextcell vxt, tcell tc, vcell vc " +  
    	        "WHERE h.id_grid = g.id AND v.id_hcell = h.id AND v.id_parameter = p.id AND vxt.id_value = v.id AND vxt.id_tcell = tc.id AND vxv.id_value = v.id AND vxv.id_vcell = vc.id " +
    	        "AND g.name=:grid AND p.name in (:names) AND vc.name in (:depths) AND tc.name in (:periods) ";
        String selectMeanGroupBy = "GROUP BY h.geoshape";
    	
    	String query = "";
    	if ( aggregationFunc.equals("avg") ) {
    		query = selectMean + RestOfQuery + selectMeanGroupBy;
    	} else if ( aggregationFunc.equals("relative_std_dev") ) {
    		query = stdDevCoefficient + RestOfQuery;
    	} else {
    		query = selectFullRange + RestOfQuery;
    	}

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
		pojo.setMaxLegend( max +1 ); //max of legend in db is integer so add 1 to get inclusive boundery
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
		return downloadPojos;
    }
    
    public Map<String, String> getNewParameterMap() {
    	String query = "Select name, standard_name from parameter_basename";
    	
    	List<Map<String,Object>> rows = jdbcTemplate.queryForList(query);
    	
    	HashMap<String, String> namePairs = new HashMap<String, String>();
    	for (Map<String,Object> row : rows) {
    		namePairs.put( (String)row.get("name"), (String)row.get("standard_name"));
    	}
    	return namePairs;
    }
}
