package temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by john.denny@gmail.com on 22/06/2018.
 */

public class HelloJohn {
    public static void main(String[] args) throws IOException {
        System.out.println("hello John");
        File file = new File(".");
        System.out.println(file.getAbsoluteFile());
        file = new File("johnjava/src/main/java/data/chilliquiz.properties");
        System.out.println("exists=" + file.exists());
        Properties termProps = new Properties();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        termProps.load(isr);
        Enumeration<?> names = termProps.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            System.out.println(name + ", " + termProps.getProperty(name));
        }
        System.out.println("adios amigo");
    }
}
