-- Function: imr_getstrataname(character varying, real, real)

-- DROP FUNCTION imr_getstrataname(character varying, real, real);

CREATE OR REPLACE FUNCTION imr_getstrataname(
    stratasystemname_in character varying,
    longitude_in real,
    latitude_in real)
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
	 point_ewkt_in varchar;


  BEGIN

	 output := '';
	 point_ewkt_in := 'SRID=4326;POINT(' || to_char(longitude_in,'099.99999') || ' ' || to_char(latitude_in,'099.99999') || ')'; 
	 
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
ALTER FUNCTION imr_getstrataname(character varying, real, real)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_getstrataname(character varying, real, real) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_getstrataname(character varying, real, real) TO public;
