package demo.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import demo.io.Person;

public class MyChannels {

	public static void main(String[] args) throws IOException {
		Person[] people = {
				new Person("Mary Jane Poppins", false, 2345),
				new Person("Peter Jones", true, 123)
		};
		// write to a text file, read it back and display:
		Path path = Paths.get("c:/temp/channelIO.txt");
		System.out.println("text file is: " + path);
		ByteBuffer buffer = ByteBuffer.allocate(2048);
		try (SeekableByteChannel channel = Files.newByteChannel(path,
				StandardOpenOption.WRITE, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			for (Person person : people) {
				String s = person.toString() + "\n";
				buffer.put(s.getBytes());
			}
			buffer.flip();
			channel.write(buffer);
			System.out.println("text file written");
		}
		try (SeekableByteChannel channel = Files.newByteChannel(path,
				StandardOpenOption.READ)) {
			buffer.clear();
			int byteCt = channel.read(buffer);
			buffer.flip();
			byte[] bytes = new byte[byteCt];
			buffer.get(bytes);
			String s = new String(bytes);
			System.out.println(s);
		}
		
		// write to a text file, read it back and display:
		path = Paths.get("c:/temp/channelIO.dat");
		System.out.println("text file is: " + path);
		buffer = ByteBuffer.allocate(2048);
		try (SeekableByteChannel channel = Files.newByteChannel(path,
				StandardOpenOption.WRITE, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			buffer.putInt(people.length);
			for (Person person : people) {
				String name = person.getName();
				buffer.putInt(name.length());
				buffer.put(name.getBytes());
				buffer.putInt(person.isMale()?1:0);
				buffer.putInt(person.getSalary());
			}
			buffer.flip();
			channel.write(buffer);
			System.out.println("data file written");
		}
		
		List<Person> personList = new ArrayList<>();
		try (SeekableByteChannel channel = Files.newByteChannel(path,
				StandardOpenOption.READ)) {
			buffer.clear();
			channel.read(buffer);
			buffer.flip();
			int personCt = buffer.getInt();
			for (int i = 0; i < personCt; ++i) {
				int nameLen = buffer.getInt();
				byte[] nameBytes = new byte[nameLen];
				buffer.get(nameBytes);
				String name = new String(nameBytes);
				int maleInt = buffer.getInt();
				boolean male = (maleInt != 0);
				int salary = buffer.getInt();
				personList.add(new Person(name, male, salary));
			}
			System.out.println(personCt + " person records read from data file:");
			for (Person person: personList) {
				System.out.println(person);
			}
		}
		
		System.out.println("adios mi amiguito");

	}

}
