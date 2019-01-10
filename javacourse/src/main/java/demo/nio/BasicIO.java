package demo.nio;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import demo.io.Person;

/**
 * java.nio version of BasicIO.java.
 * @author john.denny@gmail.com
 */
public class BasicIO {
	
	public static void main(String[] args) {
		Person[] people = {
				new Person("Mary Poppins", false, 2345),
				new Person("Peter Jones", true, 123)
		};
		// write to a text file, read it back and display:
		Path path = Paths.get("c:/temp/simpleIO.txt");
		System.out.println("text file is: " + path);
		try {
			writeTextToFile(people, path);
			System.out.println("writeTextToFile complete");
			ArrayList<String> fileList = readTextFromFile(path);
			System.out.println("lines read from file:");
			for (String line: fileList) {
				System.out.println("  " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// write to a data file, restore and display:
		path = Paths.get("c:/temp/simpleIO.dat");
		System.out.println("data file is: " + path);
		try {
			writeDataToFile(people, path);
			System.out.println("writeDataToFile complete");
			ArrayList<Person> peopleList = readDataFromFile(path);
			System.out.println("data read from file:");
			for (Person person: peopleList) {
				System.out.println("  " + person);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// serialise object to file, restore and display:
		path = Paths.get("c:/temp/simpleIO.ser");
		System.out.println("object file is: " + path);
		try {
			writeObjectsToFile(people, path);
			System.out.println("writeObjectsToFile complete");
			ArrayList<Person> peopleList = readObjectsFromFile(path);
			System.out.println("objects read from file:");
			for (Person person: peopleList) {
				System.out.println("  " + person);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// serialise object to file, restore and display:
		path = Paths.get("c:/temp/simpleIO.xml");
		System.out.println("object file is: " + path);
		try {
			writeObjectsToXMLFile(people, path);
			System.out.println("writeObjectsToXMLFile complete");
			ArrayList<Person> peopleList = readObjectsFromXMLFile(path);
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
	public static void writeTextToFile(Person[] people, Path path) throws IOException {
		try (Writer writer = Files.newBufferedWriter(path, Charset.defaultCharset());
				PrintWriter printWriter = new PrintWriter(writer)) {
			for(Person person: people) {
				printWriter.println(person.toString());
			}
		}
	}
	/**
	 * Read lines of text from the file, and return as an ArrayList.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> readTextFromFile(Path path) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset()) ) {
			ArrayList<String> fileList = new ArrayList<>();
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				fileList.add(line);
			}
			return fileList;
		}
	}
	/**
	 * For each Person object, write the binary data from each field
	 * to a data file.
	 */
	public static void writeDataToFile(Person[] people, Path path) throws IOException {
		try (OutputStream fos = Files.newOutputStream(path);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		DataOutputStream dataOS = new DataOutputStream(bos)) {
			for (Person person: people) {
				dataOS.writeUTF(person.getName());
				dataOS.writeBoolean(person.isMale());
				dataOS.writeInt(person.getSalary());
			}
		}
	}
	public static ArrayList<Person> readDataFromFile(Path path) throws IOException {
		ArrayList<Person> personList = new ArrayList<>();
		try (InputStream fis = Files.newInputStream(path);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dataIS = new DataInputStream(bis)) {
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
		}
		return personList;
	}
	/**
	 * For each Person object, write a serialised version of the object
	 * to a binary file.
	 */
	public static void writeObjectsToFile(Person[] people, Path path) throws IOException {
		try (OutputStream fos = Files.newOutputStream(path);
				ObjectOutputStream objectOS = new ObjectOutputStream(fos)) {
			for (Person person: people) {
				objectOS.writeObject(person);
			}
		}
	}
	/**
	 * Read Person objects from the file, and add to an ArrayList
	 * which is returned when EOF is reached.
	 * @param file to be read
	 * @return list of Person objects de-serialised from the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<Person> readObjectsFromFile(Path path)
			throws IOException, ClassNotFoundException {
		ArrayList<Person> personList = new ArrayList<>();
		try (InputStream fis = Files.newInputStream(path);
				ObjectInputStream objectIS = new ObjectInputStream(fis)) {
			while (true) {
				try {
					Person person = (Person) objectIS.readObject();
					personList.add(person);
				} catch (EOFException e) {
					break;
				}
			}
		}
		return personList;
	}

	/**
	 * For each Person object, write a serialised version of the object
	 * to an XML file.
	 */
	public static void writeObjectsToXMLFile(Person[] people, Path path) throws IOException {
		try (OutputStream fos = Files.newOutputStream(path);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			XMLEncoder xmlEncoder = new XMLEncoder(bos)) {
			for (Person person: people) {
				xmlEncoder.writeObject(person);
			}
		}
	}
	/**
	 * Read Person objects from the XML file, which it assumes has been
	 * created with XMLEncoder, and add to an ArrayList
	 * which is returned when EOF is reached.
	 */
	public static ArrayList<Person> readObjectsFromXMLFile(Path path)
			throws IOException {
		ArrayList<Person> personList = new ArrayList<>();
		try (InputStream fis = Files.newInputStream(path);
				BufferedInputStream bis = new BufferedInputStream(fis);
				XMLDecoder xmlDecoder = new XMLDecoder(bis)) {
			while (true) {
				try {
					Person person = (Person) xmlDecoder.readObject();
					personList.add(person);
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
			}
		}
		return personList;
	}
		
}
