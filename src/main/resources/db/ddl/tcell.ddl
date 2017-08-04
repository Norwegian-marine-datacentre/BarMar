-- Table: tcell

-- DROP TABLE tcell;

CREATE TABLE tcell
(
  id bigserial NOT NULL,
  type smallint NOT NULL, -- Type of time period. 1 = fromdate,todate 2 = year 3 = quarter in year 4 = month in year 5 = day in year
  fromdate date,
  todate date,
  year integer,
  quarter smallint,
  month smallint,
  day smallint,
  name character varying(10),
  CONSTRAINT pkey_tcell PRIMARY KEY (id),
  CONSTRAINT unique_name_tcell UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tcell
  OWNER TO grid_owner;
GRANT ALL ON TABLE tcell TO grid_owner;
GRANT SELECT ON TABLE tcell TO grid_reader;
COMMENT ON COLUMN tcell.type IS 'Type of time period. 1 = fromdate,todate 2 = year 3 = quarter in year 4 = month in year 5 = day in year';


-- Index: indx_tcell_day

-- DROP INDEX indx_tcell_day;

CREATE INDEX indx_tcell_day
  ON tcell
  USING btree
  (day);

-- Index: indx_tcell_month

-- DROP INDEX indx_tcell_month;

CREATE INDEX indx_tcell_month
  ON tcell
  USING btree
  (month);

-- Index: indx_tcell_quarter

-- DROP INDEX indx_tcell_quarter;

CREATE INDEX indx_tcell_quarter
  ON tcell
  USING btree
  (quarter);

-- Index: indx_tcell_type

-- DROP INDEX indx_tcell_type;

CREATE INDEX indx_tcell_type
  ON tcell
  USING btree
  (type);

-- Index: indx_tcell_year

-- DROP INDEX indx_tcell_year;

CREATE INDEX indx_tcell_year
  ON tcell
  USING btree
  (year);

