-- Table: value

-- DROP TABLE value;

CREATE TABLE value
(
  id bigserial NOT NULL,
  id_parameter bigint NOT NULL,
  id_hcell bigint NOT NULL,
  value real, -- The value of a parameter in the actual gridcell and timecell
  fromdepth real,
  todepth real,
  fromdate timestamp without time zone,
  todate timestamp without time zone,
  info character varying(250),
  date_first_entered timestamp without time zone,
  date_last_revised timestamp without time zone,
  CONSTRAINT pkey_value PRIMARY KEY (id),
  CONSTRAINT fkey_value_hcell FOREIGN KEY (id_hcell)
      REFERENCES hcell (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fkey_value_parameter FOREIGN KEY (id_parameter)
      REFERENCES parameter (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE value
  OWNER TO grid_owner;
GRANT ALL ON TABLE value TO grid_owner;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE value TO grid_reader;
COMMENT ON COLUMN value.value IS 'The value of a parameter in the actual gridcell and timecell';


-- Index: fki_fkey_value_hcell

-- DROP INDEX fki_fkey_value_hcell;

CREATE INDEX fki_fkey_value_hcell
  ON value
  USING btree
  (id_hcell);

-- Index: fki_fkey_value_parameter

-- DROP INDEX fki_fkey_value_parameter;

CREATE INDEX fki_fkey_value_parameter
  ON value
  USING btree
  (id_parameter);

-- Index: indx_v_fromdate

-- DROP INDEX indx_v_fromdate;

CREATE INDEX indx_v_fromdate
  ON value
  USING btree
  (fromdate);

-- Index: indx_v_todate

-- DROP INDEX indx_v_todate;

CREATE INDEX indx_v_todate
  ON value
  USING btree
  (todate);

-- Index: indx_v_value

-- DROP INDEX indx_v_value;

CREATE INDEX indx_v_value
  ON value
  USING btree
  (value);

