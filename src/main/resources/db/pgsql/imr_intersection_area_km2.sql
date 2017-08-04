-- Function: imr_intersection_area_km2(geometry, geometry)

-- DROP FUNCTION imr_intersection_area_km2(geometry, geometry);

CREATE OR REPLACE FUNCTION imr_intersection_area_km2(
    multipolygon_in geometry,
    polygon_in geometry)
  RETURNS real AS
$BODY$
  DECLARE
     a_polygon geometry;
	 n_polygon integer;
	 i_polygon integer;
	 intersection_area float;
	 sum_intersection_area float;
	 bintersects boolean;
	 bwithin boolean;
  BEGIN
	 -- The intersection area between the multipolygon_in and the polygon_in is computed in square kilometres
	 -- Trond Westgard, IMR, august 2009
	 --
	 intersection_area := 0;
	 sum_intersection_area := 0;
	 select intersects(multipolygon_in,polygon_in) into bintersects;
	 IF bintersects THEN
	   select within(polygon_in,multipolygon_in) into bwithin;
	   IF bwithin THEN
	     select area2d(transform(polygon_in,3035))/1000000.0 into intersection_area;
		 RETURN intersection_area;
	   ELSE
  	     select numgeometries(multipolygon_in) into n_polygon;
	     i_polygon := 1;
	     WHILE (i_polygon <= n_polygon) LOOP
           select geometryn(multipolygon_in,i_polygon) into a_polygon;
           IF NOT FOUND THEN
		     RETURN -99.99;
	       END IF;
           IF a_polygon IS NULL THEN
             RETURN -99.99;
    	   END IF;
           select area2d(transform(intersection(a_polygon,polygon_in),3035))/1000000.0 into intersection_area;
           IF intersection_area IS NULL OR  intersection_area = 0.0 THEN
             sum_intersection_area := sum_intersection_area;
	       ELSE
		     sum_intersection_area := sum_intersection_area + intersection_area;
	       END IF;
	       i_polygon := i_polygon + 1;
         END LOOP;
	     RETURN sum_intersection_area;
	   END IF;
	 ELSE
	   RETURN 0.0;
	 END IF;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_intersection_area_km2(geometry, geometry)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_intersection_area_km2(geometry, geometry) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_intersection_area_km2(geometry, geometry) TO public;
