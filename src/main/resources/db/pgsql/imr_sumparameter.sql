-- Function: imr_sumparameter(character varying, character varying, character varying, character varying, character varying, character varying)

-- DROP FUNCTION imr_sumparameter(character varying, character varying, character varying, character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION imr_sumparameter(
    gridname_in character varying,
    firstpartofparname_in character varying,
    lastpartofparname_in character varying,
    resultparname_in character varying,
    depthlayer_in character varying,
    period_in character varying)
  RETURNS bigint AS
$BODY$
  DECLARE
	 -- USAGE: select imr_sumparameter('FishExChange', 'Cod_survey_trawl_ecosystem_%', '%cm', 'Cod_survey_trawl_ecosystem_total', 'B010.00', 'Q20103');	 
	 v_nvalues bigint := null; -- number of values inserted into the database
	 v_id_grid bigint := null;
	 v_id_result_parameter bigint := null;
	 v_id_parameters bigint[];
	 v_id_parameter bigint;
	 v_id_depthlayer bigint := null;
	 v_id_timeperiod bigint := null;
	 v_id_hcell bigint;
	 v_value real;
	 v_next_id_value bigint;
	 v_value_sum real;
	 i int;
	 --c_parameters cursor (firstpartofparname character varying, lastpartofparname character varying) is select id from parameter where name like firstpartofparname and name like lastpartofparname;
  BEGIN
     EXECUTE 'select max(id) + 1 from value;' INTO v_next_id_value;
	 v_nvalues := 0;
	 -- Grid to enter values into
	 EXECUTE 'select id from grid where name = ' || quote_literal(gridname_in) || ';' INTO v_id_grid;
	 IF (v_id_grid IS NULL) THEN
	    v_nvalues := -1;
		RETURN v_nvalues;
	 END IF;
	 -- Parameter to enter values into
	 EXECUTE 'select id from parameter where name = ' || quote_literal(resultparname_in) || ';' INTO v_id_result_parameter;
	 IF (v_id_result_parameter IS NULL) THEN
	    v_nvalues := -2;
		RETURN v_nvalues;
	 END IF;	 
	 -- Depth layer to enter values into
	 EXECUTE 'select id from vcell where name = ' || quote_literal(depthlayer_in) || ';' INTO v_id_depthlayer;
	 IF (v_id_depthlayer IS NULL) THEN
	    v_nvalues := -3;
		RETURN v_nvalues;
	 END IF;
	 -- Time period to enter values into
	 EXECUTE 'select id from tcell where name = ' || quote_literal(period_in) || ';' INTO v_id_timeperiod;
	 IF (v_id_timeperiod IS NULL) THEN
	    v_nvalues := -4;
		RETURN v_nvalues;
	 END IF;
     i := 0;
     FOR v_id_parameter in select id from parameter where name like firstpartofparname_in and name like lastpartofparname_in 
     LOOP
        v_id_parameters[i] := v_id_parameter;
        i := i + 1;
     END LOOP;
	 IF (i = 0) THEN
	    v_nvalues := -5;
		RETURN v_nvalues;
	 END IF;	 
	 
     FOR v_id_hcell in select id from hcell where id_grid = v_id_grid 
     LOOP
	    v_value_sum := null;
        for i in array_lower(v_id_parameters,1) .. array_upper(v_id_parameters,1) loop
          v_id_parameter := v_id_parameters[i];
		  v_value := null;
          EXECUTE 'select v.value from value as v, valuextcell as vxt, valuexvcell as vxv where ' ||
		          ' vxt.id_value = v.id and vxv.id_value = v.id and ' ||
				  ' vxt.id_tcell = ' || to_char(v_id_timeperiod,'9999999') || ' and ' ||
				  ' vxv.id_vcell = ' || to_char(v_id_depthlayer,'9999999') || ' and ' ||
          		  ' v.id_parameter = ' || to_char(v_id_parameter,'9999999') || ' and ' ||
		          ' v.id_hcell = ' || to_char(v_id_hcell,'9999999') || ';' INTO  v_value;
		  IF (v_value IS NOT NULL) THEN
		     IF (v_value_sum IS NULL) THEN
			    v_value_sum := 0;
			 END IF;
			 v_value_sum := v_value_sum + v_value;
          END IF;
        end loop;
	 	IF (v_value_sum IS NOT NULL) THEN
		   EXECUTE 'insert into value(id,id_parameter,id_hcell,value,fromdepth,todepth,fromdate,todate,info,date_first_entered,date_last_revised) values (' ||
		   to_char(v_next_id_value,'9999999') || ',' || to_char(v_id_result_parameter,'9999999') || ',' || to_char(v_id_hcell,'9999999') || ',' || to_char(v_value_sum,'9999999') || ',' ||
		   'null,null,null,null,null,now(),now());';
		   EXECUTE 'insert into valuexvcell(id_value,id_vcell) values (' ||
		   to_char(v_next_id_value,'9999999') || ',' || to_char(v_id_depthlayer,'9999999') || ');';
		   EXECUTE 'insert into valuextcell(id_value,id_tcell) values (' ||
		   to_char(v_next_id_value,'9999999') || ',' || to_char(v_id_timeperiod,'9999999') || ');';
		   v_nvalues := v_nvalues + 1;
        END IF;
        v_next_id_value := v_next_id_value + 1; 		
     END LOOP;	 
	 
     RETURN v_nvalues; 
	 
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_sumparameter(character varying, character varying, character varying, character varying, character varying, character varying)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_sumparameter(character varying, character varying, character varying, character varying, character varying, character varying) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_sumparameter(character varying, character varying, character varying, character varying, character varying, character varying) TO public;
