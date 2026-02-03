package jarden.fileutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by john.denny@gmail.com on 02/02/2026.
 */
public class MergeQuizFiles {
    private static boolean debug = false;
    /*
    Read in text files from a specified directory
    for each file:
        for each line:
            create a TypeQuestionAnswer (TQA) object and add to collection
        randomise the collection
     for each collection:
        take the next TQA and add it to targetCollection
        repeat until one collection is empty
     write targetCollection to a resultFile
     */
    public static void main(String[] args) throws IOException {
        String directoryName = "/Users/john/codswallopQuestions";
        if (args.length > 0) {
            directoryName = args[0];
        }
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            System.out.println(directoryName + " is not a directory");
            return;
        }
        File[] files = directory.listFiles();
        int smallestQuestionList = -1;
        ArrayList<ArrayList<String>> filesLines = new ArrayList<>();
        for (File file : files) {
            ArrayList<String> fileLines = getLinesFromFile(file);
            if (smallestQuestionList < 0) {
                smallestQuestionList = fileLines.size();
            } else {
                if (fileLines.size() < smallestQuestionList) {
                    smallestQuestionList = fileLines.size();
                }
            }
            Collections.shuffle(fileLines);
            filesLines.add(fileLines);
        }
        System.out.println("number of files=" + filesLines.size() +
                "; minimum lines in a file=" + smallestQuestionList);
        ArrayList<String> targetLines = new ArrayList<>();
        for (int i = 0; i < smallestQuestionList; i++) {
            for (ArrayList<String> fileLines: filesLines) {
                targetLines.add(fileLines.get(i));
            }
        }
        if (debug) {
            System.out.println("targetLines:");
            for (String line : targetLines) {
                System.out.println(line);
            }
        }
        Path questionsPath = Paths.get("/Users/john/Desktop/questions.txt");
        writeLinesToFile(targetLines, questionsPath);
        System.out.println("buenas noches");
    }
    private static ArrayList<String> getLinesFromFile(File file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.defaultCharset()) ) {
            ArrayList<String> fileList = new ArrayList<>();
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                fileList.add(line);
            }
            return fileList;
        }
    }
    public static void writeLinesToFile(ArrayList<String> lines, Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path, Charset.defaultCharset());
             PrintWriter printWriter = new PrintWriter(writer)) {
            for(String line: lines) {
                printWriter.println(line);
            }
        }
    }
}
