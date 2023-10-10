package mainBot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Examiner {
    private static final Random rand = new Random();
    private static final Map<String, String> questionList = new HashMap<>();
    public static Map<String, String> getQuestionList(){
        return questionList;
    }
    public static String generateQuestion(){
        int r1 = rand.nextInt(10);
        int r2 = rand.nextInt(10);
        if(!questionList.containsKey(r1 + "+" + r2)){
            questionList.put(r1 + "+" + r2, r1+r2+"");
        }
        return r1 + "+" + r2;
    }
    public static boolean checkAnswer(String question, String answer){
        return questionList.get(question).equals(answer);
    }
}
