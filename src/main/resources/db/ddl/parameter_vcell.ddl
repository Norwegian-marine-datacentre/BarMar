-- Table: parameter_vcell

-- DROP TABLE parameter_vcell;

CREATE TABLE parameter_vcell
(
  id_parameter bigint NOT NULL,
  id_vcell bigint NOT NULL,
  id_grid bigint NOT NULL,
  CONSTRAINT pkey_parameter_vcell PRIMARY KEY (id_parameter, id_grid, id_vcell),
  CONSTRAINT fkey_parameter_vcell_grid FOREIGN KEY (id_grid)
      REFERENCES grid (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_vcell_parameter FOREIGN KEY (id_parameter)
      REFERENCES parameter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_vcell_vcell FOREIGN KEY (id_vcell)
      REFERENCES vcell (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE parameter_vcell
  OWNER TO grid_owner;
COMMENT ON TABLE parameter_vcell
  IS 'Table added just to improve performance. Use "select imr_insert_unique_depthlayers()" to insert values in this table';

-- Index: indx_parameter_vcell_id_grid

-- DROP INDEX indx_parameter_vcell_id_grid;

CREATE INDEX indx_parameter_vcell_id_grid
  ON parameter_vcell
  USING btree
  (id_grid);

-- Index: indx_parameter_vcell_id_parameter

-- DROP INDEX indx_parameter_vcell_id_parameter;

CREATE INDEX indx_parameter_vcell_id_parameter
  ON parameter_vcell
  USING btree
  (id_parameter);

-- Index: indx_parameter_vcell_id_vcell

-- DROP INDEX indx_parameter_vcell_id_vcell;

CREATE INDEX indx_parameter_vcell_id_vcell
  ON parameter_vcell
  USING btree
  (id_vcell);

