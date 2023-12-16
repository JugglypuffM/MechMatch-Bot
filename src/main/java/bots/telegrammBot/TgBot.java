package bots.telegrammBot;


import bots.Bot;
import bots.BotDriver;
import bots.platforms.Platform;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;

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
        String platformId = update.getMessage().getChatId().toString();
        String username = update.getMessage().getFrom().getUserName();
        String[] reply = new String[24];
        if (update.getMessage().hasPhoto()){
            try {
                String picture =  driver.getDatabase().getAccountWithPlatformId(platformId, Platform.TELEGRAM).getId() + ".jpg";
                downloadFile(execute(new GetFile(update.getMessage().getPhoto().get(2).getFileId())).getFilePath(),
                                     new File("./pictures/" + picture));
                reply = driver.handleUpdate(platformId, username, picture, Platform.TELEGRAM, true);
            }catch (Exception e){
                driver.getLogger().error("Failed to download the image.", e);
                reply[0] = "Не удалось загрузить твою фотографию, отправь другую или попробуй еще раз.";
            }
        }else reply = driver.handleUpdate(platformId, username, message, Platform.TELEGRAM, false);
        driver.send(this, platformId, username, message, reply);
    }
    @Override
    public String getBotUsername() {
        return dotenv.get("TG_BOT_NAME");
    }

    @Override
    public boolean executePhoto(String platformId, String message, String photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(platformId);
        String text = message.replace("_", "\\_");
        File file = new File("./pictures/" + photo);
        sendPhoto.setPhoto(new InputFile(file));
        sendPhoto.setCaption(text);
        buttonsHandler.setKeyboard(platformId, null, sendPhoto);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            driver.getLogger().error("", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean executeText(String platformId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(platformId);
        String text = message.replace("_", "\\_");
        sendMessage.setText(text);
        buttonsHandler.setKeyboard(platformId, sendMessage, null);
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