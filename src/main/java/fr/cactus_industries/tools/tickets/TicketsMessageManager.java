package fr.cactus_industries.tools.tickets;

import java.util.*;
import java.util.concurrent.CompletionException;

import fr.cactus_industries.tools.permissionslevels.PermissionsLevelsHandler;
import fr.cactus_industries.tools.permissionslevels.SBPermissionType;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerTextChannelUpdater;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

public class TicketsMessageManager extends TimerTask {
    
    private ServerTextChannel channel;
    private User user;
    private static Timer timer = new Timer();
    private static HashMap<Long, ArrayList<Long>> serverChannels;
    
    public static void init(DiscordApi api) {
        HashMap<Long, HashMap<Long, ArrayList<Long>>> grantedUsers = ChannelsTicketHandler.getAllGrantedUsers();
        if(grantedUsers == null) {
            System.out.println("Error while getting user still granted from DB.");
        } else {
            // Retrait des permissions pour les utilisateurs qui étaient encore autorisé lors de l'extinction du bot impromptue
            grantedUsers.entrySet().forEach(e -> {
                Server s;
                try {
                    s = api.getServerById(e.getKey()).get();
                    e.getValue().entrySet().forEach(e2 -> {
                        ServerTextChannel textChannel;
                        try {
                            textChannel = s.getTextChannelById(e2.getKey()).get();
                            e2.getValue().forEach(u -> {
                                try {
                                    removeWritePerm(textChannel, api.getUserById(u).join());
                                } catch (NoSuchElementException ex) {
                                    System.out.println("Error while removing rank, can't find user with id " + u + " in channel with id " + e2.getKey()
                                            + " in server with id " + e.getKey());
                                } catch (Exception ex2) {
                                    System.out.println("Bigger error while removing rank. Probably permission error.");
                                    ex2.printStackTrace();
                                }
                            });
                        } catch (NoSuchElementException ex) {
                            System.out.println("Error while removing rank, can't find channel with id " + e2.getKey()
                                    + " in server with id " + e.getKey());
                        }
                    });
                } catch (NoSuchElementException ex) {
                    System.out.println("Error while removing rank, can't find server with id :" + e.getKey());
                }
            });
        }
        serverChannels = ChannelsTicketHandler.getAllTicketedChannel();
        TicketsReactionHandler reactionListener = TicketsReactionHandler.getListener();
        
        for (Map.Entry<Long, ArrayList<Long>> entry : serverChannels.entrySet()) {
            Server server = api.getServerById(entry.getKey().longValue()).get();
            // Ajout des salons au listener
            for (long chanID : entry.getValue()) {
                (server.getTextChannelById(chanID).get()).addReactionAddListener(reactionListener);
            }
        }
    }
    
    public static void addChannel(ServerTextChannel textChannel) {
        if (!serverChannels.containsKey(textChannel.getServer().getId())) {
            serverChannels.put(textChannel.getServer().getId(), new ArrayList<>());
        }
        serverChannels.get(textChannel.getServer().getId()).add(textChannel.getId());
        textChannel.addReactionAddListener(TicketsReactionHandler.getListener());
    }
    
    public static void removeChannel(ServerTextChannel textChannel) {
        serverChannels.get(textChannel.getServer().getId()).remove(textChannel.getId());
        textChannel.removeListener(ReactionAddListener.class, TicketsReactionHandler.getListener());
    }
    
    public static void grantTemporaryPermission(ServerTextChannel channel, User user) {
        if (ChannelsTicketHandler.doesUserAlreadyGranted(user)) {
            return;
        }
        Server server = channel.getServer();
        // Vérifie si le ticket est public, si non, regarde si l'utilisateur est administrateur, si non, regarde si l'utilisateur a un niveau d'accès suffisant pour ce salon
        if (!PermissionsLevelsHandler.doesUserHavePermissionsOnChannel(SBPermissionType.TicketChannel, server, channel, user)) {
            System.out.println("User " + user.getName() + " hasn't level required for ticket.");
            return;
        }
        ServerTextChannelUpdater updater = new ServerTextChannelUpdater(channel);
        // Ajout de l'utilisateur dans la liste des personnes déjà "autorisés" avant de donner les droits
        if(ChannelsTicketHandler.userGranted(channel, user)) {
            try {
                updater.addPermissionOverwrite(user, new PermissionsBuilder().setAllowed(PermissionType.SEND_MESSAGES).build());
                updater.update().join();
            } catch (CompletionException e){
                System.out.println("Error while updating permissions of a user for ticket.");
                e.printStackTrace();
                ChannelsTicketHandler.userNoLongerGranted(user); // On le retire de la liste si erreur
                if(!server.isAdmin(user))
                    return;
                System.out.println("User is admin, error probably normal.");
            }
            timer.schedule(new TicketsMessageManager(channel, user), ChannelsTicketHandler.getGrantTimeOnChannel(channel) * 1000);
        } else {
            System.out.println("Something went wrong while adding the little people to the granted list :'(\n*sad cactus noises*");
        }
    }
    
    private TicketsMessageManager(ServerTextChannel channel, User user) {
        this.channel = channel;
        this.user = user;
    }
    
    @Override
    public void run() {
        try {
            removeWritePerm(this.channel, this.user);
        } catch (Exception e){
            System.out.println("Erreur lors du retrait de permissions ou rafraichissement du ticket.");
        }
        TicketUpdater.updateTicket(this.channel);
        // On retire l'utilisateur de la liste des "autorisés"
        ChannelsTicketHandler.userNoLongerGranted(this.user);
    }
    
    private static void removeWritePerm(ServerTextChannel textChannel, User user){
        ServerTextChannelUpdater updater = new ServerTextChannelUpdater(textChannel);
        updater.addPermissionOverwrite(user, new PermissionsBuilder().setUnset(new PermissionType[]{PermissionType.SEND_MESSAGES}).build());
        updater.update().join();
    }
}
