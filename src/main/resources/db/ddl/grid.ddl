-- Table: grid

-- DROP TABLE grid;

CREATE TABLE grid
(
  id bigserial NOT NULL,
  name character varying(256) NOT NULL,
  description character varying(1024),
  i_max bigint NOT NULL, -- Number of cells in longitude direction
  j_max bigint NOT NULL, -- Number of cells in latitude direction
  gridtype bigint, -- 1=rectangular grid,2=irregular strata grid, 3=clip grid
  CONSTRAINT pkey_grid PRIMARY KEY (id),
  CONSTRAINT unique_name UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE grid
  OWNER TO grid_owner;
GRANT ALL ON TABLE grid TO grid_owner;
GRANT SELECT ON TABLE grid TO grid_reader;
COMMENT ON COLUMN grid.i_max IS 'Number of cells in longitude direction';
COMMENT ON COLUMN grid.j_max IS 'Number of cells in latitude direction';
COMMENT ON COLUMN grid.gridtype IS '1=rectangular grid,2=irregular strata grid, 3=clip grid';


-- Index: indx_grid_gridtype

-- DROP INDEX indx_grid_gridtype;

CREATE INDEX indx_grid_gridtype
  ON grid
  USING btree
  (gridtype);

-- Index: indx_grid_name

-- DROP INDEX indx_grid_name;

CREATE UNIQUE INDEX indx_grid_name
  ON grid
  USING btree
  (name COLLATE pg_catalog."default");

