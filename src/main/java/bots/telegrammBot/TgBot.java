package bots.telegrammBot;


import bots.Bot;
import bots.BotDriver;
import bots.platforms.Platform;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TgBot extends TelegramLongPollingBot implements Bot {
    private final Dotenv dotenv = Dotenv.load();
    private final BotDriver driver;
    private final ButtonsHandler buttonsHandler;
    public TgBot(BotDriver m_driver){
        super(Dotenv.load().get("TG_BOT_TOKEN"));
        this.driver = m_driver;
        this.buttonsHandler = new ButtonsHandler(driver.getDatabase());
    }
    public boolean start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            driver.getLogger().error("Bot registration failed", e);
            return false;
        }
        return true;
    }
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String id = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();
        String[] reply = (update.getMessage().hasPhoto()) ?
                driver.handleUpdate(id, username, update.getMessage().getPhoto().get(0).getFileId(), true) :
                driver.handleUpdate(id, username, message, false);
        driver.send(this, id, username, message, reply);
    }
    @Override
    public String getBotUsername() {
        return dotenv.get("TG_BOT_NAME");
    }

    @Override
    public boolean executePhoto(String id, String message, String photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(id);
        String text = message.replace("_", "\\_");
        sendPhoto.setPhoto(new InputFile(photo));
        sendPhoto.setCaption(text);
        buttonsHandler.setKeyboard(id, null, sendPhoto);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            driver.getLogger().error("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean executeText(String id, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        String text = message.replace("_", "\\_");
        sendMessage.setText(text);
        buttonsHandler.setKeyboard(id, sendMessage, null);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            driver.getLogger().error("", e);
            return false;
        }
        return true;
    }

    @Override
    public Platform getPlatform() {
        return Platform.TELEGRAM;
    }
}