package fr.cactus_industries;

import fr.cactus_industries.tools.Tisstober;
import fr.cactus_industries.tools.tickets.TicketsMessageManager;
import fr.cactus_industries.listeners.ReactionManager;
import fr.cactus_industries.listeners.MessageListener;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.DiscordApi;
import fr.cactus_industries.tools.ConfigSpiky;
import org.javacord.api.entity.intent.Intent;

public class Main{
    
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
        
        // Initialisation Tisstober (amené à être remplacé)
        Tisstober.Initiate(api);
        
        System.out.println("Bot invite link: " + api.createBotInvite());
    }
    
    public static void Disconnect(DiscordApi api) {
        DBInterface.Disconnect(api);
    }
}
