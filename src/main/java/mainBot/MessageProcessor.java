package mainBot;

import java.util.HashMap;
import java.util.Map;

public class MessageProcessor {
    private static final Map<String, User> userList = new HashMap<>();
    public static Map<String, User> getUserList(){
        return userList;
    }
    public static String giveHelp(){
        return "Привет, я - Арифметический бот.\nЯ люблю давать арифметические задачки, при чем настолько, что если начну, то не остановлюсь)))";
    }
    public static User initUser(String id){
        User user;
        if (!userList.containsKey(id)){
            user = new User(id);
            userList.put(id, user);
        }else {
            user = userList.get(id);
        }
        return user;
    }
    public static String[] processMessage(String userId, String message){
        String[] answer = new String[2];
        User sender = initUser(userId);

        if(message.equals("/start") || message.equals("/help")){
            answer[0] = giveHelp();
        }else if (sender.getLastReply().equals("question")){

            if (Examiner.checkAnswer(sender.getCurrentQuestion(), message)){
                answer[0] = "Да ты крут!\nМожешь идти во второй класс, наверное...";
                sender.setLastReply("answer");
            }else {
                answer[0] = "Да ты глуп!\nМожет сходишь в первый класс, что-ли...";
            }
        }else {
            answer[0] = "none";
        }
        if (sender.getLastReply().equals("answer")){
            String question = Examiner.generateQuestion();
            answer[1] = question + " = ?";
            sender.setCurrentQuestion(question);
            sender.setLastReply("question");
        }else{
            answer[1] = sender.getCurrentQuestion() + " = ?";
        }
        return answer;
    }
}
