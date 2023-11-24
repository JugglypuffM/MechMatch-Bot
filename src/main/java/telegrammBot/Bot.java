package telegrammBot;


import database.main.Database;
import database.main.DatabaseService;
import io.github.cdimascio.dotenv.Dotenv;
import mainBot.MessageProcessor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private final Dotenv dotenv = Dotenv.load();
    private final Database database = new DatabaseService();
    private final MessageProcessor processor = new MessageProcessor(database);
    public Bot(){
        super(Dotenv.load().get("TG_BOT_TOKEN"));
    }

    public void send(String id, String username, String message, String[] reply) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(id);
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(id);
        for (int i = 0; i < 12; i++){
            if (reply[i] != null){
                System.out.println("-----------------------------------------------------------------------\n");
                System.out.println("-----------------------------------------------------------------------");
                System.out.println(id);
                System.out.println(username);
                System.out.println(message);
                String text = reply[i].replace("_", "\\_");
                if (reply[i+12] != null){
                    sendPhoto.setPhoto(new InputFile(reply[i+12]));
                    sendPhoto.setCaption(text);
                    System.out.println("Has photo: TRUE");
                    System.out.println(reply[i]);
                    System.out.println(reply[i+12]);
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace(System.out);
                        System.out.println("Message send fail");
                        continue;
                    }
                    System.out.println("Message sent successfully");
                }
                else {
                    sendMessage.setText(text);
                    System.out.println("Has photo: FALSE");
                    System.out.println(reply[i]);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace(System.out);
                        System.out.println("Message send fail");
                        continue;
                    }
                    System.out.println("Message sent successfully");
                }
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        int l = 0;
        if (message != null){
            l = message.length();
        }
        if (l <= 150){
            String[] reply;
            if (update.getMessage().hasPhoto()){
                reply = processor.processPhoto(chatId, update.getMessage().getPhoto().get(0).getFileId());
            }else {
                reply = processor.processMessage(chatId, message);
                if (reply[0].equals("требуется имя пользователя")){
                    reply = processor.processMessage(chatId, "username" + username);
                }
            }
            send(chatId, username, message, reply);
        }
        else{
            sendMessage.setText("Длинна сообщения слишком большая, введите не более 150-и символов");
            try {
                execute(sendMessage);
                System.out.println("-----------------------------------------------------------------------");
                System.out.println(chatId);
                System.out.println(username);
                System.out.println(message);
                System.out.println("Has photo: FALSE");
                System.out.println("Длинна сообщения слишком большая, введите не более 150-и символов");
                System.out.println("-----------------------------------------------------------------------");
            } catch (TelegramApiException e) {
                e.printStackTrace(System.out);
            }
        }
    }
    @Override
    public String getBotUsername() {
        return dotenv.get("TG_BOT_NAME");
    }
}