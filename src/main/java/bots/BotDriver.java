package bots;

import bots.platforms.Platform;
import database.main.Database;
import database.entities.Account;
import logic.MessageProcessor;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BotDriver {
    private final Database database;
    private MessageProcessor processor;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(BotDriver.class);
    private final Map<Platform, Bot> onlineBots = new HashMap<>();
    public BotDriver(Database m_database){
        this.database = m_database;
    }
    public String[] handleUpdate(String platformId, String username, String message, Platform platform, boolean hasPhoto){
        if (this.processor == null){
            logger.error("MessageProcessor was not set up");
            return new String[24];
        }
        String[] reply = new String[24];
        if (message.length() > 500){
            reply[0] = "Длинна сообщения слишком большая, введите не более 500 символов.";
        }
        else if (hasPhoto){
            reply = processor.processPhoto(platformId, platform, message);
        }else {
            reply = processor.processMessage(platformId, platform, message);
            if (reply[0].startsWith("data")){
                Account account = database.getAccountWithLogin(reply[0].substring(4));
                account.setPlatformUsername(username, platform);
                account.setPlatformId(platformId, platform);
                database.updateAccount(account);
                reply = processor.processMessage(platformId, platform, message);
            }
        }
        return reply;
    }
    public void send(Bot bot, String platformId, String username, String message, String[] reply){
        for (int i = 0; i < 12; i++){
            if (reply[i] != null){
                logger.info("-----------------------------------------------------------------------\n");
                logger.info("-----------------------------------------------------------------------");
                logger.info("Platform: " + bot.getPlatform());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                logger.info(dateFormat.format(date));
                logger.info(platformId);
                logger.info(username);
                logger.info("Message: " + message);
                if (reply[i+12] != null){
                    logger.info("Has photo: TRUE");
                    logger.info(reply[i]);
                    logger.info(reply[i+12]);
                    if(bot.executePhoto(platformId, reply[i], reply[i+12])){
                        logger.info("Message sent successfully");
                    }else {
                        logger.error("Message send fail");
                    }
                }
                else {
                    logger.info("Has photo: FALSE");
                    logger.info(reply[i]);
                    if(bot.executeText(platformId, reply[i])){
                        logger.info("Message sent successfully");
                    }else {
                        logger.error("Message send fail");
                    }
                }
            }
        }
    }
    public Map<Platform, Bot> getOnlineBots(){
        return onlineBots;
    }
    public void addOnlineBot(Platform platform, Bot bot){
        onlineBots.put(platform, bot);
    }

    public Database getDatabase() {
        return database;
    }

    public void setProcessor(MessageProcessor processor) {
        this.processor = processor;
    }

    public Logger getLogger() {
        return logger;
    }
}
