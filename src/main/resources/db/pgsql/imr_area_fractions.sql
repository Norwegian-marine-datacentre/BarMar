-- Function: imr_area_fractions(character varying, character varying, character varying)

-- DROP FUNCTION imr_area_fractions(character varying, character varying, character varying);

CREATE OR REPLACE FUNCTION imr_area_fractions(
    gridname_from character varying,
    cellname_from character varying,
    gridname_to character varying)
  RETURNS character varying AS
$BODY$
  DECLARE
	 -- gridname_from: The name of the "multipolygon strata-system" that you want to "distribute" into another strata-system
	 -- cellname_from: The name of the "cell" in the "multipolygon strata-system" that you want to "distribute" into another strata-system
	 -- gridname_to:   The name of the "multipolygon strata-system" that you want the "cellname_from" to be distributed to
	 --
	 -- The output is of the form: "(.......;gridname_to_cellname_i,fraction_of_area_of_gridname_from_cellname_that_lies_in_gridname_to_cellname_i;.....)"
	 --
	 -- e.g. INPUT: select imr_area_fractions('FishExChange','Y28X16','WMOsquares');
	 -- OUTPUT: "(X32Y44,0.1;X33Y44;0.9;X34Y44,0.1)" i.e.: 0.1 + 0.9 + 0.1 = 1.0 (will be the result if the input cell is within the gridname_to total area.)
	 --
	 c_from cursor (my_gridname_from varchar, my_cellname_from varchar) is 
	                select h.geoshape FROM hcell as h WHERE h.id_grid = (select id from grid where name = my_gridname_from) and h.name = my_cellname_from;
	 c_to cursor (my_gridname_to varchar) is select h.geoshape, h.name FROM hcell as h WHERE h.id_grid = (select id from grid where name = my_gridname_to);
	 gridcellpolygon_from geometry;
	 area_gridcellpolygon_from float;
	 gridcellpolygon_to geometry;
	 gridcellpolygon_to_name varchar;
	 fraction_area float;
	 overlap_area float;
	 output varchar;
     a_polygon geometry;
	 n_polygon integer;
	 i_polygon integer;

  BEGIN
	 output := '( ';
	 
	 -- Gets the multipolygon for the from_cell:
	 
	 OPEN c_from(gridname_from, cellname_from);
     FETCH c_from into gridcellpolygon_from;
	 close c_from;
	 IF gridcellpolygon_from IS NULL THEN
       RETURN 'no result';
     ELSE
	   --
	   -- Now loops all the gridcells (multipolygons) in the gridname_to strata-system
	   --
	   select area2d(transform(gridcellpolygon_from,3035))/1000000.0 into area_gridcellpolygon_from;
	   OPEN c_to(gridname_to);
       LOOP
	     FETCH c_to into gridcellpolygon_to, gridcellpolygon_to_name;
		 IF NOT FOUND THEN
		   close c_to;
		   RETURN substr(output,1, (length(output) - 1) ) || ')';
		 ELSE
		   IF intersects(gridcellpolygon_from,gridcellpolygon_to) THEN
             IF within(gridcellpolygon_from,gridcellpolygon_to) THEN
			   output := output || gridcellpolygon_to_name || ',' || '1.0' || ';';
			 ELSE
			   -- now we have to loop the single polygons in the multipolygon "gridcellpolygon_to" to find overlapping area:
			   overlap_area:= 0.0;
  	           select numgeometries(gridcellpolygon_to) into n_polygon;
	           i_polygon := 1;
	           WHILE (i_polygon <= n_polygon) LOOP
                 select geometryn(gridcellpolygon_to,i_polygon) into a_polygon;
     	         select imr_intersection_area_km2(gridcellpolygon_from, a_polygon) into fraction_area;
		         overlap_area := overlap_area + fraction_area;
				 i_polygon := i_polygon + 1;
		       END LOOP;	   
			   output := output || gridcellpolygon_to_name || ',' || trim(to_char(overlap_area/area_gridcellpolygon_from,'0.99999')) || ';';
			 END IF;
		   END IF;
		 END IF;		 
	   END LOOP;	   	   
	 END IF;
  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_area_fractions(character varying, character varying, character varying)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_area_fractions(character varying, character varying, character varying) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_area_fractions(character varying, character varying, character varying) TO public;
