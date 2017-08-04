-- Function: imr_insert_unique_timeperiods()

-- DROP FUNCTION imr_insert_unique_timeperiods();

CREATE OR REPLACE FUNCTION imr_insert_unique_timeperiods()
  RETURNS boolean AS
$BODY$
  DECLARE
     v_return boolean;
	 c_timeperiod cursor for 
                 select distinct to_char(tc.id,'99999999999'), to_char(v.id_parameter,'9999999999'), to_char(h.id_grid,'9999999999')  
                 from tcell as tc, valuextcell as vxt, value as v, hcell as h
                 where vxt.id_tcell = tc.id and vxt.id_value = v.id and v.id_hcell = h.id;
     v_id_parameter character varying;
	 v_id_tcell character varying;
	 v_id_grid character varying;
  BEGIN
	 v_return := 'true';
     OPEN c_timeperiod;
     EXECUTE 'delete from parameter_tcell';
     LOOP
	     FETCH c_timeperiod into v_id_tcell, v_id_parameter, v_id_grid;
		 IF NOT FOUND THEN
		   close c_timeperiod;
	       RETURN v_return;
		 ELSE
           EXECUTE 'insert into parameter_tcell(id_grid,id_parameter,id_tcell) values ( ' || v_id_grid || ',' || v_id_parameter || ',' || v_id_tcell || ')';	 
		 END IF;
	 END LOOP; 
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_insert_unique_timeperiods()
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_unique_timeperiods() TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_unique_timeperiods() TO public;
