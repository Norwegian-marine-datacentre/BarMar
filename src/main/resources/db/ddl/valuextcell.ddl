-- Table: valuextcell

-- DROP TABLE valuextcell;

CREATE TABLE valuextcell
(
  id_value bigint NOT NULL,
  id_tcell bigint NOT NULL,
  CONSTRAINT fkey_valuextcell_tcell FOREIGN KEY (id_tcell)
      REFERENCES tcell (id) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fkey_valuextcell_value FOREIGN KEY (id_value)
      REFERENCES value (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE valuextcell
  OWNER TO grid_owner;
GRANT ALL ON TABLE valuextcell TO grid_owner;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE valuextcell TO grid_reader;

-- Index: fki_fkey_valuextcell_tcell

-- DROP INDEX fki_fkey_valuextcell_tcell;

CREATE INDEX fki_fkey_valuextcell_tcell
  ON valuextcell
  USING btree
  (id_tcell);

-- Index: fki_fkey_valuextcell_value

-- DROP INDEX fki_fkey_valuextcell_value;

CREATE INDEX fki_fkey_valuextcell_value
  ON valuextcell
  USING btree
  (id_value);

