package temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by john.denny@gmail.com on 22/06/2018.
 */

public class HelloJohn {
    public static void main(String[] args) throws IOException {
        System.out.println("hello John");
        File file = new File("hotbridge/src/main/res/raw/reviseit.txt");
        System.out.println("exists=" + file.exists());
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(isr);
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        String regex = "Q: 1C, 1H.*";
        for (String lineA: lines) {
            if (lineA.matches(regex)) {
                System.out.println(lineA);
            }
        }
        System.out.println("adios amigo");
    }
}
