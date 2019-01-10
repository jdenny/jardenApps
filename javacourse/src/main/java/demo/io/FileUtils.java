package demo.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {

	/**
	 * Some utilities for manipulating files for this course.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File inFile = new File("C:/Users/John/java/problems/alimentos.dat");
		File outFile = new File("C:/Users/John/java/problems/alimentosWithQuotes.dat");
		addQuotesToSQLStrings(inFile, outFile);
		System.out.println("adios Juan");
	}
	/**
	 * From file with lines of form: one,two,three,four
	 * create file with lines: "one","two","three","four".
	 * @param inFile source file
	 * @param outFile destination file
	 * @throws IOException
	 */
	public static void addQuotesToSQLStrings(File inFile, File outFile) throws IOException {
		FileReader fReader = new FileReader(inFile);
		BufferedReader reader = new BufferedReader(fReader);
		FileWriter fWriter = new FileWriter(outFile);
		PrintWriter writer = new PrintWriter(fWriter);
		String line;
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			String[] cols = line.split(",");
			for (String col: cols) {
				builder.append("\"" + col + "\",");
			}
			writer.println(builder.toString().substring(0, builder.length() - 1));
			builder.setLength(0);
		}
		reader.close();
		writer.close();
	}
}
