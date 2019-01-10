package demo.generics;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import quiz.AlgebraQuiz;
import quiz.ArithmeticQuiz;
import quiz.Quiz;

/**
 * Simplified version of java.util.ArrayList<E>,
 * to illustrate some of the ideas involved.
 */
public class MyArrayList<E> implements Iterable<E> {
	private Object[] elementData;
	private int size;
	
	public MyArrayList() {
		// capacity set low so it soon needs to expand
		elementData = new Object[2];
		size = 0;
	}
	public void add(E element) {
		if (size >= elementData.length) {
			Object[] temp = elementData;
			elementData = new Object[elementData.length * 2];
			for (int i = 0; i < temp.length; i++) {
				elementData[i] = temp[i];
			}
			System.out.println("new array length: " + elementData.length);
		}
		elementData[size++] = element;
	}
	@SuppressWarnings("unchecked")
	public E get(int index) {
		return (E)elementData[index];
	}
	@SuppressWarnings("unchecked")
	public E set(int index, E element) {
		E oldValue = (E)elementData[index];
		elementData[index] = element;
		return oldValue;
	}
	public int size() {
		return size;
	}
	public Object[] toArray() {
		return Arrays.copyOf(elementData, size);
	}
	@Override
	public Iterator<E> iterator() {
		return new JohnIterator(this);
	}
	private class JohnIterator implements Iterator<E> {
		private MyArrayList<E> jArray;
		private int index;
		
		public JohnIterator(MyArrayList<E> jArray) {
			this.jArray = jArray;
			this.index = 0;
		}
		@Override
		public boolean hasNext() {
			return (index < jArray.size());
		}

		@Override
		public E next() {
			return jArray.get(index++);
		}

		@Override
		public void remove() {
			throw new java.lang.IllegalStateException("remove not supported yet!");
		}
	}
	/**
	 * counts the number of elements in a JohnArray T[] that are greater
	 * than a specified value.
	 */
	public static <T extends Number> int countJABigs(MyArrayList<T> array, T value) {
		int count = 0;
		for (T element: array) {
			if (element.intValue() > value.intValue()) ++count;
		}
		return count;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> void sort(MyArrayList<T> list) {
		Object[] array = list.toArray();
		for (int i = 0; i < array.length; i++) {
			for (int j = i+1; j < array.length; j++) {
				T compI = (T)array[i]; 
				T compJ = (T)array[j]; 
				if (compI.compareTo(compJ) > 0) {
					Object swap = array[j];
					array[j] = array[i];
					array[i] = swap;
				}
			}
		}
		for (int i = 0; i < array.length; i++) {
			list.set(i, (T)array[i]);
		}
	}
	public static void main(String[] args) {
		// try adding objects to JohnArray, and iterating through the elements:
		MyArrayList<Quiz> jArray = new MyArrayList<>(); // type=Quiz inferred
		jArray.add(new AlgebraQuiz());
		jArray.add(new ArithmeticQuiz());
		for (Quiz quizEl: jArray) {
			System.out.println("object from jArray: " + quizEl);
		}
		// try the static method countJABigs:
		MyArrayList<Double> dArray = new MyArrayList<>();
		dArray.add(new Double(12.3));
		dArray.add(new Double(3.4));
		dArray.add(new Double(14.5));
		int bigCt = MyArrayList.countJABigs(dArray, new Double(5));
		System.out.println("bigCt: " + bigCt);
		// try sorting a JohnArray:
		MyArrayList<Date> myDateList = new MyArrayList<>();
		myDateList.add(new GregorianCalendar(1961, 12, 10).getTime());
		myDateList.add(new GregorianCalendar(1961, 11, 14).getTime());
		myDateList.add(new GregorianCalendar(1989, 9, 13).getTime());
		myDateList.add(new GregorianCalendar(1989, 9, 9).getTime());
		MyArrayList.sort(myDateList);
		System.out.println("sorted date list: ");
		for (Date date: myDateList) {
			System.out.format("  %1$td %1$tb %1$tY%n", date);
		}
	}
}
