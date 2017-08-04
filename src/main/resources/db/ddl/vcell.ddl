-- Table: vcell

-- DROP TABLE vcell;

CREATE TABLE vcell
(
  id bigserial NOT NULL,
  type bigint NOT NULL, -- 1 = free depth layer, 2 = whole water column, 3 = pelagic layer, 4 = bottom layer
  fromdepth double precision, -- Minimum depth in meters
  todepth double precision, -- Maximum depth in meters
  layerthickness double precision, -- Thickness of bottom OR pelagic layer
  name character varying(20),
  CONSTRAINT pkey_vcell PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vcell
  OWNER TO grid_owner;
GRANT ALL ON TABLE vcell TO grid_owner;
GRANT SELECT ON TABLE vcell TO grid_reader;
COMMENT ON COLUMN vcell.type IS '1 = free depth layer, 2 = whole water column, 3 = pelagic layer, 4 = bottom layer';
COMMENT ON COLUMN vcell.fromdepth IS 'Minimum depth in meters';
COMMENT ON COLUMN vcell.todepth IS 'Maximum depth in meters';
COMMENT ON COLUMN vcell.layerthickness IS 'Thickness of bottom OR pelagic layer';


-- Index: indx_vcell_type

-- DROP INDEX indx_vcell_type;

CREATE INDEX indx_vcell_type
  ON vcell
  USING btree
  (type);
ALTER TABLE vcell CLUSTER ON indx_vcell_type;

