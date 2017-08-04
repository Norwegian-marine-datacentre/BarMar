-- Function: imr_getstrataname(character varying, character varying)

-- DROP FUNCTION imr_getstrataname(character varying, character varying);

CREATE OR REPLACE FUNCTION imr_getstrataname(
    stratasystemname_in character varying,
    point_ewkt_in character varying)
  RETURNS character varying AS
$BODY$
  DECLARE
	 -- stratasystemname: The name of the "multipolygon strata-system" that you want to check if the point is inside one of the strata
	 -- point_in_ewkt: example 'SRID=4326;POINT(20.00 75.65)' - 20.00 = longitude
	 --
	 -- The output is an empty string or the name of the strata
	 --
	 --
	 c_getstrataname cursor (stratasystemname varchar, point_ewkt character varying) is 
	                select h.name FROM hcell as h WHERE h.id_grid = (select id from grid where name = stratasystemname) and st_within(st_geomfromewkt(point_ewkt) ,h.geoshape);
	 output varchar;

  BEGIN
	 output := '';
	 
	 OPEN c_getstrataname(stratasystemname_in, point_ewkt_in);
     FETCH c_getstrataname into output;
	 close c_getstrataname;
	 IF NOT FOUND THEN
       RETURN NULL;
     ELSE
	   RETURN output;
	 END IF;

  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_getstrataname(character varying, character varying)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_getstrataname(character varying, character varying) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_getstrataname(character varying, character varying) TO public;
