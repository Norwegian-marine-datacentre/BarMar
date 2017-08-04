-- Function: imr_insert_parameter_statistics()

-- DROP FUNCTION imr_insert_parameter_statistics();

CREATE OR REPLACE FUNCTION imr_insert_parameter_statistics()
  RETURNS boolean AS
$BODY$
  DECLARE
     v_return boolean;
	 c_statistics cursor for
   SELECT  to_char(count(*),'99999999999') as nval, to_char(max(v.value),'999999999999.99999') as maxval, to_char(min(v.value),'999999999999.99999') as minval,
           to_char(g.id,'99999999999') as id_grid, to_char(p.id,'99999999999') as id_parameter, to_char(vc.id,'99999999999') as id_vcell, to_char(tc.id,'99999999999') as id_tcell
     FROM grid g, hcell h, value v, parameter p, valuexvcell vxv, valuextcell vxt, tcell tc, vcell vc  
     WHERE h.id_grid = g.id AND v.id_hcell = h.id AND v.id_parameter = p.id AND vxt.id_value = v.id AND vxt.id_tcell = tc.id 
         AND vxv.id_value = v.id AND vxv.id_vcell = vc.id
	 GROUP BY g.id,p.id,vc.id,tc.id;	 
     v_id_parameter character varying;
	 v_id_vcell character varying;
	 v_id_grid character varying;
	 v_id_tcell character varying;
	 v_maxval character varying;
	 v_minval character varying;
	 v_nval character varying;
  BEGIN
	 v_return := 'true';
     OPEN c_statistics;
     EXECUTE 'delete from  parameter_statistics';
     LOOP
	     FETCH c_statistics into v_nval,v_maxval,v_minval,v_id_grid,v_id_parameter,v_id_vcell,v_id_tcell ;
		 IF NOT FOUND THEN
		   close c_statistics;
	       RETURN v_return;
		 ELSE
           EXECUTE 'insert into parameter_statistics(nval,maxval,minval,id_grid,id_parameter,id_vcell,id_tcell) values (' ||
		   v_nval || ',' || v_maxval || ',' || v_minval || ',' || v_id_grid || ',' || v_id_parameter || ',' || v_id_vcell || ',' || v_id_tcell || ')';	 
		 END IF;
	 END LOOP; 
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_insert_parameter_statistics()
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_parameter_statistics() TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_insert_parameter_statistics() TO public;
