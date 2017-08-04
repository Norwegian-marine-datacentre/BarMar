-- Function: imr_depthlayer_name(bigint)

-- DROP FUNCTION imr_depthlayer_name(bigint);

CREATE OR REPLACE FUNCTION imr_depthlayer_name(id_in bigint)
  RETURNS character varying AS
$BODY$
  DECLARE
     depthstring varchar;
	 c_depthlayer cursor (my_id bigint) is select CAST ( v.type AS int2 ),  CAST ( COALESCE(v.fromdepth,0.0) AS float4 )  , CAST ( COALESCE(v.todepth,0.0) AS float4 ), CAST ( COALESCE(v.layerthickness,0.0) AS float4 ) FROM vcell as v WHERE v.id = my_id;
	 v_type int2;
	 v_fromdepth float4;
	 v_todepth float4;
     v_layerthickness float4;
  BEGIN
	 depthstring := '';
     OPEN c_depthlayer(id_in);
	 FETCH c_depthlayer into v_type, v_fromdepth, v_todepth, v_layerthickness;
	 IF NOT FOUND THEN
		close c_depthlayer;
	    RETURN depthstring;
	 ELSE
		IF (v_type = 1)  THEN
		  depthstring := depthstring || 'F';
     	ELSIF (v_type = 2)  THEN
		  depthstring := depthstring || 'W';
     	ELSIF (v_type = 3)  THEN
		  depthstring := depthstring || 'L'||trim(to_char(v_fromdepth,'9990.9'))||':'||trim(to_char(v_todepth,'9990.9'));
     	ELSIF (v_type = 4)  THEN
		  depthstring := depthstring || 'B'||trim(to_char(v_layerthickness,'990.9'));
		ELSE
		  depthstring := depthstring || '';
		END IF;	 
        close c_depthlayer;
	    RETURN depthstring;		 
	 END IF;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_depthlayer_name(bigint)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_depthlayer_name(bigint) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_depthlayer_name(bigint) TO public;
