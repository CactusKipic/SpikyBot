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
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

@Deprecated
public class TicketsPermissionManager extends TimerTask {
    
    private ServerTextChannel channel;
    private User user;
    private static Timer timer = new Timer();
    private static HashMap<Long, ArrayList<Long>> serverChannels;
    
    public static void init(DiscordApi api) {
        HashMap<Long, HashMap<Long, HashMap<Long, Long>>> grantedUsers = ChannelsTicketHandler.getAllGrantedUsers();
        if(grantedUsers == null) {
            System.out.println("Error while getting user still granted from DB.");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 2);
            Date dateLimit = calendar.getTime();
            // TODO Déplacer ça
            // Retrait des permissions pour les utilisateurs qui étaient encore autorisé lors de l'extinction du bot impromptue
            grantedUsers.forEach((serverID, channelMap) -> {
                Server server;
                try {
                    server = api.getServerById(serverID).get();
                    channelMap.forEach((channelID, userMap) -> {
                        ServerTextChannel textChannel;
                        try {
                            textChannel = server.getTextChannelById(channelID).get();
                            userMap.forEach((userID, endGrant) -> {
                                try {
                                    Date date = new Date(endGrant);
                                    User user = api.getUserById(userID).join();
                                    if(date.before(dateLimit)) { // On retire les permissions si la date de fin est passée (+2 min)
                                        removeWritePerm(textChannel, user);
                                        ChannelsTicketHandler.userNoLongerGranted(user, textChannel);
                                    } else // Sinon on résume l'autorisation
                                        resumeGrantPermission(textChannel, user, date);
                                    
                                    // removeWritePerm(textChannel, api.getUserById(userID).join()); // Old code
                                } catch (NoSuchElementException ex) {
                                    System.out.println("Error while removing rank, can't find user with id " + userID + " in channel with id " + channelID
                                            + " in server with id " + serverID);
                                } catch (Exception ex2) {
                                    System.out.println("Bigger error while removing rank. Probably permission error.");
                                    ex2.printStackTrace();
                                }
                            });
                        } catch (NoSuchElementException ex) {
                            System.out.println("Error while removing rank, can't find channel with id " + channelID
                                    + " in server with id " + serverID);
                        }
                    });
                } catch (NoSuchElementException ex) {
                    System.out.println("Error while removing rank, can't find server with id :" + serverID);
                }
            });
        }
        serverChannels = ChannelsTicketHandler.getAllTicketedChannel();
        // TicketsReactionHandler reactionListener = TicketsReactionHandler.getListener();
        // TicketsMessageManager messageManager = TicketsMessageManager.getInstance(); // Old
        // TicketsButtonHandler buttonHandler = TicketsButtonHandler.getInstance(); // Old
        
