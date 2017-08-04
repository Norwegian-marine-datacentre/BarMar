-- Function: imr_insert_warehouse()

-- DROP FUNCTION imr_insert_warehouse();

CREATE OR REPLACE FUNCTION imr_insert_warehouse()
  RETURNS boolean AS
$BODY$
  DECLARE
     v_return boolean;
  BEGIN
    v_return := false;
	IF (imr_insert_unique_timeperiods()) THEN
	  IF (imr_insert_unique_depthlayers()) THEN
	    IF (imr_insert_parameter_statistics()) THEN
	       v_return := true;
	    END IF;
	  END IF;
	END IF;
	RETURN v_return;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_insert_warehouse()
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_warehouse() TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_warehouse() TO public;
