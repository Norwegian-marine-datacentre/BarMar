-- Function: imr_geopoint_with_avg_min_max_value(text, text, text, text, text)

-- DROP FUNCTION imr_geopoint_with_avg_min_max_value(text, text, text, text, text);

CREATE OR REPLACE FUNCTION imr_geopoint_with_avg_min_max_value(
    IN agridname text,
    IN parameter_ids text,
    IN depthlayernametext text,
    IN periodnametext text,
    IN aggregationfunc text)
  RETURNS TABLE(id bigint, geopoint geometry, gridname character varying, gridcellname character varying, parametername character varying, parameter_id bigint, depthlayername character varying, periodname character varying, value double precision) AS
$BODY$	
    DECLARE
      sql text;
      parameter_idsIn text[];
      depthlayernameIn text[];
      periodnameIn text[];
    BEGIN
     sql := $$select 
	DISTINCT ON (h.name) v.id, h.geopoint, g.name as gridname, h.name as gridcellname, p.name as parametername, p.id as parameter_id, vc.name as depthlayername, tc.name as periodname, $$;
     IF aggregationFunc = 'avg' THEN
      	sql := sql || $$ avg(v.value) OVER (PARTITION BY h.name) as value $$;
     ELSIF aggregationFunc = 'min' THEN
	sql := sql || $$ cast(min(v.value) OVER (PARTITION BY h.name) as double precision) as value $$;
     ELSIF aggregationFunc = 'relative_std_dev' THEN
        sql := sql || $$ (CASE stddev_samp(v.value) OVER (PARTITION BY h.name) WHEN 0 THEN 0 ELSE (stddev_samp(v.value) OVER (PARTITION BY h.name) ) / ( avg(v.value) OVER (PARTITION BY h.name) ) END) as value $$;
     END IF;
     sql := sql || $$ FROM grid g, hcell h, value v, parameter p, valuexvcell vxv, valuextcell vxt, tcell tc, vcell vc
        WHERE h.id_grid = g.id AND v.id_hcell = h.id AND v.id_parameter = p.id AND vxt.id_value = v.id AND vxt.id_tcell = tc.id AND vxv.id_value = v.id AND vxv.id_vcell = vc.id AND
	g.name = '$$ || agridname || $$' AND ($$;

     parameter_idsIn := string_to_array(parameter_ids, ' ');
     depthlayernameIn := string_to_array(depthlayernameText, ' ');
     periodnameIn := string_to_array(periodnameText, ' ');

     FOR i IN 1 .. array_upper(parameter_idsIn, 1)
	LOOP
	sql := sql || $$p.id =$$ || parameter_idsIn[i] || $$ $$;
	IF i != array_upper(parameter_idsIn, 1) THEN
	  sql := sql || $$ OR $$;
	END IF;
     END LOOP;

     sql := sql || $$) AND ($$;     
     FOR i IN 1 .. array_upper(depthlayernameIn, 1)
	LOOP
	sql := sql || $$vc.name ='$$ || depthlayernameIn[i] || $$'$$;
	IF i != array_upper(depthlayernameIn, 1) THEN
	  sql := sql || $$ OR $$;
	END IF;
     END LOOP;

     sql := sql || $$) AND ($$;
     FOR i IN 1 .. array_upper(periodnameIn, 1)
	LOOP
	sql := sql || $$tc.name ='$$ || periodnameIn[i] || $$'$$;
	IF i != array_upper(periodnameIn, 1) THEN
	  sql := sql || $$ OR $$;
	END IF;
     END LOOP;
     sql := sql || $$) $$;
	RAISE NOTICE '%',sql;
     RETURN QUERY EXECUTE sql;
    END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION imr_geopoint_with_avg_min_max_value(text, text, text, text, text)
  OWNER TO grid_owner;
