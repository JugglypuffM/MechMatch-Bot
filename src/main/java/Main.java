import bots.BotDriver;
import bots.discordBot.DsBot;
import bots.platforms.Platform;
import bots.telegrammBot.TgBot;
import database.main.Database;
import database.main.DatabaseService;
import logic.MessageProcessor;
import org.apache.log4j.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        try {
            FileAppender appender = new FileAppender(new SimpleLayout(), "./logs/logs.log");
            appender.setEncoding("UTF-8");
            appender.activateOptions();
            Logger.getRootLogger().addAppender(appender);
        }catch (IOException e){
            Logger.getRootLogger().error("Failed to initialize File Appender.", e);
        }
        Database database = new DatabaseService();
        BotDriver driver = new BotDriver(database);
        MessageProcessor processor = new MessageProcessor(database, driver);
        driver.setProcessor(processor);
        TgBot tgBot = new TgBot(driver);
        if (tgBot.start()) driver.addOnlineBot(Platform.TELEGRAM, tgBot);
        else Logger.getRootLogger().error("Telegram bot start failed.");
        DsBot dsBot = new DsBot(driver);
        if (dsBot.start()) driver.addOnlineBot(Platform.DISCORD, dsBot);
        else Logger.getRootLogger().error("Discord bot start failed.");
    }
}
