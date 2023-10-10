package mainBot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ExaminerTests{
    @Test
    public void questionGeneratorTest(){
        String question = Examiner.generateQuestion();
        int answer = Integer.parseInt(String.valueOf(question.charAt(0))) + Integer.parseInt(String.valueOf(question.charAt(2)));
        Map<String, String> questionList = Examiner.getQuestionList();
        Assertions.assertEquals(questionList.get(question), answer+"");
    }
    @Test
    public void checkAnswerTest(){
        String question = Examiner.generateQuestion();
        int answer = Integer.parseInt(String.valueOf(question.charAt(0))) + Integer.parseInt(String.valueOf(question.charAt(2)));
        Assertions.assertTrue(Examiner.checkAnswer(question, answer+""));
        Assertions.assertFalse(Examiner.checkAnswer(question, "2"));
    }
}
