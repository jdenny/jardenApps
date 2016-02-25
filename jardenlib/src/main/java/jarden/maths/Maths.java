package jarden.maths;

/**
 * Various mathematical functions. Main one is to calculate prime numbers;
 * algorithm is: n is a prime if cannot be divided without remainder
 * by all the primes <= sqrt(n). Strategy is to maintain an array of
 * primesSoFar, initially containing the prime number 2. For each request for
 * lowest prime > m, keep on adding the next highest prime number to primesSoFar,
 * until we find one > m.
 */
public class Maths implements MathsIF {
	private long[] primesSoFar;
	private int primeCt;
	private boolean verbose;
	private boolean stop;
	private int delay;

	public Maths() {
		try {
			verbose = Boolean.getBoolean("verbose");
		}
		catch(Exception e) {
			System.out.println("exception trying to getBoolean(verbose): " + e);
		}
		resetCache();
	}
	private void resetCache() {
		primesSoFar = new long[10];
		primesSoFar[0] = 2;
		primesSoFar[1] = 3;
		primeCt = 2;
	}
	public static int factorial2(int n) {
		return fact(n, 1);
	}
	public static int[] quadratic(int a, int b, int c)  {
		int b2m4ac = b * b - 4 * a * c;
		if (b2m4ac < 0) {
			throw new IllegalArgumentException("no real solution");
		}
		int sqrt = (int) Math.sqrt(b2m4ac);
		if (sqrt * sqrt != b2m4ac) {
			throw new IllegalArgumentException("no integer solution");
		}
		int r1 = (-b - sqrt) / (2 * a);
		int r2 = (-b + sqrt) / (2 * a);
		return new int[] {r1, r2};
	}
	private static int fact(int n, int total) {
		if (n < 2) return total;
		else return fact(n-1, n * total);
	}
	public long factorial(int n) {
		long fact = 1;
		for (int i = 2; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}
	public long[] getPrimes(long start, int count) {
		return getPrimes(start, count, 0, false, false);
	}
	/**
	 * Get the next count primes > start. If delay > 0, sleep for delay milliseconds
	 * between each calculation.
	 */
	public long[] getPrimes(long start, int count, int delay, boolean resetCache,
	boolean verbose) {
		if (resetCache) {
			resetCache();
		}
		this.verbose = verbose;
		this.delay = delay;
		stop = false;
		long[] primes = new long[count];
		int currentIndex = 0;
		long currentPrime = primesSoFar[currentIndex];
		for (int i = 0; i < count; i++) {
			while (start >= currentPrime) {
				if (stop) return primes;
				if (++currentIndex >= primeCt) addNextPrime();
				currentPrime = primesSoFar[currentIndex];
			}
			primes[i] = currentPrime;
			start = currentPrime;
		}
		return primes;
	}
	public void stop() {
		stop = true;
	}
	/**
	 * Find next prime larger than largest in primesSoFar,
	 * and add to primesSoFar;
	 */
	private long addNextPrime() {
		long lastPrime = primesSoFar[primeCt - 1];
		for (long nextLong = lastPrime + 2; ; nextLong+=2) {
			boolean isPrime = true; // until proved otherwise
			for (int i = 0; i < primeCt; i++) {
				long l = primesSoFar[i];
				if (nextLong % l == 0) {
					isPrime = false;
					break;
				}
				if (l * l > nextLong) break;
			}
			if (isPrime) {
				addToList(nextLong);
				return nextLong;
			}
		}
	}
	/**
	 * Add newLong to primesSoFar. If primesSoFar is full, create a new primesSoFar,
	 * double in size. This method is not thread safe; to highlight the problem,
	 * a delay is introduced at the worst possible time, between updating the
	 * count and adding newLong. The method needs to be synchronised, either
	 * by the client or by an EJB container.
	 */
	private void addToList(long newLong) {
		if (primeCt >= primesSoFar.length) {
			long[] newPrimeList = new long[primesSoFar.length * 2];
			System.arraycopy(primesSoFar, 0, newPrimeList, 0, primeCt);
			primesSoFar = newPrimeList;
		}
		if (delay <= 0) {
			primesSoFar[primeCt++] = newLong;
			if (verbose) {
				System.out.println("Added " + newLong + " at pos " + (primeCt - 1));
			}
		}
		else {
			// code equivalent to the above line, but with
			// a delay at the worst moment:
			int oldPrimeCt = primeCt++;
			if (verbose) {
				System.out.println(Thread.currentThread().getName() +
					" primeCt incremented to " + primeCt);
				System.out.println("about to delay " + delay);
			}
			try { Thread.sleep(delay);
			} catch(InterruptedException ie) {}
			primesSoFar[oldPrimeCt] = newLong;
			if (verbose) {
				System.out.println(Thread.currentThread().getName() +
					" added " + newLong + " at pos " + oldPrimeCt);
			}
		}
	}
}