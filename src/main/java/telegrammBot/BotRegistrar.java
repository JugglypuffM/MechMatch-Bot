package telegrammBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotRegistrar {
    public static Bot bot = new Bot();
    public static boolean isInit = false;
    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            isInit = true;
        } catch (TelegramApiException e) {
            e.printStackTrace(System.out);
        }
    }
}