        for (Map.Entry<Long, ArrayList<Long>> entry : serverChannels.entrySet()) {
            System.out.println("Ajout des tickets pour "+entry.getKey());
    
            Optional<Server> serverById = api.getServerById(entry.getKey());
            if(serverById.isPresent()){
                Server server = serverById.get();
                System.out.println("Ajout des tickets pour "+server.getName() +" ("+entry.getKey()+")");
                // Ajout des salons au listener
                for (long chanID : entry.getValue()) {
                    Optional<ServerTextChannel> textChannelById = server.getTextChannelById(chanID);
                    if(textChannelById.isEmpty()){
                        System.out.println("Le salon '"+chanID+"' a été supprimé, suppression de ses données de la BDD.");
                        ChannelsTicketHandler.deleteTicketOnChannel(server.getId(), chanID);
                    } else {
                        ServerTextChannel textChannel = textChannelById.get();
                        // textChannel.addReactionAddListener(reactionListener); // Old code, Vieux listener pour les réactions // Old
    
                        // textChannel.addButtonClickListener(buttonHandler); // Listener pour les boutons des messages de tickets // Old
                        // textChannel.addMessageCreateListener(messageManager); // Listener de nouveaux messages pour redescendre le message de ticket // Old
                    }
                }
            } else {
                System.out.println("ID du serveur invalide (serveur supprimé ? Bot kick ?)");
                // TODO Suppression automatique des données de serveur introuvable ? (Peut-être pas dès la première erreur, genre 1 semaine ?)
            }
        }
    }
    
    public static void addChannel(ServerTextChannel textChannel) {
        if (!serverChannels.containsKey(textChannel.getServer().getId())) {
            serverChannels.put(textChannel.getServer().getId(), new ArrayList<>());
        }
        serverChannels.get(textChannel.getServer().getId()).add(textChannel.getId());
        textChannel.addReactionAddListener(TicketsReactionHandler.getListener()); // Old code, vieux listener pour les réactions
        // Ajout des listeners pour le bouton et les nouveaux messages
        // textChannel.addButtonClickListener(TicketsButtonHandler.getInstance()); // Old
        // textChannel.addMessageCreateListener(TicketsMessageManager.getInstance()); // Old
    }
    
    public static void removeChannel(ServerTextChannel textChannel) {
        serverChannels.get(textChannel.getServer().getId()).remove(textChannel.getId());
        textChannel.removeListener(ReactionAddListener.class, TicketsReactionHandler.getListener()); // Old listener
        // Retrait des listeners pour le bouton et les nouveaux messages
        // textChannel.removeListener(MessageCreateListener.class, TicketsMessageManager.getInstance()); // Old
        // textChannel.removeListener(ButtonClickListener.class, TicketsButtonHandler.getInstance()); // Old
    }
    
    public static boolean grantTemporaryPermission(ServerTextChannel channel, User user) {
        if (ChannelsTicketHandler.doesUserAlreadyGranted(channel, user)) {
            return false;
        }
        Server server = channel.getServer();
        // Vérifie si le ticket est public, si non, regarde si l'utilisateur est administrateur, si non, regarde si l'utilisateur a un niveau d'accès suffisant pour ce salon
        if (!PermissionsLevelsHandler.doesUserHavePermissionsOnChannel(SBPermissionType.TicketChannel, server, channel, user)) {
            System.out.println("User " + user.getName() + " hasn't level required for ticket.");
            return false;
        }
        ServerTextChannelUpdater updater = new ServerTextChannelUpdater(channel);
        // Ajout de l'utilisateur dans la liste des personnes déjà "autorisés" avant de donner les droits
        Integer grantTimeOnChannel = ChannelsTicketHandler.getGrantTimeOnChannel(channel);
        if(ChannelsTicketHandler.userGranted(channel, user, grantTimeOnChannel)) {
            try {
                updater.addPermissionOverwrite(user, new PermissionsBuilder().setAllowed(PermissionType.SEND_MESSAGES).build());
                updater.update().join();
            } catch (CompletionException e){
                System.out.println("Error while updating permissions of a user for ticket.");
                e.printStackTrace();
                ChannelsTicketHandler.userNoLongerGranted(user, channel); // On le retire de la liste si erreur
                if(!server.isAdmin(user))
                    return false;
                System.out.println("User is admin, error probably normal.");
            }
            timer.schedule(new TicketsPermissionManager(channel, user), grantTimeOnChannel * 1000);
        } else {
            System.out.println("Something went wrong while adding the little people to the granted list :'(\n*sad cactus noises*");
            return false;
        }
        return true;
    }
    
    public static void resumeGrantPermission(ServerTextChannel channel, User user, Date endTime) {
        timer.schedule(new TicketsPermissionManager(channel, user), endTime);
    }
    
    private TicketsPermissionManager(ServerTextChannel channel, User user) {
        this.channel = channel;
        this.user = user;
    }
    
    @Override
    public void run() {
        System.out.println("Retrait permission bouton");
        try {
            removeWritePerm(this.channel, this.user);
        } catch (Exception e){
            System.out.println("Erreur lors du retrait de permissions ou rafraichissement du ticket.");
        }
        // On retire l'utilisateur de la liste des "autorisés"
        ChannelsTicketHandler.userNoLongerGranted(this.user, this.channel);
        TicketUpdater.updateTicket(this.channel);
    }
    
    private static void removeWritePerm(ServerTextChannel textChannel, User user){
        ServerTextChannelUpdater updater = new ServerTextChannelUpdater(textChannel);
        updater.addPermissionOverwrite(user, new PermissionsBuilder().setUnset(new PermissionType[]{PermissionType.SEND_MESSAGES}).build());
        updater.update().join();
    }
}
