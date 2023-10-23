package mainBot;

public class User{
    private static String m_id;
    private String m_lastReply, m_currentQuestion;
    public User(String id){
        m_id = id;
        m_lastReply = "answer";
    }
    public static String getId(){
        return m_id;
    }
    public String getLastReply(){
        return m_lastReply;
    }
    public void setLastReply(String nextReply) {
        this.m_lastReply = nextReply;
    }
    public String getCurrentQuestion(){
        return m_currentQuestion;
    }
    public void setCurrentQuestion(String newQuestion){
        m_currentQuestion = newQuestion;
    }
}
