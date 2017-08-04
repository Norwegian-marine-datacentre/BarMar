-- Function: imr_gettcells(bigint)

-- DROP FUNCTION imr_gettcells(bigint);

CREATE OR REPLACE FUNCTION imr_gettcells(id_value_in bigint)
  RETURNS character varying AS
$BODY$
  DECLARE
     i int8 = -1;
	 sResult varchar := '';
	 sFinal varchar := '';
     c1 cursor (myidvalue int) is select trim(to_char(id_tcell,'999999999')) FROM valuextcell
           WHERE id_value = myidvalue
           ORDER BY id_tcell;
  BEGIN

     OPEN c1(id_value_in);

     LOOP
       fetch c1 into sResult;
       IF NOT FOUND THEN
           close c1;
           RETURN sFinal;
       END IF;
       i := i + 1;
	 
       IF i = 0 THEN
	     sFinal = sResult;
	   ELSE
	     sFinal := sFinal ||',' ||sResult;
	   END IF;
	 
     END LOOP;
     sFinal := sFinal ||',' ||sResult;
     close c1;
	 RETURN sFinal;

  END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION imr_gettcells(bigint)
  OWNER TO grid_owner;
