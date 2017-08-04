-- Function: imr_getmaxval(bigint, bigint, bigint, bigint)

-- DROP FUNCTION imr_getmaxval(bigint, bigint, bigint, bigint);

CREATE OR REPLACE FUNCTION imr_getmaxval(
    id_grid_in bigint,
    id_parameter_in bigint,
    id_vcell_in bigint,
    id_tcell_in bigint)
  RETURNS real AS
$BODY$
  DECLARE
     v_return real;
	 c_statistics cursor for
    SELECT  maxval 
     FROM parameter_statistics 
     WHERE id_grid = id_grid_in AND id_parameter = id_parameter_in AND id_vcell = id_vcell_in AND id_tcell = id_tcell_in; 

  BEGIN
	 v_return := null;
     OPEN c_statistics;

	     FETCH c_statistics into v_return;
		 IF NOT FOUND THEN
		   close c_statistics;
	       RETURN null;
		 ELSE
		 close c_statistics;
           RETURN v_return;
		 END IF;

  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_getmaxval(bigint, bigint, bigint, bigint)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_getmaxval(bigint, bigint, bigint, bigint) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_getmaxval(bigint, bigint, bigint, bigint) TO public;
