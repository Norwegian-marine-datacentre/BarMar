-- Function: imr_insert_unique_depthlayers()

-- DROP FUNCTION imr_insert_unique_depthlayers();

CREATE OR REPLACE FUNCTION imr_insert_unique_depthlayers()
  RETURNS boolean AS
$BODY$
  DECLARE
     v_return boolean;
	 c_depthlayer cursor for 
                 select distinct to_char(vc.id,'99999999999'), to_char(v.id_parameter,'9999999999'), to_char(h.id_grid,'9999999999') 
                 from vcell as vc, valuexvcell as vxv, value as v, hcell as h 
                 where vxv.id_vcell = vc.id and vxv.id_value = v.id and v.id_hcell = h.id;
     v_id_parameter character varying;
	 v_id_vcell character varying;
	 v_id_grid character varying;
  BEGIN
	 v_return := 'true';
     OPEN c_depthlayer;
     EXECUTE 'delete from parameter_vcell';
     LOOP
	     FETCH c_depthlayer into v_id_vcell, v_id_parameter, v_id_grid;
		 IF NOT FOUND THEN
		   close c_depthlayer;
	       RETURN v_return;
		 ELSE
           EXECUTE 'insert into parameter_vcell(id_grid,id_parameter,id_vcell) values ( ' || v_id_grid || ',' || v_id_parameter || ',' || v_id_vcell || ')';	 
		 END IF;
	 END LOOP; 
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_insert_unique_depthlayers()
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_unique_depthlayers() TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_unique_depthlayers() TO public;
