package fr.cactus_industries;

import fr.cactus_industries.listeners.MessageListener;
import fr.cactus_industries.tools.Tisstober;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class Main {
    
    private static DBInterface DB;
    
    public static void main(String[] args0){
        String token = "";
        
        System.out.println("Démarrage !");
        
        Yaml yaml = new Yaml();
        File f = new File("./config.yml");
        if(!f.exists()){ // Load config file from resources
            try{
                InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("config.yml");
                OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                byte[] buff = new byte[1024];
                int length;
                while((length = in.read(buff))>0){
                    out.write(buff, 0, length);
                }
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // Load default config.yml if not existing
        Map<String, Object> config = null;
        try {
             config = yaml.load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(config == null){
            System.out.println("Could not load config.yml file (from resource and/or local).");
            return;
        }
        token = (String) config.get("bot-token");
        if(token.equals("")){
            System.out.println("Bot token is empty, add a correct token to start the bot in the config.yml.");
            return;
        } // Check token value is not empty
        String prefix = (String) config.get("command-prefix");
        if(prefix.length() == 0) {
            System.out.println("Prefix is null, setting default prefix '!'.");
            prefix = "!";
        } // Prefix value check
        String url = "jdbc:postgresql://"+(String) config.get("postgresql.address") +"/"+ (String) config.get("postgresql.database"),
                user = (String) config.get("postgresql.user"),
                pass = (String) config.get("postgresql.password");
        
        
        
        DB = new DBInterface(url, user, pass);
        
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join(); // Create the api with the given token and log in
        api.addMessageCreateListener(new MessageListener(prefix));
    
        Tisstober.Initiate(api);
        
        System.out.println("Bot invite link: " + api.createBotInvite());
    }
    
    public static void Disconnect(DiscordApi api){
        DB.Disconnect(api);
    }
    
}
