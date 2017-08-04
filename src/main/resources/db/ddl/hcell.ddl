-- Table: hcell

-- DROP TABLE hcell;

CREATE TABLE hcell
(
  id bigserial NOT NULL,
  id_grid bigint NOT NULL,
  i bigint NOT NULL, -- index in longitude direction  values 0..(i_max-1)
  j bigint NOT NULL, -- index in latitude direction values 0..(j_max-1)
  area double precision, -- Area of grid_cell in km^2
  seaarea double precision, -- Area of gridcell in km^2 that is at sea
  minlon real, -- Min longitude of grid cell in decimal degrees
  maxlon real, -- Maximum longitude of grid cell in decimal degrees
  minlat real, -- Minimum latitude of grid cell in decimal degrees
  maxlat real, -- Maximum latitude of grid cell in decimal degrees
  mindepth real, -- Minimum depth of grid cell in meters. Negative values at sea.
  maxdepth real, -- Maximum depth of cell. Negative values at sea.
  meandepth real, -- Mean depth in grid cell in meters. Negative values at sea.
  lonsize real, -- Size in km of longitude direction of grid cell in km. Value only for square grid cells.
  latsize real, -- Size in km of latitude direction of grid cell. Value only for square grid cells.
  geoshape geometry, -- The polygon of the grid cell it may be a multipolygon if clipped to e.g. a coastline. Usually it is a square grid cell i.e. a polygon with four vertexes.
  name character varying(16), -- The unique name of the hcell within the grid usually on the form YJXI, but could alaso hav name e.g. Statistical area
  variancedepth real,
  geopoint geometry,
  CONSTRAINT pkey_hcell PRIMARY KEY (id),
  CONSTRAINT fkey_hcell_grid FOREIGN KEY (id_grid)
      REFERENCES grid (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT enforce_dims_geopoint CHECK (ndims(geopoint) = 2),
  CONSTRAINT enforce_dims_geoshape CHECK (ndims(geoshape) = 2),
  CONSTRAINT enforce_geotype_geopoint CHECK (geometrytype(geopoint) = 'POINT'::text OR geopoint IS NULL),
  CONSTRAINT enforce_geotype_geoshape CHECK (geometrytype(geoshape) = 'MULTIPOLYGON'::text OR geoshape IS NULL),
  CONSTRAINT enforce_srid_geopoint CHECK (srid(geopoint) = 4326),
  CONSTRAINT enforce_srid_geoshape CHECK (srid(geoshape) = 4326)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE hcell
  OWNER TO grid_owner;
GRANT ALL ON TABLE hcell TO grid_owner;
GRANT SELECT ON TABLE hcell TO grid_reader;
COMMENT ON COLUMN hcell.i IS 'index in longitude direction  values 0..(i_max-1)';
COMMENT ON COLUMN hcell.j IS 'index in latitude direction values 0..(j_max-1)';
COMMENT ON COLUMN hcell.area IS 'Area of grid_cell in km^2';
COMMENT ON COLUMN hcell.seaarea IS 'Area of gridcell in km^2 that is at sea';
COMMENT ON COLUMN hcell.minlon IS 'Min longitude of grid cell in decimal degrees';
COMMENT ON COLUMN hcell.maxlon IS 'Maximum longitude of grid cell in decimal degrees';
COMMENT ON COLUMN hcell.minlat IS 'Minimum latitude of grid cell in decimal degrees';
COMMENT ON COLUMN hcell.maxlat IS 'Maximum latitude of grid cell in decimal degrees';
COMMENT ON COLUMN hcell.mindepth IS 'Minimum depth of grid cell in meters. Negative values at sea.';
COMMENT ON COLUMN hcell.maxdepth IS 'Maximum depth of cell. Negative values at sea.';
COMMENT ON COLUMN hcell.meandepth IS 'Mean depth in grid cell in meters. Negative values at sea.';
COMMENT ON COLUMN hcell.lonsize IS 'Size in km of longitude direction of grid cell in km. Value only for square grid cells.';
COMMENT ON COLUMN hcell.latsize IS 'Size in km of latitude direction of grid cell. Value only for square grid cells.';
COMMENT ON COLUMN hcell.geoshape IS 'The polygon of the grid cell it may be a multipolygon if clipped to e.g. a coastline. Usually it is a square grid cell i.e. a polygon with four vertexes.';
COMMENT ON COLUMN hcell.name IS 'The unique name of the hcell within the grid usually on the form YJXI, but could alaso hav name e.g. Statistical area';


-- Index: fki_fkey_hcell_grid

-- DROP INDEX fki_fkey_hcell_grid;

CREATE INDEX fki_fkey_hcell_grid
  ON hcell
  USING btree
  (id_grid);

-- Index: gist_hcell_geopoint

-- DROP INDEX gist_hcell_geopoint;

CREATE INDEX gist_hcell_geopoint
  ON hcell
  USING gist
  (geopoint);

-- Index: gist_hcell_geoshape

-- DROP INDEX gist_hcell_geoshape;

CREATE INDEX gist_hcell_geoshape
  ON hcell
  USING gist
  (geoshape);

-- Index: index_hcell_minlon

-- DROP INDEX index_hcell_minlon;

CREATE INDEX index_hcell_minlon
  ON hcell
  USING btree
  (minlon);

-- Index: indx_hcell_i

-- DROP INDEX indx_hcell_i;

CREATE INDEX indx_hcell_i
  ON hcell
  USING btree
  (i);

-- Index: indx_hcell_j

-- DROP INDEX indx_hcell_j;

CREATE INDEX indx_hcell_j
  ON hcell
  USING btree
  (j);

-- Index: indx_hcell_maxlat

-- DROP INDEX indx_hcell_maxlat;

CREATE INDEX indx_hcell_maxlat
  ON hcell
  USING btree
  (maxlat);

-- Index: indx_hcell_maxlon

-- DROP INDEX indx_hcell_maxlon;

CREATE INDEX indx_hcell_maxlon
  ON hcell
  USING btree
  (maxlon);

-- Index: indx_hcell_minlat

-- DROP INDEX indx_hcell_minlat;

CREATE INDEX indx_hcell_minlat
  ON hcell
  USING btree
  (minlat);

-- Index: indx_hcell_name

-- DROP INDEX indx_hcell_name;

CREATE INDEX indx_hcell_name
  ON hcell
  USING btree
  (name COLLATE pg_catalog."default");

