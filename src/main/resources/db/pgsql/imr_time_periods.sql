-- Function: imr_time_periods(bigint)

-- DROP FUNCTION imr_time_periods(bigint);

CREATE OR REPLACE FUNCTION imr_time_periods(id_value_in bigint)
  RETURNS character varying AS
$BODY$
  DECLARE
     timestring varchar;
	 c_timeperiod cursor (myvalue_id bigint) is 
	 select CAST ( t.type AS int2 ),  
	  CAST ( COALESCE(trim(to_char(t.year,'9999')),'0') AS varchar )  , 
	  CAST ( COALESCE(trim(to_char(t.quarter,'9')),'0') AS varchar ), 
	  CAST ( COALESCE(trim(to_char(t.month,'09')),'0') AS varchar ) 
	  FROM tcell as t, valuextcell as vxt 
	  WHERE vxt.id_tcell = t.id and vxt.id_value = myvalue_id
	  ORDER BY t.id;
	 t_type int2;
	 t_year varchar;
	 t_quarter varchar;
     t_month varchar;
  BEGIN
	 timestring := '(';
     OPEN c_timeperiod(id_value_in);
     LOOP
	     FETCH c_timeperiod into t_type, t_year, t_quarter, t_month;
		 IF NOT FOUND THEN
		   close c_timeperiod;
		   IF (timestring = '(') THEN
		      timestring := NULL;
		   ELSE
  	          timestring := substr(timestring,1, (length(timestring) - 1) ) || ')';
		   END IF;
	       RETURN timestring;
		 ELSE
		    IF (t_type = 1)  THEN
		      timestring := timestring || 'F;';
     	    ELSIF (t_type = 2)  THEN
		      timestring := timestring || 'Y'||t_year||';';
     	    ELSIF (t_type = 3)  THEN
		      timestring := timestring || 'Q'||t_year||t_quarter||';';
     	    ELSIF (t_type = 4)  THEN
		      timestring := timestring || 'M'||t_year||t_month||';';
		    ELSE
		      timestring := timestring || ';';
		    END IF;	 
		 END IF;
	 END LOOP;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_time_periods(bigint)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_time_periods(bigint) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_time_periods(bigint) TO public;
GRANT EXECUTE ON FUNCTION imr_time_periods(bigint) TO grid;
