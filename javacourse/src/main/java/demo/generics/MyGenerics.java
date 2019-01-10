package demo.generics;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import quiz.AlgebraQuiz;
import quiz.ArithmeticQuiz;
import quiz.CapitalsQuiz;
import quiz.EndOfQuestionsException;
import quiz.Quiz;

class Box<T> {
	private T value;
	
	public void setValue(T value) {
		this.value = value;
	}
	public T getValue() {
		return this.value;
	}
}

public class MyGenerics {
	/**
	 * Find the element in the array that equals current,
	 * and return the next one. Return element zero if no
	 * match or at end of array.
	 */
	// Return next element in array after current.
	private static <T> T nextInArray(T[] array, T current) {
		for (int i = 0; i < array.length; i++) {
			if (current.equals(array[i])) {
				if (++i < array.length) {
					return array[i];
				}
			}
		}
		return array[0];
	}

	public static void main(String[] args) {
		Integer[] primes = {
			new Integer(7),
			new Integer(11),
			new Integer(13),
			new Integer(17)
		};
		Integer next = nextInArray(primes, new Integer(11));
		System.out.println("next in primes after 11: " + next);
		// define some objects of type Quiz:
		Quiz algebraQuiz = new AlgebraQuiz();
		Quiz arithmeticQuiz = new ArithmeticQuiz();
		Quiz capitalsQuiz = new CapitalsQuiz();
		
		// test out Box class on 2 different types:
		Box<String> boxStr = new Box<String>();
		boxStr.setValue("this is a String");
		String str = boxStr.getValue();
		System.out.println(str);
		Box<Quiz> quizBox = new Box<>(); // type=Quiz inferred
		quizBox.setValue(capitalsQuiz);
		Quiz unboxedQuiz = quizBox.getValue();
		System.out.println(unboxedQuiz);
		
		// use collection without Generics:
		List list = new ArrayList();
		list.add(algebraQuiz);
		list.add("arithmeticQuiz");
		
		try {
			Quiz quiz = (Quiz)list.get(0);
			System.out.println(quiz);
			Quiz quiz1 = (Quiz)list.get(1); // throws class cast exception
			System.out.println(quiz1);
		} catch (Exception e) {
			System.out.println(e);
		}

		// use collection with Generics:
		List<Quiz> quizList = new ArrayList<>(); // type=Quiz inferred
		quizList.add(algebraQuiz);
		// quizList.add("arithmeticQuiz"); // would be compiler error
		quizList.add(arithmeticQuiz);
		
		Quiz quiz = quizList.get(0);
		System.out.println(quiz);
		Quiz quiz1 = quizList.get(1);
		System.out.println(quiz1);

		// can also define generics for methods; usage of countBigs():
		List<Double> dArray = new ArrayList<>();
		dArray.add(new Double(12.3));
		dArray.add(new Double(3.4));
		dArray.add(new Double(14.5));
		int bigCt = MyGenerics.countBigs(dArray, new Double(5));
		System.out.println("bigCt: " + bigCt);
		List<Integer> iArray = new ArrayList<>();
		iArray.add(new Integer(12));
		iArray.add(new Integer(3));
		iArray.add(new Integer(14));
		bigCt = MyGenerics.countBigs(iArray, new Integer(5));
		System.out.println("bigCt: " + bigCt);
		// bigCt = MyGenerics.countBigs(quizList, new Integer(5)); // compile error
		
		// more examples of calling generic methods defined in this class:
		System.out.println("Sample Quiz Questions:");
		MyGenerics.sampleQuizQuestions(quizList);
		System.out.println("End of Sample Quiz Questions:");

		List<Date> myDateList = new ArrayList<>();
		myDateList.add(new GregorianCalendar(1961, 2, 10).getTime());
		myDateList.add(new GregorianCalendar(1951, 11, 14).getTime());
		myDateList.add(new GregorianCalendar(1989, 9, 3).getTime());
		myDateList.add(new GregorianCalendar(1990, 3, 9).getTime());
		MyGenerics.sort(myDateList);
		System.out.println("sorted date list: ");
		for (Date date: myDateList) {
			System.out.format("  %1$td %1$tb %1$tY%n", date);
		}
		// calls to generic methods that use wild-cards:
		// sampleQuizQuestions(new ArrayList<Object>()); // compiler error
		sampleQuizQuestions(new ArrayList<Quiz>());
		sampleQuizQuestions(new ArrayList<ArithmeticQuiz>());
		
		addQuizes(new ArrayList<Object>());
		addQuizes(new ArrayList<Quiz>());
		// addQuizes(new ArrayList<AlgebraQuiz>()); // compiler error

	}
	/**
	 * counts the number of elements in an array T[] that are greater
	 * than a specified value.
	 */
	public static <T extends Number> int countBigs(List<T> array, T value) {
		int count = 0;
		for (T element: array) {
			if (element.intValue() > value.intValue()) ++count;
		}
		return count;
	}
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> void sort(List<T> list) {
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
	/*
	 * For the following demo of generics, bear in mind the hierarchy:
	 *   Throwable -> Exception -> RuntimeException
	 */
	public static <T extends Exception> void printIt(T t) {
		/*
		 * Note the following calls to this method:
		 *    printIt(new Throwable()); // error
		 *    printIt(new Exception()); // ok
		 *    printIt(new RuntimeException()); // ok
		 */
		System.out.println(t);
	}
	/*
	 * Examples of using wildcards in generics.
	 * Note the upper bounded wildcard - ? extends Quiz - is used when we want to
	 * take values out of the parameter; the lower bounded wildcard - ? super Quiz -
	 * is when we want to add values to the parameter.
	 */
	public static void sampleQuizQuestions(List<? extends Quiz> list) {
		/*
		 * Note the following calls to this method:
		 *   sampleQuizQuestions(new ArrayList<Object>()); // compiler error
		 *   sampleQuizQuestions(new ArrayList<Quiz>()); // ok
		 *   sampleQuizQuestions(new ArrayList<ArithmeticQuiz>()); // ok
		 */
		for (Quiz quiz: list) {
			try {
				System.out.println(quiz.getNextQuestion());
			} catch (EndOfQuestionsException e) {
				System.out.println(e + " returned by Quiz " + quiz.getClass().getName());
			}
		}
	}
	public static void addQuizes(List<? super Quiz> list) {
		/*
		 * Note the following calls to this method:
		 *   addQuizes(new ArrayList<Object>()); // ok
		 *   addQuizes(new ArrayList<Quiz>()); // ok
		 *   addQuizes(new ArrayList<ArithmeticQuiz>()); // compiler error
		 */
		Quiz quiz = new AlgebraQuiz();
		list.add(quiz);
		list.add(new ArithmeticQuiz());
	}
}


