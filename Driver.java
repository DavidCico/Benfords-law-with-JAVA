import java.text.*;
import java.util.*;

public class Driver {

	public static void main(String[] args) {
		
////////////////////////////////////Variables and input parameters/////////////////////		
		
		Map<String, String> dataMap;
		
		// filename for CSV file
		String filename = "AAPL.csv";
		
		// Define dates for generating dataset from startDate to EndDate
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		Date startDate = null;
		Date endDate = null;
		
		try {
			startDate = format.parse("2009-12-31");
			endDate = format.parse("2010-12-31");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		/// User Input //////
		
		System.out.println("Enter 1 for CSV dataset; Enter 2 for uniform random dist.; Enter any integer for random Gaussian dist. :");
		
		Scanner sc = new Scanner(System.in);
		
		int choice = sc.nextInt();
		
		if(choice == 1){
			dataMap = Benford.ConvertCSVtoDataMap(filename);
		}
		else if(choice == 2){
			dataMap = Benford.createRandomDataMap(startDate, endDate, "Uniform");
		}
		else{
			dataMap = Benford.createRandomDataMap(startDate, endDate, "Gaussian");
		}

		//Benford.printDataMap(dataMap);

		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			Benford.incrementlinesCounter(); // increment the line for // percentage counting
			Benford.countFirstDigit(entry.getValue()); // count first digit of the current element
														// in map
		}
		
		Benford.calcPercentage();
		Benford.printGraph();
		
	}

}
