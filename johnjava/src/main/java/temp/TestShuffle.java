package temp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by john.denny@gmail.com on 07/01/2026.
 */
public class TestShuffle {
    private class QA {
        String name;
        String answer;

        QA(String nam, String ans) {
            name = nam;
            answer = ans;
        }
    }
    private final Map<String, String> answers =
            new HashMap<>();
    private final QA[] qaArray = {
            new QA("correct", "cider"),
            new QA("john", "mild"),
            new QA("julie", "red wine"),
            new QA("joe", "bitter")
    };

    public static void main(String[] args) {
        new TestShuffle().go();
    }
    private void go() {
        for (QA qa : qaArray) {
            answers.put(qa.name, qa.answer);
        }

        // This is the code to go into onMessage > all answers received
        Set<String> nameSet = answers.keySet();
        List<String> shuffledNameList = new ArrayList<>();
        for (String name : nameSet) {
            shuffledNameList.add(name);
        }
        Collections.shuffle(shuffledNameList);
        StringBuffer buffer = new StringBuffer("ALL_ANSWERS" + "|" + 1);
        for (String name: shuffledNameList) {
            buffer.append("|" + answers.get(name));
        }
        String message = buffer.toString();

        // This is the code to go into onItemClicked()
        String nameI, answerI;
        for (int i = 0; i < answers.size(); i++) {
            nameI = shuffledNameList.get(i);
            answerI = answers.get(nameI);
            System.out.println("answers[" + i + "]=(" + nameI + ", " + answerI + ")");
        }
        String realAnswer = answers.get("correct");
        System.out.println("realAnswer=" + realAnswer);
    }

}
