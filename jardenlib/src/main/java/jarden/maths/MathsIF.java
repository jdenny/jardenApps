package jarden.maths;

public interface MathsIF {
	long factorial(int n);
	/**
	 * Same as getPrimes(start, count, 0).
	 */
	long[] getPrimes(long start, int count);
	/**
	 * Get the next count primes > start. If delay > 0, sleep for delay milliseconds
	 * between each calculation.
	 */
	long[] getPrimes(long start, int count, int delay, boolean resetCache,
							boolean verbose);
	/**
	 * Stop current calculations.
	 */
	void stop();
}
