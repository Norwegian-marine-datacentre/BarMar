-- Function: imr_depth_layers(bigint)

-- DROP FUNCTION imr_depth_layers(bigint);

CREATE OR REPLACE FUNCTION imr_depth_layers(id_value_in bigint)
  RETURNS character varying AS
$BODY$
  DECLARE
     depthstring varchar;
	 c_depthlayer cursor (myvalue_id bigint) is 
	 select CAST ( v.type AS int2 ),  
	 CAST ( COALESCE(v.fromdepth,0.0) AS float4 )  , 
	 CAST ( COALESCE(v.todepth,0.0) AS float4 ), 
	 CAST ( COALESCE(v.layerthickness,0.0) AS float4 ) 
	 FROM vcell as v, valuexvcell as vxv 
	 WHERE vxv.id_vcell = v.id and vxv.id_value = myvalue_id
	 ORDER BY v.id;
	 v_type int2;
	 v_fromdepth float4;
	 v_todepth float4;
     v_layerthickness float4;
  BEGIN
	 depthstring := '(';
     OPEN c_depthlayer(id_value_in);
     LOOP
	     FETCH c_depthlayer into v_type, v_fromdepth, v_todepth, v_layerthickness;
		 IF NOT FOUND THEN
		   close c_depthlayer;
		   IF (depthstring = '(') THEN
		      depthstring := NULL;
		   ELSE
	          depthstring := substr(depthstring,1, (length(depthstring) - 1) ) || ')';
		   END IF;
	       RETURN depthstring;
		 ELSE
		    IF (v_type = 1)  THEN
		      depthstring := depthstring || 'F;';
     	    ELSIF (v_type = 2)  THEN
		      depthstring := depthstring || 'W;';
     	    ELSIF (v_type = 3)  THEN
		      depthstring := depthstring || 'L'||trim(to_char(v_fromdepth,'9990.9'))||':'||trim(to_char(v_todepth,'9990.9'))||';';
     	    ELSIF (v_type = 4)  THEN
		      depthstring := depthstring || 'B'||trim(to_char(v_layerthickness,'990.9'))||';';
		    ELSE
		      depthstring := depthstring || ';';
		    END IF;	 
		 END IF;
	 END LOOP; 
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_depth_layers(bigint)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_depth_layers(bigint) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_depth_layers(bigint) TO public;
GRANT EXECUTE ON FUNCTION imr_depth_layers(bigint) TO grid;
