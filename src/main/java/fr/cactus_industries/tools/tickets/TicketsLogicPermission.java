package fr.cactus_industries.tools.tickets;

import fr.cactus_industries.database.interaction.service.TicketService;
import fr.cactus_industries.database.schema.table.TTicketChannelEntity;
import fr.cactus_industries.database.schema.table.TTicketGrantedEntity;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelUpdater;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketsLogicPermission {
    
    private final TicketService ticketService;
    private final DiscordApi api;
    private final Timer timer = new Timer();
    
    public TicketsLogicPermission(TicketService ticketService, DiscordApi api) {
        this.ticketService = ticketService;
        this.api = api;
    }
    
    // Récupération des personnes ayant encore un ticket et traitement de leur état (prog retrait ou retrait)
    @PostConstruct
    public void init(){
        final List<TTicketGrantedEntity> allGranted = ticketService.findAllGranted();
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        final Date time = calendar.getTime();
        final Map<Long, Map<Long, Map<Boolean, List<TTicketGrantedEntity>>>> serverChannelEnded = allGranted.stream().collect(
                Collectors.groupingBy(TTicketGrantedEntity::getServer,
                        Collectors.groupingBy(TTicketGrantedEntity::getServer,
                                Collectors.groupingBy(ticketGranted -> ticketGranted.getEndGrant().before(time)))));
        // Pour chaque serveur sur chaque salon on retire ou on résume la permission d'écriture
        serverChannelEnded.forEach((serveur, channelEnded) -> {
            final Server server = api.getServerById(serveur).orElse(null);
            if(server == null) {
                log.info("Le serveur "+serveur+" n'a pas été trouvé. Impossible de résumer les permissions de tickets.");
                return;
            }
            channelEnded.forEach((channel, ended) -> {
                final ServerTextChannel textChannel = server.getTextChannelById(channel).orElse(null);
                if(textChannel == null) {
                    log.info("Le salon "+channel+" sur le serveur "+server.getName()+" ("+serveur+") n'a pas été trouvé. Impossible de résumer les permissions de tickets.");
                    return;
                }
                // Permission terminée
                ended.get(true).forEach(ticketGranted -> {
                    final User user = api.getUserById(ticketGranted.getUserid()).join();
                    if(removeWritePermission(textChannel, user) == 0)
                        ticketService.removeGranted(ticketGranted);
                });
                // Ajout du retrait de la permission à la date prévue
                ended.get(false).forEach(ticketGranted -> {
                    final User user = api.getUserById(ticketGranted.getUserid()).join();
                    addTimerPermissionRemoval(textChannel, user, ticketGranted, ticketGranted.getEndGrant());
                });
            });
        });
    }
    
    public int givePermission(TTicketChannelEntity ticketChannel, ServerTextChannel textChannel, User user) {
        // On tente l'ajout de permission sur le salon
        final int resWritePerm = addWritePermission(textChannel, user);
        // On obtient 0 en cas de succès
        if (resWritePerm == 0) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, (int) ticketChannel.getGrantTime());
            final Date endGrantTime = calendar.getTime();
            final TTicketGrantedEntity ticketGranted = ticketService.saveGranted(
                    new TTicketGrantedEntity(ticketChannel.getServer(), ticketChannel.getChannel(), user.getId(), new java.sql.Date(endGrantTime.getTime())));
            addTimerPermissionRemoval(textChannel, user, ticketGranted, endGrantTime);
        }
        return resWritePerm;
    }
    
    public void addTimerPermissionRemoval(ServerTextChannel textChannel, User user, TTicketGrantedEntity ticketGranted, Date time) {
        timer.schedule(new RemovePermission(textChannel, user, ticketGranted, this), time);
    }
    
    public void removeGrant(TTicketGrantedEntity ticketGranted){
        ticketService.removeGranted(ticketGranted);
    }
    
    public int addWritePermission(ServerTextChannel textChannel, User user) {
        if (textChannel.canWrite(user)) {
            // Pas besoin de perm : :o:
            return 3;
        }
        // Si l'utilisateur n'a pas le droit de prendre de ticket, on renvoie 1
        if (!ticketService.canUse(textChannel, user.getRoles(textChannel.getServer()).stream().map(Role::getId).collect(Collectors.toList())))
            // Pas les perms :no_entry_sign:
            return 1;
        try {
            ServerTextChannelUpdater updater = new ServerTextChannelUpdater(textChannel);
            updater.addPermissionOverwrite(user, new PermissionsBuilder().setAllowed(new PermissionType[]{PermissionType.SEND_MESSAGES}).build());
            updater.update().join();
        } catch (CompletionException e) {
            log.info("Could not set write permission for "+user.getName()+" on channel "+textChannel.getId()
                    +" server "+textChannel.getServer().getName()+" ("+textChannel.getServer().getId()+").");
            e.printStackTrace();
            // Erreur : :x:
            return 2;
        }
        // Succès : :white_mark_check:
        return 0;
    }
    
    public int removeWritePermission(ServerTextChannel textChannel, User user) {
        try {
            ServerTextChannelUpdater updater = new ServerTextChannelUpdater(textChannel);
            updater.addPermissionOverwrite(user, new PermissionsBuilder().setUnset(new PermissionType[]{PermissionType.SEND_MESSAGES}).build());
            updater.update().join();
        } catch (CompletionException e) {
            log.info("Could not unset write permission for "+user.getName()+" on channel "+textChannel.getId()
                    +" server "+textChannel.getServer().getName()+" ("+textChannel.getServer().getId()+").");
            e.printStackTrace();
            // Erreur : :x:
            return 1;
        }
        // Succès : :white_mark_check:
        return 0;
    }
    
    private static class RemovePermission extends TimerTask {
        
        private final ServerTextChannel textChannel;
        private final User user;
        private final TTicketGrantedEntity ticketGranted;
        private final TicketsLogicPermission ticketsLogicPermission;
        
        public RemovePermission(ServerTextChannel textChannel, User user, TTicketGrantedEntity ticketGranted, TicketsLogicPermission ticketsLogicPermission) {
            this.textChannel = textChannel;
            this.user = user;
            this.ticketGranted = ticketGranted;
            this.ticketsLogicPermission = ticketsLogicPermission;
        }
        
        @Override
        public void run() {
            if(ticketsLogicPermission.removeWritePermission(textChannel, user) == 0){
                ticketsLogicPermission.removeGrant(ticketGranted);
            }
        }
    }
}
