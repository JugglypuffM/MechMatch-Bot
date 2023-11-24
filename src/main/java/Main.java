import org.apache.log4j.*;
import telegrammBot.BotRegistrar;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        try {
            Logger.getRootLogger().addAppender(new FileAppender(new SimpleLayout(), "./logs/logs.log"));
        }catch (IOException e){
            System.out.println("Failed to initialize logger.");
            e.printStackTrace(System.out);
        }
        BotRegistrar registrar = new BotRegistrar();
        registrar.start();
    }
}
