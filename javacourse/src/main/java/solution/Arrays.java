package solution;

public class Arrays {
	public static void main(String[] args) {
		// part 1:
		String [] messages = new String[5];
		messages[0] = "Monday";
		messages[1] = "Tuesday";
		messages[2] = "Wednesday";
		messages[3] = "Thursday";
		messages[4] = "Friday morning";
		for (int i = 0; i < messages.length; i++) {
			System.out.println("messages[" + i + "]=" + messages[i]);
		}

		// part2:
		int [] series = new int[10];
		for (int i = 0; i < series.length; i++) {
			series[i] = i + 1;
		}
		int sum = 0;
		for (int i = 0; i < series.length; i++) {
			sum += series[i];
		}
		System.out.println("sum=" + sum);
		int n = series.length;
		System.out.println("n(n+1)/2=" + (n * (n + 1) / 2));

		// part3:
		char [] directions = {'N', 'S', 'E', 'N', 'W', 'S'};
		char c;
		for (int i = 0; i < directions.length; i++) {
			switch(c = directions[i]) {
				case 'N':
				System.out.println("North");
				break;
				case 'S':
				System.out.println("South");
				break;
				case 'E':
				System.out.println("East");
				break;
				case 'W':
				System.out.println("West");
				break;
				default:
				System.out.println("Unknown direction: " + c);
			}
		}
	}
}
