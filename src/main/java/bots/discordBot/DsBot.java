package bots.discordBot;

import bots.Bot;
import bots.BotDriver;
import bots.platforms.Platform;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class DsBot extends ListenerAdapter implements Bot {
    private final Dotenv dotenv = Dotenv.load();
    private final BotDriver driver;
    private final JDA jda = JDABuilder.createDefault(dotenv.get("DS_BOT_TOKEN"))
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .build();
    public DsBot(BotDriver m_driver){
        this.driver = m_driver;
    }
    public boolean start() {
        try {
            jda.addEventListener(new DsBot(this.driver));
            jda.awaitReady();
        } catch (InterruptedException e) {
            driver.getLogger().error("Bot registration failed", e);
            return false;
        }
        return true;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }
        String message = event.getMessage().getContentDisplay();
        String id = event.getChannel().getId();
        String username = event.getAuthor().getName();
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        String[] reply = (!attachments.isEmpty() && attachments.get(0).isImage()) ?
                driver.handleUpdate(id, username, attachments.get(0).getUrl(), Platform.DISCORD, true) :
                driver.handleUpdate(id, username, message, Platform.DISCORD, false);
        driver.send(this, id, username, message, reply);
    }


    @Override
    public synchronized boolean executePhoto(String id, String message, String photo) {
        try {
            PrivateChannel channel = jda.getPrivateChannelById(id);
            if (channel == null) return false;
            URL url = new URL(photo);
            BufferedImage img = ImageIO.read(url);
            String[] filename = photo.split("\\?ex")[0].split("/")[6].split("\\.");
            String extension = filename[filename.length-1];
            File file = new File("./tmp." + extension);
            ImageIO.write(img, extension, file);
            channel.sendMessage(message).addFiles(FileUpload.fromData(file)).queue();
            while (!file.delete()) {}
            return true;
        }catch (Exception e){
            driver.getLogger().error("Failed to handle the image.", e);
            return false;
        }
    }

    @Override
    public boolean executeText(String id, String message) {
        PrivateChannel channel = jda.getPrivateChannelById(id);
        if (channel != null){
            channel.sendMessage(message).queue();
            return true;
        }
        return false;
    }

    @Override
    public Platform getPlatform() {
        return Platform.DISCORD;
    }
}
