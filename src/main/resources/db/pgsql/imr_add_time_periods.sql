-- Function: imr_add_time_periods(smallint)

-- DROP FUNCTION imr_add_time_periods(smallint);

CREATE OR REPLACE FUNCTION imr_add_time_periods(year_in smallint)
  RETURNS character varying AS
$BODY$
  DECLARE
    statement_start character varying;
	statement_end character varying;
	statement character varying;
	s_year character varying;
	year smallint;
	id bigint;
	s_id character varying;
	s_type character varying;
	s_todate character varying;
	s_td character varying;
	s_fromdate character varying;
	s_fd character varying;
	s_name character varying;
	s_quarter character varying;
	s_month character varying;
	s_day character varying;
	the_date_start date;
	the_date_end date;
	the_date date;
	s character varying;
  BEGIN
    year := year_in;
    s_year := trim(to_char((select cast(year as bigint)),'9999'));
    statement_start := 'insert into tcell (id,type,fromdate,todate,year,quarter,month,day,name) values ( ';	
	s := trim(to_char((select cast((year - 1) as bigint)),'9999'));
	the_date_start := to_date(s||'1231','YYYYMMDD'); -- last day the year before
	s := trim(to_char((select cast((year + 1) as bigint)),'9999'));
	the_date_end := to_date(s||'0101','YYYYMMDD'); -- first date the year after
	the_date := the_date_start + 1;
    WHILE the_date < the_date_end LOOP
		IF ((the_date - the_date_start) = 1) THEN
		  -- YEAR
          Execute 'select max(id) + 1 from tcell' into id;
          s_id := to_char(id,'99999999');
		  s_type := '2';
	      s_td := s_year||'1231';
	      s_todate := 'to_date('||quote_literal(s_td) ||','||quote_literal('YYYYMMDD') ||')';
	      s_fd := s_year||'0101';
	      s_fromdate := 'to_date('||quote_literal(s_fd) ||','||quote_literal('YYYYMMDD') ||')';
	      s_name := quote_literal('Y'||s_year);
	      s_quarter := 'null';
	      s_month := 'null';
	      s_day := 'null';
          statement_end := s_id||','||s_type||','||s_fromdate||','||s_todate||','||s_year||','||s_quarter||','||s_month||','||s_day||','||s_name||')';
          statement := statement_start || statement_end; 
          Execute statement;		  
		END IF;
		IF (trim(to_char(the_date,'MMDD')) in ( '0101','0401','0701','1001')  ) THEN
		  -- QUARTER	  
          s_fd := trim(to_char(the_date,'YYYYMMDD'));
          s_fromdate := 'to_date('||quote_literal(s_fd) ||','||quote_literal('YYYYMMDD') ||')';
     	  IF (trim(to_char(the_date,'MMDD')) = '0101') THEN
		    s_quarter := '1';
            s_td := s_year||'0331';		
		  END IF;
		  IF (trim(to_char(the_date,'MMDD')) = '0401') THEN
		    s_quarter := '2';
            s_td := s_year||'0630';				
		  END IF;
		  IF (trim(to_char(the_date,'MMDD')) = '0701') THEN
		    s_quarter := '3';
            s_td := s_year||'0930';			
		  END IF;
		  IF (trim(to_char(the_date,'MMDD')) = '1001') THEN
		    s_quarter := '4';
            s_td := s_year||'1231';				
		  END IF;
		  s_todate := 'to_date('||quote_literal(s_td) ||','||quote_literal('YYYYMMDD') ||')';
		  s_name := quote_literal('Q'||s_year||s_quarter);
	      Execute 'select max(id) + 1 from tcell' into id;
          s_id := to_char(id,'99999999');
		  s_type := '3';
	      s_month := 'null';
	      s_day := 'null';
          statement_end := s_id||','||s_type||','||s_fromdate||','||s_todate||','||s_year||','||s_quarter||','||s_month||','||s_day||','||s_name||')';
          statement := statement_start || statement_end; 
          Execute statement;		  
		END IF;
		IF (trim(to_char(the_date,'DD')) = '01') THEN
		  -- MONTH
		  s_type := '4';
		  s_month := trim(to_char(the_date,'MM'));
		  IF (s_month in ('01','03','05','07','08','10','12')) THEN
		    s_todate := 'to_date('||quote_literal(s_year||s_month||'31') ||','||quote_literal('YYYYMMDD') ||')';
		  END IF;
		  IF (s_month in ('04','06','09','11')) THEN
		    s_todate := 'to_date('||quote_literal(s_year||s_month||'30') ||','||quote_literal('YYYYMMDD') ||')';
		  END IF;		  
		  IF (s_month = '02') THEN
		    IF(year%4 = 0) THEN
		      s_todate := 'to_date('||quote_literal(s_year||s_month||'29') ||','||quote_literal('YYYYMMDD') ||')';
			ELSE
			  s_todate := 'to_date('||quote_literal(s_year||s_month||'28') ||','||quote_literal('YYYYMMDD') ||')';
			END IF;
		  END IF;
          Execute 'select max(id) + 1 from tcell' into id;
          s_id := to_char(id,'99999999');
	      s_fd := trim(to_char(the_date,'YYYYMMDD'));
	      s_fromdate := 'to_date('||quote_literal(s_fd) ||','||quote_literal('YYYYMMDD') ||')';
		  s_month := trim(to_char(the_date,'MM'));
	      s_day := 'null';
	      s_name := quote_literal('M'||s_year||s_month);
          statement_end := s_id||','||s_type||','||s_fromdate||','||s_todate||','||s_year||','||s_quarter||','||s_month||','||s_day||','||s_name||')';
          statement := statement_start || statement_end; 
          Execute statement;		  	  
		END IF;
		-- DAY
        Execute 'select max(id) + 1 from tcell' into id;
        s_id := to_char(id,'99999999');
		s_type := '5';
	    s_td := trim(to_char(the_date,'YYYYMMDD'));
	    s_todate := 'to_date('||quote_literal(s_td) ||','||quote_literal('YYYYMMDD') ||')';
	    s_fd := s_td;
	    s_fromdate := 'to_date('||quote_literal(s_fd) ||','||quote_literal('YYYYMMDD') ||')';
		s_month := trim(to_char(the_date,'MM'));
	    s_day := trim(to_char(the_date,'DD'));
	    s_name := quote_literal('D'||s_year||trim(to_char(the_date,'MM'))||trim(to_char(the_date,'DD')));
        statement_end := s_id||','||s_type||','||s_fromdate||','||s_todate||','||s_year||','||s_quarter||','||s_month||','||s_day||','||s_name||')';
        statement := statement_start || statement_end; 
        Execute statement;		
		--
		the_date := the_date + 1;
    END LOOP;	
    RETURN 'finished';  
   END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_add_time_periods(smallint)
  OWNER TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_add_time_periods(smallint) TO grid_owner;
GRANT EXECUTE ON FUNCTION imr_add_time_periods(smallint) TO public;
GRANT EXECUTE ON FUNCTION imr_add_time_periods(smallint) TO grid;
