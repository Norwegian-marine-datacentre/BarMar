function displayPeriodClosure() {
	return function displayPeriod( aPeriod ) {
		if (aPeriod === "F" ) {
			return "Aggregated all";
		} else if (aPeriod.indexOf("M") > -1 ) {
			return aPeriod.substring(1, 5) + "-" + findMonth(aPeriod.substring(5));
		} else if (aPeriod.indexOf("Q") > -1 ) {
			if ( aPeriod.indexOf("1") > -1) return aPeriod.replace("-1", "-Spring");
			if ( aPeriod.indexOf("2") > -1) return aPeriod.replace("-2", "-Spring");
			if ( aPeriod.indexOf("3") > -1) return aPeriod.replace("-3", "-Autumn");
			if ( aPeriod.indexOf("4") > -1) return aPeriod.replace("-4", "-Autumn");
			//return aPeriod.substring(1, 5) + "-" + findQuarter(aPeriod.substring(5));
		} else if (aPeriod.indexOf("Y") > -1 ) {
			return aPeriod.substring(1);
		}
		return aPeriod;
	}
	
	function findMonth( monthNum ) {
		if     ( monthNum === "01" ) return "January";
		else if( monthNum === "02" ) return "February";
		else if( monthNum === ("03") ) return "March";
		else if( monthNum === ("04") ) return "April";
		else if( monthNum === ("05") ) return "May";
		else if( monthNum === ("06") ) return "June";
		else if( monthNum === ("07") ) return "July";
		else if( monthNum === ("08") ) return "August";
		else if( monthNum === ("09") ) return "September";
		else if( monthNum === ("10") ) return "October";
		else if( monthNum === ("11") ) return "November";
		else if( monthNum === ("12") ) return "December";
		return "Unknown Month";
	}
		
	function findQuarter(quart) {
		return quart + ".quarter";
	}
}
	
function displayDepth( aDepth ) {
	if ( aDepth === "F" ) 
		return "Aggregated all";
	return aDepth;
}