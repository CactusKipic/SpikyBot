package fr.cactus_industries;

import fr.cactus_industries.commands.Commands;
import fr.cactus_industries.commands.SlashCommandListener;
import fr.cactus_industries.listeners.*;
import fr.cactus_industries.tisseurs.BumperListener;
import fr.cactus_industries.tools.Tisstober;
import fr.cactus_industries.tools.pdfreading.PDFCommandHandler;
import fr.cactus_industries.tools.pdfreading.PDFSlashHandler;
import fr.cactus_industries.tools.tickets.TicketsPermissionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.DiscordApi;
import fr.cactus_industries.tools.ConfigSpiky;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collectors;

//@SpringBootApplication
public class Main{
    
    static HashMap<Long, Long> newUserMemory = new HashMap<>(); // User, Time
    
    static final Logger logger = LogManager.getLogger(Main.class);
    
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
        
        // Ajout des listeners de mise à jour (server join, server leave, modif role)
        /*api.addServerJoinListener(SpikyServerJoinListener.getInstance());
        api.addRoleChangePermissionsListener(SpikyRoleChangePermissionListener.getInstance());
        api.addServerChangeOwnerListener(SpikyOwnerChangeListener.getInstance());
        
        // Ajout des listeners de base
        api.addMessageCreateListener(new MessageListener(prefix));
        api.addReactionAddListener(new ReactionManager.ReactionAdded());
        
        // Initialisation des tickets
        TicketsPermissionManager.init(api);
        
        // Initialisation de la lecture de PDF
        PDFSlashHandler.init(api);
        
        // Initialisation Tisstober (amené à être remplacé)
        Tisstober.Initiate(api);
        
        // Ajout des commandes
        Commands.addCommands(api);
        
        // Ajout du listener pour les Slashcommand
        api.addSlashCommandCreateListener(new SlashCommandListener());*/
        
        // Ajout du listener pour le !d bump scoreboard
        api.getServerById(555169863291895814L).get()
                .getTextChannelById(627913609908977684L).get().addMessageCreateListener(new BumperListener());
        
        // Les tisseurs c'est un vrai moulin !!
        api.getServerById(555169863291895814L).get().addServerMemberJoinListener(event -> {
            long userId = event.getUser().getId();
            long curTime = new Date().getTime();
            if(event.getServer().getMemberCount() == 420) {
                ServerTextChannel textChannel = event.getServer().getTextChannelById(580774929767596043L).get();
                Message mess420 = textChannel.sendMessage("Ca y est, c'est enfin arrivé ! Nous sommes désormais 420 sur le serveur !").join();
                new MessageBuilder().setContent("https://tenor.com/view/420-blaze-it-simpsons-the-gif-13774936")
                        .replyTo(mess420).send(textChannel).join();
                new MessageBuilder().setContent("https://tenor.com/view/the-simpson-homer-simpson-drive-chill-high-gif-8367291")
                        .replyTo(mess420).send(textChannel);
            }
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
