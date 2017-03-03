package no.imr.barmar.pojo;

import java.util.ArrayList;
import java.util.List;


/**
 * Contains attributes used to query fishexchange db.
 * Methods on object are null-safe.
 * @author endrem
 */
public class BarMarPojo {
    
    public static final String GRIDNAME = "gridname";
    public static final String PARAMETERNAME = "parametername";
    public static final String DEPTHLAYERNAME = "depthlayername";
    public static final String PERIODNAME = "periodname";
    
    private String grid = "";
    private List<String> parameter = new ArrayList<String>(1);
//  private String parameter = "";
    private List<String> time = new ArrayList<String>(1); //default one
    private List<String> depth = new ArrayList<String>(1); //default one
    
    private float maxLegend;
    private float minLegend;
    
    public BarMarPojo( String grid, List<String> parameter, List<String> depth, List<String> time ) {
        setGrid( grid );
        setParameter( parameter );
        setTime( time );
        setDepth( depth );
    }
  
    public BarMarPojo(){}
 
    public String getGrid() {
        return grid;
    }
    public void setGrid(String grid) {
        if ( grid == null ) {
            this.grid = "";
        } else {
            this.grid = grid;
        }
    }
    public void addParameter(String param ) {
        this.parameter.add( param );
    }
    public List<String> getParameter() {
        return parameter;
    }
    public String getParameter(int i) {
    	if ( parameter.size() > i) {
    		return parameter.get(i);
    	}
    	return "";
    }
    public void setParameter(List<String> parameter) {
        if ( parameter != null ) {
            this.parameter = parameter;
        }
    }
    public void setParameter(String parameter) {
        if ( parameter != null ) {
            this.parameter.add(0, parameter);
        }
    }
    public List<String> getTime() {
        return time;
    }
    public String getTime(int i) {
		if ( time.size() > i) {
			return time.get(i);
		}
		return "";
    }    
    public void setTime(List<String> time) {
        if ( time == null ) {
            this.time = new ArrayList<String>(0);
        } else {
            this.time = time;
        }
    }
    public List<String> getDepth() {
        return depth;
    }
    public String getDepth(int i) {
		if ( depth.size() > i) {
			return depth.get(i);
		}
		return "";
    }
    public void setDepth(List<String> depth) {
        if ( depth == null ) {
            this.depth = new ArrayList<String>(0);
        } else {
            this.depth = depth;
        }
    }

    public float getMaxLegend() {
        return maxLegend;
    }

    public void setMaxLegend(float maxLegend) {
        this.maxLegend = maxLegend;
    }

    public float getMinLegend() {
        return minLegend;
    }

    public void setMinLegend(float minLegend) {
        this.minLegend = minLegend;
    }
    
    @Override 
    public String toString() {
        return "grid:"+ grid + " parameter:" + parameter + " time:" + time + " depth:" + depth + " maxLegend:" + maxLegend + " minLegend:" + minLegend;
    }
}
