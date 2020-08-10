package fr.cactus_industries;

import fr.cactus_industries.listeners.MessageListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.File;

public class Main {
    
    public static void main(String[] args0){
        String token = "";
        Yaml yaml = new Yaml();
        File f = new File("./config.yml");
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        api.addMessageCreateListener(new MessageListener("!"));
        
        System.out.println("Bot invite link: " + api.createBotInvite());
    }


}
