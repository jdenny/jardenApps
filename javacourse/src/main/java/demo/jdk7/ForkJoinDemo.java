package demo.jdk7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Finds the square root of each of a list of numbers, using GCSE maths.
 * TODO: perhaps replace this with Mandelbrot set? See MandelbrotTest.java
 * @author john.denny@gmail.com
 *
 */
public class ForkJoinDemo extends RecursiveTask<List<Double>> {
	private static final long serialVersionUID = 1L;
	private static final double MARGIN = 0.001;
	private static final int THRESHOLD = 5;
	
	private List<Double> numberList;
	private static int numSplits = 0;
	
	public ForkJoinDemo(List<Double> numberList) {
		this.numberList = numberList;
	}
	@Override
	protected List<Double> compute() {
		if (this.numberList.size() <= THRESHOLD) {
			return getRoots();
		}
		ForkJoinDemo.numSplits++;
		int midpoint = numberList.size() / 2;
		List<Double> secondHalf = new ArrayList<Double>(numberList);
		List<Double> temp = secondHalf.subList(0, midpoint);
		List<Double> firstHalf = new ArrayList<Double>(temp);
		temp.clear(); // as temp is sublist of secondHalf, 
			// clear() removes elements from secondHalf; that's handy!
		ForkJoinDemo left = new ForkJoinDemo(firstHalf);
		left.fork();
		ForkJoinDemo right = new ForkJoinDemo(secondHalf); 
		List<Double> rightResult = right.compute();
		List<Double> leftResult = left.join();
		leftResult.addAll(rightResult);
		return leftResult;
	}

	/*
	 * This is the method that does the work. It is called from
	 * compute() when the size of the array is small enough.
	 * @return
	 */
	private List<Double> getRoots() {
		List<Double> roots = new ArrayList<>();
		for (double number: numberList) {
			roots.add(squareRoot(number));
		}
		return roots;
	}
	
	public static double squareRoot(double number) {
		if (number < 0) {
			throw new IllegalArgumentException("number shouldn't be -ve: " +
					number);
		}
		double root = number / 2;
		double diff, absDiff;
		while (true) {
			diff = number - root * root;
			absDiff = diff;
			if (absDiff < 0) absDiff = -absDiff;
			if (absDiff <= MARGIN) break;
			root += diff / (2 * root);
		}
		return root;
	}
	public static void main(String[] args) {
		System.out.println("squareRoot of 10 is " + squareRoot(10.0));
		
		double[] numbers = {2, 3, 6, 8, 10, 12, 14, 22, 24, 26, 108, 110, 112};
		List<Double> numberList = new ArrayList<Double>();
		for (double d: numbers) {
			numberList.add(d);
		}
		ForkJoinDemo task = new ForkJoinDemo(numberList);
		ForkJoinPool pool = new ForkJoinPool();
		List<Double> results = pool.invoke(task);
		System.out.println("number of splits: " + ForkJoinDemo.numSplits);
		
		System.out.println("number\troot\t\t\troot*root");
		for (int i = 0; i < numbers.length; i++) {
			double result = results.get(i);
			System.out.println(numbers[i] + "\t" + result + "\t" + (result * result));
		}
		System.out.println("adios mi amigita");
	}
}
