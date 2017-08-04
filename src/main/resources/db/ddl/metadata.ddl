-- Table: metadata

-- DROP TABLE metadata;

CREATE TABLE metadata
(
  id bigserial NOT NULL,
  id_grid bigint NOT NULL,
  id_parameter bigint NOT NULL,
  dataset_name character varying(120),
  geographic_coverage character varying(240),
  summary character varying(2000),
  originator character varying(120),
  contact character varying(120),
  lastupdated character varying(30),
  publish boolean,
  CONSTRAINT pkey_meatadata PRIMARY KEY (id),
  CONSTRAINT fkey_metadata_grid FOREIGN KEY (id_grid)
      REFERENCES grid (id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fkey_metadata_parameter FOREIGN KEY (id_parameter)
      REFERENCES parameter (id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT unique_parameter_grid UNIQUE (id_grid, id_parameter)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE metadata
  OWNER TO grid_owner;
GRANT ALL ON TABLE metadata TO grid_owner;
GRANT SELECT ON TABLE metadata TO grid_reader;

-- Index: fki_fkey_metadata_grid

-- DROP INDEX fki_fkey_metadata_grid;

CREATE INDEX fki_fkey_metadata_grid
  ON metadata
  USING btree
  (id_grid);

-- Index: fki_fkey_metadata_parameter

-- DROP INDEX fki_fkey_metadata_parameter;

CREATE INDEX fki_fkey_metadata_parameter
  ON metadata
  USING btree
  (id_parameter);

