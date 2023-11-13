package telegrammBot;


import mainBot.MessageProcessor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private final MessageProcessor processor = new MessageProcessor();
    private final Map<String, String> env = System.getenv();
    private synchronized void printInfo(Message tgMessage, String[] reply){
        System.out.println("-----------------------------------------------------------------------");
        System.out.println(tgMessage.getChatId());
        System.out.println(tgMessage.getFrom().getUserName());
        System.out.println(tgMessage.getText());
        for (int i = 0; i < 12; i++){
            if (reply[i] != null){
                System.out.println(reply[i]);
            }
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        if (message.length() <= 150){
            String[] reply = processor.processMessage(chatId, message);
            if (reply[0].equals("требуется имя пользователя")){
                reply = processor.processMessage(chatId, "username" + username);
            }
            for (int i = 0; i < 12; i++){
                if (reply[i] != null){
                    sendMessage.setText(reply[i]);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace(System.out);
                    }
                }
            }
            printInfo(update.getMessage(), reply);
        }
        else{
            sendMessage.setText("Длинна сообщения слишком большая, введите не более 150-и символов");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace(System.out);
            }
            printInfo(update.getMessage(), new String[]{"Длинна сообщения слишком большая, введите не более 150-и символов"});
        }
    }
    @Override
    public String getBotUsername() {
        return env.get("testBotName");
    }

    @Override
    public String getBotToken() {
        return env.get("testBotToken");
    }
}