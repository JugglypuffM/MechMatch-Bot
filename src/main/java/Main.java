import org.apache.log4j.*;
import telegrammBot.BotRegistrar;

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
            System.out.println("Failed to initialize logger.");
            e.printStackTrace(System.out);
        }
        BotRegistrar registrar = new BotRegistrar();
        registrar.start();
    }
}
