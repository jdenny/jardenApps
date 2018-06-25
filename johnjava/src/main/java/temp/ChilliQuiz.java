package temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;
import jarden.quiz.Quiz;

/**
 * Created by john.denny@gmail.com on 22/06/2018.
 */

public class ChilliQuiz {
    public static void main(String[] args) {
        String fileName = "johnjava/src/main/java/data/chilliquiz.properties";
        try {
            File file = new File(fileName);
            File fileData = new File("johnjava/src/main/java/data");
            System.out.println("Exists=" + fileData.exists());
            System.out.println("path=" + fileData.getAbsolutePath());
            FileInputStream fis = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fis);
            Quiz quiz = new PresetQuiz(properties);
            fis.close();
            String input;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String question = quiz.getNextQuestion(1);
                System.out.println("what does the last of following bids mean: " + question);
                input = scanner.nextLine();
                System.out.println(quiz.getCorrectAnswer());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (EndOfQuestionsException e) {
            System.out.println("end of questions");
        }
    }
}
