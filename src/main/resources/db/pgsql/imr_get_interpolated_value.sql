-- Function: imr_get_interpolated_value(bigint, bigint)

-- DROP FUNCTION imr_get_interpolated_value(bigint, bigint);

CREATE OR REPLACE FUNCTION imr_get_interpolated_value(
    id_value_in bigint,
    id_metadata_to_in bigint)
  RETURNS double precision AS
$BODY$
  DECLARE
	 -- id_value_in: The id value that you want to find the best possible value for from another dataset (example you have an herring_amount you want the temperature)
	 -- id_metadata_to: The dataset that you want find a "corresponding" value in. (id_metadata_to will give you both id_parameter and id_grid)
	 --
     id_parameter_to bigint;
	 id_grid_to bigint;
	 hcell_in geometry; -- The geometry that the ingoing value belongs to
	 id_timeperiod_in bigint; 
	 area_hcell_km2_in float;
	 hcell_ids_to bigint[];
	 hcell_id_to bigint;
	 i int;
	 result float;
	 sqlstatement character varying;
	 
	 c_hcell_ids_to cursor (the_hcell geometry, the_grid_id_to bigint) is 
	                select h.id FROM hcell as h WHERE h.id_grid = the_grid_id_to and st_intersects(the_hcell,h.geoshape);

  BEGIN
	 result := -99.99;
	 sqlstatement := 'select id_grid from metadata where id = '||to_char(id_metadata_to_in,'99999999');
	 EXECUTE sqlstatement into id_grid_to;
	 sqlstatement := 'select id_parameter from metadata where id = '||to_char(id_metadata_to_in,'99999999');
	 EXECUTE sqlstatement into id_parameter_to;
	 sqlstatement := 'select h.geoshape from hcell as h, value as v where v.id_hcell = h.id and v.id = '||to_char(id_value_in,'99999999');
	 EXECUTE sqlstatement into hcell_in;
	 -- Area of the polygon: select st_area2d(st_transform(hcell_in,3035))/1000000.0 into area_hcell_km2_in;
	 -- Finds the grid-cell id's in the "to"-grid:
	 i := 0;
	 OPEN c_hcell_ids_to(hcell_in,id_grid_to);
	 LOOP
	   FETCH c_hcell_ids_to into hcell_id_to;
       IF NOT FOUND THEN
           close c_hcell_ids_to;
           EXIT;
       END IF;
	   hcell_ids_to[i] := hcell_id_to;
       i := i + 1;
	 END LOOP;	 
	 
	 result := array_upper(hcell_ids_to,1);
	 
	 RETURN result;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_get_interpolated_value(bigint, bigint)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_get_interpolated_value(bigint, bigint) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_get_interpolated_value(bigint, bigint) TO public;
