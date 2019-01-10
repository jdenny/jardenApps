package demo.io;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * BasicIO.java - simple examples of basic output to file and input from file,
 * using text, binary data, serialised objects, and serialised object to XML .
 */
public class BasicIO {
	
	public static void main(String[] args) {
		Person[] people = {
				new Person("Mary Poppins", false, 2345),
				new Person("Peter Jones", true, 123)
		};
		// write to a text file, read it back and display:
		File file = new File("c:/temp/simpleIO.txt");
		System.out.println("text file is: " + file.getPath());
		try {
			writeTextToFile(people, file);
			System.out.println("writeTextToFile complete");
			ArrayList<String> fileList = readTextFromFile(file);
			System.out.println("lines read from file:");
			for (String line: fileList) {
				System.out.println("  " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// write to a data file, restore and display:
		file = new File("c:/temp/simpleIO.dat");
		System.out.println("data file is: " + file.getPath());
		try {
			writeDataToFile(people, file);
			System.out.println("writeDataToFile complete");
			ArrayList<Person> peopleList = readDataFromFile(file);
			System.out.println("data read from file:");
			for (Person person: peopleList) {
				System.out.println("  " + person);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// serialise object to file, restore and display:
		file = new File("c:/temp/simpleIO.ser");
		System.out.println("object file is: " + file.getPath());
		try {
			writeObjectsToFile(people, file);
			System.out.println("writeObjectsToFile complete");
			ArrayList<Person> peopleList = readObjectsFromFile(file);
			System.out.println("objects read from file:");
			for (Person person: peopleList) {
				System.out.println("  " + person);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// serialise object to file, restore and display:
		file = new File("c:/temp/simpleIO.xml");
		System.out.println("object file is: " + file.getPath());
		try {
			writeObjectsToXMLFile(people, file);
			System.out.println("writeObjectsToXMLFile complete");
			ArrayList<Person> peopleList = readObjectsFromXMLFile(file);
			System.out.println("objects read from XML file:");
			for (Person person: peopleList) {
				System.out.println("  " + person);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * For each Person object, write the String returned by toString()
	 * to a text file.
	 * @param people array of Person objects to be written to file
	 * @param file
	 * @throws IOException
	 */
	public static void writeTextToFile(Person[] people, File file) throws IOException {
		PrintWriter writer = null;
		try {
			FileWriter fWriter = new FileWriter(file);
			writer = new PrintWriter(fWriter);
			for(Person person: people) {
				writer.println(person.toString());
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	/**
	 * Read lines of text from the file, and return as an ArrayList.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> readTextFromFile(File file) throws IOException {
		BufferedReader reader = null;
		try {
			FileReader fReader = new FileReader(file);
			reader = new BufferedReader(fReader);
			ArrayList<String> fileList = new ArrayList<>();
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				fileList.add(line);
			}
			return fileList;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	/**
	 * For each Person object, write the binary data from each field
	 * to a data file.
	 */
	public static void writeDataToFile(Person[] people, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		DataOutputStream dataOS = new DataOutputStream(bos);
		for (Person person: people) {
			dataOS.writeUTF(person.getName());
			dataOS.writeBoolean(person.isMale());
			dataOS.writeInt(person.getSalary());
		}
		dataOS.close();
	}
	public static ArrayList<Person> readDataFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dataIS = new DataInputStream(bis);
		ArrayList<Person> personList = new ArrayList<>();
		while (true) {
			try {
				String name = dataIS.readUTF();
				boolean male = dataIS.readBoolean();
				int salary = dataIS.readInt();
				personList.add(new Person(name, male, salary));
			} catch (EOFException e) {
				break;
			}
		}
		dataIS.close();
		return personList;
	}
	/**
	 * For each Person object, write a serialised version of the object
	 * to a binary file.
	 */
	public static void writeObjectsToFile(Person[] people, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream objectOS = new ObjectOutputStream(fos);
		for (Person person: people) {
			objectOS.writeObject(person);
		}
		objectOS.close();
	}
	/**
	 * Read Person objects from the file, and add to an ArrayList
	 * which is returned when EOF is reached.
	 * @param file to be read
	 * @return list of Person objects de-serialised from the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<Person> readObjectsFromFile(File file) throws IOException,
	ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream objectIS = new ObjectInputStream(fis);
		ArrayList<Person> personList = new ArrayList<>();
		while (true) {
			try {
				Person person = (Person)objectIS.readObject();
				personList.add(person);
			} catch (EOFException e) {
				break;
			}
		}
		objectIS.close();
		return personList;
	}
	/**
	 * For each Person object, write a serialised version of the object
	 * to an XML file.
	 */
	public static void writeObjectsToXMLFile(Person[] people, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		XMLEncoder xmlEncoder = new XMLEncoder(bos);
		for (Person person: people) {
			xmlEncoder.writeObject(person);
		}
		xmlEncoder.close();
	}
	/**
	 * Read Person objects from the XML file, which it assumes has been
	 * created with XMLEncoder, and add to an ArrayList
	 * which is returned when EOF is reached.
	 */
	public static ArrayList<Person> readObjectsFromXMLFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		XMLDecoder xmlDecoder = new XMLDecoder(bis);
		ArrayList<Person> personList = new ArrayList<>();
		while (true) {
			try {
				Person person = (Person)xmlDecoder.readObject();
				personList.add(person);
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		xmlDecoder.close();
		return personList;
	}
		
}
