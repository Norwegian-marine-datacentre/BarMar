package no.imr.barmar.controller.view;

public class PeriodAndDepthHelper {

	public String getPeriod( String periodFromDepth ) {
		if (periodFromDepth.equals("F")) {
			return "Aggregated all";
		} else if (periodFromDepth.contains("M")) {
			return periodFromDepth.substring(1, 5) + "-" + findMonth(periodFromDepth.substring(5));
		} else if (periodFromDepth.contains("Q")) {
			return periodFromDepth.substring(1, 5) + "-" + findQuarter(periodFromDepth.substring(5));
		} else if (periodFromDepth.contains("Y")) {
			return periodFromDepth.substring(1);
		}
		return periodFromDepth;
	}

	protected String findMonth(String monthNum){
		if(monthNum.equals("01")) return "January";
		else if(monthNum.equals("02")) return "February";
		else if(monthNum.equals("03")) return "March";
		else if(monthNum.equals("04")) return "April";
		else if(monthNum.equals("05")) return "May";
		else if(monthNum.equals("06")) return "June";
		else if(monthNum.equals("07")) return "July";
		else if(monthNum.equals("08")) return "August";
		else if(monthNum.equals("09")) return "September";
		else if(monthNum.equals("10")) return "October";
		else if(monthNum.equals("11")) return "November";
		else if(monthNum.equals("12")) return "December";
		return "Unknown Month";
	}
	
	protected String findQuarter(String quart) {
		return quart + ".quarter";
	}
	
	public String getDepth( String depthFromDb ) {
		if ( depthFromDb.equals("F") ) 
			return "Aggregated all data";
		return depthFromDb;
	}
}