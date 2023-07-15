package temp;

import java.util.Random;

/**
 * Created by john.denny@gmail.com on 14/07/2023.
 */
public class TestSpanish {
    private static Random random = new Random();
    private static String[] goList = {
            "we will go",
            "I went",
            "he used to go"
    };
    private static int listSize = goList.length;

    public static void main(String[] args) {
        System.out.println("Hello John 15:55");
        String[] engList = {
                "it is sunny, <go> to the beach",
                "<go> to the beach",
                "it is sunny, <go>",
                "<go>"
        };
        for (String eng : engList) {
            parse(eng);
        }
        System.out.println("goodbye");
    }
    private static void parse(String eng) {
        int lt = eng.indexOf('<');
        if (lt < 0) {
            System.out.println("no '<' in eng");
        } else {
            int gt = eng.indexOf('>', lt);
            if (gt < 0) {
                System.out.println("no '>' in eng");
            } else {
                System.out.println("rawVerb=" + eng.substring(lt+1, gt));
                StringBuffer sb = new StringBuffer(eng);
                int rand = random.nextInt(listSize);
                sb.replace(lt, gt+1, goList[rand]);
                System.out.println(sb);
            }
        }


    }
}
