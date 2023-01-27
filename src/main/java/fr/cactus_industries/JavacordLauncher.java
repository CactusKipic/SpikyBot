package fr.cactus_industries;

import fr.cactus_industries.commands.Commands;
import fr.cactus_industries.listeners.*;
import fr.cactus_industries.tisseurs.BumperListener;
import fr.cactus_industries.tools.ConfigSpiky;
import fr.cactus_industries.tools.Tisstober;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class JavacordLauncher {
    
    static HashMap<Long, Long> newUserMemory = new HashMap<>(); // User, Time
    
    private DiscordApi api;
    
    public JavacordLauncher(SpikyServerJoinListener spikyServerJoinListener, SpikyRoleChangePermissionListener spikyRoleChangePermissionListener,
                            SpikyOwnerChangeListener spikyOwnerChangeListener, ReactionManager.ReactionAdded reactionAdded) {
        System.out.println("Lancement du bot...");
        
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
        api = new DiscordApiBuilder().setToken(token).setAllIntentsExcept(Intent.GUILD_PRESENCES).login().join();
        
        // TODO update
        // Ajout des listeners de mise à jour (server join, server leave, modif role)
        api.addServerJoinListener(spikyServerJoinListener);
        api.addRoleChangePermissionsListener(spikyRoleChangePermissionListener);
        api.addServerChangeOwnerListener(spikyOwnerChangeListener);
    
        // Ajout des listeners de base
        api.addMessageCreateListener(new MessageListener(prefix));
        api.addReactionAddListener(reactionAdded);
    
        // Initialisation Tisstober (amené à être remplacé)
        Tisstober.Initiate(api);
    
        // Ajout des commandes
        Commands.addCommands(api);
        // Hardcode pour les Tisseurs
        {
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
        }
    
        System.out.println("Bot invite link: " + api.createBotInvite());
        
        System.out.println("Le bot est prêt à piquer des culs !!!");
    }
    
    @Bean
    public DiscordApi getApi() {
        return api;
    }
}
