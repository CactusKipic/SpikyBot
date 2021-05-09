package fr.cactus_industries;

import fr.cactus_industries.tools.Tisstober;
import fr.cactus_industries.tools.pdfreading.PDFCommandHandler;
import fr.cactus_industries.tools.tickets.TicketsMessageManager;
import fr.cactus_industries.listeners.ReactionManager;
import fr.cactus_industries.listeners.MessageListener;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.DiscordApi;
import fr.cactus_industries.tools.ConfigSpiky;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.Reaction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main{
    
    static HashMap<Long, String> memory = new HashMap<>();
    
    static HashMap<Long, Long> newUserMemory = new HashMap<>(); // User, Time
    
    public static void main(String[] args0) {
        System.out.println("Démarrage !");
        // Initialisation config locale
        if (!ConfigSpiky.init()) {
            System.out.println("Could not load config.yml file (from resource and/or local).");
            return;
        }
        // Récupération du token depuis la config locale
        String token = ConfigSpiky.getConfigString("bot-token");
        if (token.equals("")) {
            System.out.println("Bot token is empty, add a correct token to start the bot in the config.yml.");
            return;
        }
        // Définition du préfixe des commandes
        String prefix = ConfigSpiky.getConfigString("command-prefix");
        if (prefix.length() == 0) {
            System.out.println("Prefix is null, setting default prefix '!'.");
            prefix = "!";
        }
        // Connexion
        DiscordApi api = new DiscordApiBuilder().setToken(token).setAllIntentsExcept(Intent.GUILD_PRESENCES).login().join();
        
        // Ajout des listeners de base
        api.addMessageCreateListener(new MessageListener(prefix));
        api.addReactionAddListener(new ReactionManager.ReactionAdded());
        
        // Initialisation des tickets
        TicketsMessageManager.init(api);
        
        // Initialisation de la lecture de PDF
        PDFCommandHandler.init(api);
        
        // Initialisation Tisstober (amené à être remplacé)
        Tisstober.Initiate(api);
        
        // Les tisseurs c'est un vrai moulin !!
        api.getServerById(555169863291895814L).get().addServerMemberJoinListener(event -> {
            long userId = event.getUser().getId();
            long curTime = new Date().getTime();
            newUserMemory = new HashMap<>(newUserMemory.entrySet().stream().filter(e -> e.getValue() + 300_000 > curTime)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            newUserMemory.put(userId, curTime);
        });
        api.getServerById(555169863291895814L).get().addServerMemberLeaveListener(event -> {
            long userId = event.getUser().getId();
            long curTime = new Date().getTime();
            newUserMemory = new HashMap<>(newUserMemory.entrySet().stream().filter(e -> e.getValue() + 300_000 > curTime)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            if(newUserMemory.containsKey(userId)){
                System.out.println("Ca faisait moins de 5 minutes.");
                newUserMemory.remove(userId);
                event.getServer().getTextChannelById(580774929767596043L).get()
                        .sendMessage("https://tenor.com/view/in-and-out-in-out-the-simpsons-simpsons-gif-21378859");
            } else
                System.out.println("Ca faisait plus de 5 minutes.");
        });
        
        System.out.println("Bot invite link: " + api.createBotInvite());
    }
    
    public static void Disconnect(DiscordApi api) {
        DBInterface.Disconnect(api);
    }
}
