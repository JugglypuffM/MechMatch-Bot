package telegrammBot;


import database.main.Database;
import database.main.DatabaseService;
import io.github.cdimascio.dotenv.Dotenv;
import mainBot.MessageProcessor;
import org.slf4j.Logger;
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
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(Bot.class);
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
                logger.info("-----------------------------------------------------------------------\n");
                logger.info("-----------------------------------------------------------------------");
                logger.info(id);
                logger.info(username);
                logger.info(message);
                String text = reply[i].replace("_", "\\_");
                if (reply[i+12] != null){
                    sendPhoto.setPhoto(new InputFile(reply[i+12]));
                    sendPhoto.setCaption(text);
                    logger.info("Has photo: TRUE");
                    logger.info(reply[i]);
                    logger.info(reply[i+12]);
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        logger.error("Message send fail", e);
                        continue;
                    }
                    logger.info("Message sent successfully");
                }
                else {
                    sendMessage.setText(text);
                    logger.info("Has photo: FALSE");
                    logger.info(reply[i]);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        logger.error("Message send fail", e);
                        continue;
                    }
                    logger.info("Message sent successfully");
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
        if (l <= 500){
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
            sendMessage.setText("Длинна сообщения слишком большая, введите не более 500 символов");
            try {
                execute(sendMessage);
                logger.info("-----------------------------------------------------------------------");
                logger.info(chatId);
                logger.info(username);
                logger.info(message);
                logger.info("Has photo: FALSE");
                logger.info("Длинна сообщения слишком большая, введите не более 150-и символов");
                logger.info("-----------------------------------------------------------------------");
            } catch (TelegramApiException e) {
                logger.error("Message send fail", e);
            }
        }
    }
    @Override
    public String getBotUsername() {
        return dotenv.get("TG_BOT_NAME");
    }
}