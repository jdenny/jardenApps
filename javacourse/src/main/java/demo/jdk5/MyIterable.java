package demo.jdk5;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyIterable implements Iterable<String>, Iterator<String> {
	private int index;
	private final static String[] dias = {
		"sábado", "lunes", "martes", "miércoles", "jueves",
		"viernes", "domingo"
	};

	public static void main(String[] args) {
		MyIterable mfl = new MyIterable();
		for (String s: mfl) {
			System.out.println(s);
		}
		for (String s: mfl) {
			System.out.println(s);
		}
	}

	@Override
	public boolean hasNext() {
		return index < dias.length;
	}

	@Override
	public String next() {
		if (index < dias.length) {
			return dias[index++];
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		this.index = 0;
		return this;
	}

}
