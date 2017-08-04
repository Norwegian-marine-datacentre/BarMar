-- Table: parameter_statistics

-- DROP TABLE parameter_statistics;

CREATE TABLE parameter_statistics
(
  id_grid bigint NOT NULL,
  id_parameter bigint NOT NULL,
  id_vcell bigint NOT NULL,
  id_tcell bigint NOT NULL,
  nval bigint,
  maxval real,
  minval real,
  CONSTRAINT pkey_parameter_statistics PRIMARY KEY (id_grid, id_parameter, id_vcell, id_tcell),
  CONSTRAINT fkey_parameter_statistics_grid FOREIGN KEY (id_grid)
      REFERENCES grid (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_statistics_parameter FOREIGN KEY (id_parameter)
      REFERENCES parameter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_statistics_tcell FOREIGN KEY (id_tcell)
      REFERENCES tcell (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_statistics_vcell FOREIGN KEY (id_vcell)
      REFERENCES vcell (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE parameter_statistics
  OWNER TO grid_owner;
COMMENT ON TABLE parameter_statistics
  IS 'Table inserted just for performance.';

-- Index: indx_parameter_statistics_id_grid

-- DROP INDEX indx_parameter_statistics_id_grid;

CREATE INDEX indx_parameter_statistics_id_grid
  ON parameter_statistics
  USING btree
  (id_grid);

-- Index: indx_parameter_statistics_id_parameter

-- DROP INDEX indx_parameter_statistics_id_parameter;

CREATE INDEX indx_parameter_statistics_id_parameter
  ON parameter_statistics
  USING btree
  (id_parameter);

-- Index: indx_parameter_statistics_id_tcell

-- DROP INDEX indx_parameter_statistics_id_tcell;

CREATE INDEX indx_parameter_statistics_id_tcell
  ON parameter_statistics
  USING btree
  (id_tcell);

-- Index: indx_parameter_statistics_id_vcell

-- DROP INDEX indx_parameter_statistics_id_vcell;

CREATE INDEX indx_parameter_statistics_id_vcell
  ON parameter_statistics
  USING btree
  (id_vcell);

