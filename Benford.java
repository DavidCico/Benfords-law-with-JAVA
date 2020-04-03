import java.io.*;
import java.text.*;
import java.util.*;

public class Benford {

	private static int[] digitsCounter = new int[9];
	private static double[] digitsPercentages = new double[9];
	private static int linesCounter = 0;
	private static List<String> datesList;
	private static List<String> stringValues;

	// return particular digit percentage value if needed;
	public static double getDigitsPercentages(int index) {
		return digitsPercentages[index];
	}

	// //////////// Read variables from CSV file or generate random (dates,
	// values) ////////

	/*
	 * Read CSV file and generate Hashmap of Dates, Values (string format)
	 */

	public static Map<String, String> ConvertCSVtoDataMap(String filename) {

		BufferedReader br = null;
		String strCurrentLine;
		Map<String, String> dataMap = new HashMap<>();

		// open file with buffered reader
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " does not exist!!");
			System.exit(0);
		}
	

		try {
			
			// read first line in case of header
			strCurrentLine = br.readLine();
			
			// iterate through all lines and store data in hashmap
			while ((strCurrentLine = br.readLine()) != null) {
				String[] strSplit = strCurrentLine.split(",");
				dataMap.put(strSplit[0], strSplit[1]);

				strCurrentLine = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return dataMap;
	}

	/*
	 * Function that generate a List of Strings containing dates between two
	 * endpoints --> to be called in createRandomDataMap
	 */
	public static void createListDateBetween(Date startDate, Date endDate) {
		datesList = new ArrayList<>();

		// set starting date
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);

		// set ending date
		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);

		// iterate day by day and store String in datesList at each iteration
		while (calendar.before(endCalendar)) {
			Date result = calendar.getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String strDate = dateFormat.format(result);
			datesList.add(strDate);
			calendar.add(Calendar.DATE, 1);
		}
	}

	/*
	 * Function that generates a List of Random floats between 0 and 1 based on
	 * list size of Dates list --> to be called in createRandomDataMap
	 */
	public static void generateValuesForDates(String distribution) {

		// random generator
		Random rand = new Random();

		// operations done on an array to add random numbers
		String[] strArrayValues = new String[datesList.size()];

		for (int i = 0; i < strArrayValues.length; i++) {
			if (distribution == "Uniform") {
				double number = rand.nextDouble();
				String str = Double.toString(number);
				strArrayValues[i] = str;
			}
			else if (distribution == "Gaussian") {
				double number = rand.nextGaussian();
				String str = Double.toString(number);
				strArrayValues[i] = str;
			}
			else {
				System.out.println("Distribution is not implemented");
				System.exit(0);
			}
		}

		// convert Array to ArrayList
		stringValues = Arrays.asList(strArrayValues);
	}

	/*
	 * Function that generates a HashMap with random double from 0 to 1
	 * associated with with the keys (dates).
	 */
	public static Map<String, String> createRandomDataMap(Date startDate,
			Date endDate, String distribution) {

		createListDateBetween(startDate, endDate);
		generateValuesForDates(distribution);

		// check list sizes are equal
		if (datesList.size() != stringValues.size()) {
			throw new IllegalArgumentException(
					"ArrayLists must be of same size");
		}

		Map<String, String> dataMap = new HashMap<>();

		// iterators to iterate at same time on both lists
		Iterator<String> it1 = datesList.iterator();
		Iterator<String> it2 = stringValues.iterator();

		while (it1.hasNext() && it2.hasNext()) {
			dataMap.put(it1.next(), it2.next());
		}

		return dataMap;

	}

	/*
	 * Function to print the dataMap
	 */
	public static void printDataMap(Map<String, String> map) {
		for (Map.Entry<String, String> name : map.entrySet()) {
			System.out.println(name.getKey() + " " + name.getValue());
		}
	}

	// ////////// Here starts the program for Benford's law ///////////////////

	/*
	 * Function that look at the characters inside the string and avoid '-', '0', and
	 * '.' to ensure only digits from 1 to 9 are considered. Once the digit is
	 * found, its count is incremented in digitsCounter.
	 */
	public static void countFirstDigit(String str) {

		int index = 0;

		for (int i = 0; i < str.length(); i++) {
			if ((str.charAt(i) == '-') || (str.charAt(i) == '0')
					|| (str.charAt(i) == '.')) {
				continue;
			} else {
				index = i;
				break;
			}
		}

		char firstDigit = str.charAt(index); // store the first digit character
		int digit = Character.getNumericValue(firstDigit); // convert the
															// character to an
															// integer

		digitsCounter[digit - 1]++; // increment the array of counter at index
									// of digit-1, no zero here
	}

	/*
	 * Function to increment the number of lines for calculating percentage
	 */
	public static void incrementlinesCounter() {
		linesCounter++;
	}

	/*
	 * Function that calculates the percentage of occurrence of all digits from
	 * 1 to 9
	 */
	public static void calcPercentage() {
		for (int i = 0; i < digitsCounter.length; i++) {
			digitsPercentages[i] = digitsCounter[i] / (double) linesCounter
					* 100;
		}
	}

	/*
	 * Function used to create a string of stars based on digit occurrence, for
	 * graph printing
	 */
	public static String NumberOfStars(int index) {
		String stars = "";
		long numStars = Math.round(digitsPercentages[index]);
		for (int i = 0; i < numStars; i++) {
			stars += "*";
		}
		return stars;
	}

	/*
	 * This function prints out the final graph of the occurrences of nonzero
	 * digits, including the values for Benford's law, actual percentages and
	 * their graph representation.
	 */
	public static void printGraph() {
		System.out.printf("%-3s %-10s %-10s %-30s \n", "#", "Benford Law",
				"Percentage", "Graph");
		for (int i = 0; i < digitsCounter.length; i++) {
			System.out.printf("%-3d %-11.3f %-10.3f %-30s \n", i + 1,
					Math.log10(1.0 + 1.0 / (i + 1)) * 100,
					digitsPercentages[i], NumberOfStars(i));
		}
	}

}
