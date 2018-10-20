package quiz;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JardenAppLibUtils {
	public static void main(String[] args) throws IOException {
		String srcFileName = "/Users/John/git/JardenProviders/res/raw/engspa.txt";
		String destFileName = "/Temp/words.txt";
		int count = AnagramQuiz.engSpa2Word(
				new FileInputStream(srcFileName),
				new FileOutputStream(destFileName));
		System.out.println(count + " words written to " + destFileName);

	}

}
