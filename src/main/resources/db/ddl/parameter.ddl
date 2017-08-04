-- Table: parameter

-- DROP TABLE parameter;

CREATE TABLE parameter
(
  id bigserial NOT NULL,
  name character varying(80) NOT NULL,
  description character varying(240),
  id_unit bigint,
  publish boolean DEFAULT true,
  CONSTRAINT pkey_parameter PRIMARY KEY (id),
  CONSTRAINT fkey_parameter_unit FOREIGN KEY (id_unit)
      REFERENCES unit (id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT unique_name_parameter UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE parameter
  OWNER TO grid_owner;
GRANT ALL ON TABLE parameter TO grid_owner;
GRANT SELECT ON TABLE parameter TO grid_reader;

-- Index: fki_fkey_parameter_unit

-- DROP INDEX fki_fkey_parameter_unit;

CREATE INDEX fki_fkey_parameter_unit
  ON parameter
  USING btree
  (id_unit);

-- Index: indx_parameter_name

-- DROP INDEX indx_parameter_name;

CREATE UNIQUE INDEX indx_parameter_name
  ON parameter
  USING btree
  (name COLLATE pg_catalog."default");

