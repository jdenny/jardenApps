package demo.util;

public class Sort {
	private static int[] numbers = {12, -5, 14, 7, -9};

	public static void main(String[] args) {
		sort(numbers);
		for (int num: numbers) {
			System.out.println(num + " ");
		}
	}
	private static void sort(int[] nums) {
		for (int i = 1; i < nums.length; i++) {
			for (int j = 0; j < i; j++) {
				if (nums[i-j] < nums[i-j-1]) {
					int temp = nums[i-j];
					nums[i-j] = nums[i-j-1];
					nums[i-j-1] = temp;
				}
			}
		}
	}
}
