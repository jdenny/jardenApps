package solution.junit;

public class MySort {
	public static void sort(int[] numbers) {
		for (int i = 0; i < (numbers.length - 1); i++) {
			for (int j = i; j < numbers.length; j++) {
				if (numbers[i] > numbers[j]) {
					int swap = numbers[i];
					numbers[i] = numbers[j];
					numbers[j] = swap;
				}
			}
		}
	}
	public static void reverseSort(int[] numbers) {
		for (int i = 0; i < (numbers.length - 1); i++) {
			for (int j = i; j < numbers.length; j++) {
				if (numbers[i] < numbers[j]) {
					int swap = numbers[i];
					numbers[i] = numbers[j];
					numbers[j] = swap;
				}
			}
		}
	}

}
