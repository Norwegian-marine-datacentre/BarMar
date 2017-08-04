-- Function: imr_symbolsize(bigint, bigint, bigint, bigint, real, real)

-- DROP FUNCTION imr_symbolsize(bigint, bigint, bigint, bigint, real, real);

CREATE OR REPLACE FUNCTION imr_symbolsize(
    id_grid_in bigint,
    id_parameter_in bigint,
    id_vcell_in bigint,
    id_tcell_in bigint,
    value_in real,
    maxsymbolsize_in real)
  RETURNS real AS
$BODY$
  DECLARE
     v_return real;
	 v_maxval real;
	 v_minval real;
	 v_maxsymbolsize real;
	 v_value real;
  BEGIN
	 v_return := null;
	 v_maxval := imr_getmaxval(id_grid_in,id_parameter_in,id_vcell_in,id_tcell_in);
	 v_minval := imr_getminval(id_grid_in,id_parameter_in,id_vcell_in,id_tcell_in);
	 v_maxsymbolsize := maxsymbolsize_in;
	 v_value := value_in;
	 IF (v_maxval IS NULL OR v_minval IS NULL) THEN
	    RETURN NULL;
	 ELSE
	   IF (v_value = 0) THEN
		  RETURN 0;
	   END IF;	   
	   IF (v_maxval = v_minval) THEN
	     RETURN 2.0;
	   ELSE
	     v_return := (((v_value - v_minval)/(v_maxval - v_minval)) + 0.1)*v_maxsymbolsize;
	     RETURN v_return;
	   END IF;
	 END IF;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_symbolsize(bigint, bigint, bigint, bigint, real, real)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_symbolsize(bigint, bigint, bigint, bigint, real, real) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_symbolsize(bigint, bigint, bigint, bigint, real, real) TO public;
