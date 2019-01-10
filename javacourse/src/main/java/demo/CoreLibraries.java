package demo;

import java.util.Calendar;
import java.util.Date;

public class CoreLibraries {

	public static void main(String[] args) {
		stringMethods();
		stringBuilderMethods();
		formatMethods();
		dateMethods();
	}
	private static void dateMethods() {
		Calendar calendar = Calendar.getInstance(); // default to now
		System.out.println("date now: " + calendarToString(calendar));
		calendar.add(Calendar.DATE, 7);
		System.out.println("date next week: " + calendarToString(calendar));
		calendar.set(1988, Calendar.APRIL, 2);
		System.out.println("anniversary: " + calendarToString(calendar));
		Date date = new Date(1988, 3, 2);
		System.out.println("date=" + date);
		date = calendar.getTime();
		System.out.println("date=" + date);
	}
	private static String calendarToString(Calendar calendar) {
		return calendar.get(Calendar.DATE) + "." +
				(calendar.get(Calendar.MONTH)+1) + "." +
				calendar.get(Calendar.YEAR);
	}
	private static void formatMethods() {
		int i = 2;
	    double r = Math.sqrt(i);
	    System.out.format("The square root of %d is %f.%n", i, r);
	}
	private static void stringBuilderMethods() {
		String summary = "Sales for week %1 shown above";
		String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
		int weekNumber = 34;
		int[] sales = {25, 32, 12, 18, 24};
		StringBuilder builderSales = new StringBuilder();
		for (int i = 0; i < sales.length; ++i) {
			builderSales.append(days[i]);
			builderSales.append("=");
			builderSales.append(sales[i]);
			builderSales.append(" ");
		}
		System.out.println(builderSales);
		StringBuilder builderSummary = new StringBuilder(summary);
		int index = builderSummary.indexOf("%1");
		builderSummary.replace(index, index+2, String.valueOf(weekNumber));
		System.out.println(builderSummary);
	}
	private static void stringMethods() {
		String module = "  Core Libraries  ";
		System.out.println("length=" + module.length());
		System.out.println("first 'e'=" + module.indexOf('e'));
		System.out.println("first 'z'=" + module.indexOf('z'));
		System.out.println("first \"rar\"=" + module.indexOf("rar"));
		// all the following create and return a new String
		System.out.println("upper=" + module.toUpperCase());
		System.out.println("lower=" + module.toLowerCase());
		System.out.println("trim=" + module.trim());
		
		// first println statement above is shorthand for:
		int len = module.length();
		String lenStr = String.valueOf(len); // i.e. convert int to String
		String titleLen = "length=".concat(lenStr); // concatenate
		System.out.println(titleLen);
	}

}
