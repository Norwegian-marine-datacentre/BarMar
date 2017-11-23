-- Table: parameter_basename

-- DROP TABLE parameter_basename;

CREATE TABLE parameter_basename
(
  id bigserial NOT NULL,
  name character varying(80) NOT NULL,
  standard_name character varying(80) NOT NULL,
  CONSTRAINT pkey_parameter_basename PRIMARY KEY (id),
  CONSTRAINT unique_name_parameter_basename UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE parameter_basename
  OWNER TO grid_owner;
GRANT ALL ON TABLE parameter_basename TO grid_owner;
GRANT SELECT ON TABLE parameter_basename TO grid;
