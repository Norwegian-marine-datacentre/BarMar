-- Table: parameter_tcell

-- DROP TABLE parameter_tcell;

CREATE TABLE parameter_tcell
(
  id_parameter bigint NOT NULL,
  id_tcell bigint NOT NULL,
  id_grid bigint NOT NULL,
  publish boolean DEFAULT true,
  CONSTRAINT pkey_parameter_tcell PRIMARY KEY (id_parameter, id_grid, id_tcell),
  CONSTRAINT fkey_parameter_tcell_grid FOREIGN KEY (id_grid)
      REFERENCES grid (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_tcell_parameter FOREIGN KEY (id_parameter)
      REFERENCES parameter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkey_parameter_tcell_tcell FOREIGN KEY (id_tcell)
      REFERENCES tcell (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE parameter_tcell
  OWNER TO grid_owner;
COMMENT ON TABLE parameter_tcell
  IS 'Table designed just for improvement of performance. Use "select imr_insert_unique_periods()"procedure to insert values in this table';

-- Index: indx_parameter_tcell_id_grid

-- DROP INDEX indx_parameter_tcell_id_grid;

CREATE INDEX indx_parameter_tcell_id_grid
  ON parameter_tcell
  USING btree
  (id_grid);

-- Index: indx_parameter_tcell_id_parameter

-- DROP INDEX indx_parameter_tcell_id_parameter;

CREATE INDEX indx_parameter_tcell_id_parameter
  ON parameter_tcell
  USING btree
  (id_parameter);

-- Index: indx_parameter_tcell_id_tcell

-- DROP INDEX indx_parameter_tcell_id_tcell;

CREATE INDEX indx_parameter_tcell_id_tcell
  ON parameter_tcell
  USING btree
  (id_tcell);

