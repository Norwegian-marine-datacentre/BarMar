-- Table: valuexvcell

-- DROP TABLE valuexvcell;

CREATE TABLE valuexvcell
(
  id_value bigint NOT NULL,
  id_vcell bigint NOT NULL,
  CONSTRAINT fkey_valuexvcell_value FOREIGN KEY (id_value)
      REFERENCES value (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fkey_valuexvcell_vcell FOREIGN KEY (id_vcell)
      REFERENCES vcell (id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
WITH (
  OIDS=FALSE
);
ALTER TABLE valuexvcell
  OWNER TO grid_owner;
GRANT ALL ON TABLE valuexvcell TO grid_owner;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE valuexvcell TO grid_reader;

-- Index: fki_fkey_valuexvcell_value

-- DROP INDEX fki_fkey_valuexvcell_value;

CREATE INDEX fki_fkey_valuexvcell_value
  ON valuexvcell
  USING btree
  (id_value);

-- Index: fki_fkey_valuexvcell_vcell

-- DROP INDEX fki_fkey_valuexvcell_vcell;

CREATE INDEX fki_fkey_valuexvcell_vcell
  ON valuexvcell
  USING btree
  (id_vcell);

