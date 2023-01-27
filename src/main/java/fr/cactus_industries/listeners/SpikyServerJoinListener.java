package fr.cactus_industries.listeners;

import fr.cactus_industries.commands.Commands;
import fr.cactus_industries.database.interaction.service.ServerService;
import fr.cactus_industries.database.schema.table.TGServerEntity;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

@Service
public class SpikyServerJoinListener implements ServerJoinListener {
    
    private final ServerService serverService;
    
    public SpikyServerJoinListener(ServerService serverService) {
        this.serverService = serverService;
    }
    
    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server server = event.getServer();
        System.out.println("SpikyBot vient de rejoindre le serveur "+server.getName() + " ("+server.getId()+").");
        
        // Ajout du serveur dans la BDD
        serverService.save(new TGServerEntity(server.getId(), new Date(Calendar.getInstance().getTime().getTime())));
        
        // Hard-codage du message indiquant l'ajout du bot sur un serveur sur STS
        final ServerTextChannel channelSTS = event.getApi().getServerById(739254091154456628L).get().getTextChannelById(979408422325862530L).get();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Arrivée de SpikyBot sur : "+server.getName() + " ("+server.getId()+")");
        final User owner = server.requestOwner().join();
        embedBuilder.setDescription("Propriétaire du serveur : "+ owner.getMentionTag()+" ("+ owner.getDiscriminatedName()+" | "+ owner.getId()+")\nDescription:\n"
                +server.getDescription().orElse("Aucune description de serveur."));
        embedBuilder.addInlineField("Info", "Date de création"+
                "\nNb membres"+
                "\nEst large ?"+
                "\nRégion"+
                "\nLangue préférée"+
                "\nNiveau de vérification"+
                "\nNSFW Level"
        );
        embedBuilder.addInlineField("", "<t:"+server.getCreationTimestamp().getEpochSecond()+">"+
                "\n"+server.getMemberCount()+
                "\n"+(server.isLarge()?"Oui":"Non")+
                "\n"+server.getRegion().getName()+
                "\n"+server.getPreferredLocale().getDisplayLanguage(Locale.FRANCE)+" | "+server.getPreferredLocale().getDisplayCountry(Locale.FRANCE)+
                "\n"+server.getVerificationLevel().name()+
                "\n"+server.getNsfwLevel().name()
        );
        channelSTS.sendMessage(embedBuilder);
        // Ajout des permissions pour les commandes
        Commands.setPermissionOnServer(server);
    }
}
