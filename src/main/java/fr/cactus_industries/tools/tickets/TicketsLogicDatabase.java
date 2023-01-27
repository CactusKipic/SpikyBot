package fr.cactus_industries.tools.tickets;

import fr.cactus_industries.database.interaction.service.TicketService;
import fr.cactus_industries.database.schema.table.TTicketChannelEntity;
import fr.cactus_industries.tools.PremiumServers;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Service
public class TicketsLogicDatabase {
    
    public static final Long GRANT_TIME = 30L;
    public static final long GRANT_LEVEL = 0L;
    private final TicketService ticketService;
    private final TicketsLogicMessage ticketsLogicMessage;
    private final TicketsMessageListener ticketsMessageListener;
    private final TicketsButtonListener ticketsButtonListener;
    private final DiscordApi api;
    
    public TicketsLogicDatabase(TicketService ticketService, TicketsLogicMessage ticketsLogicMessage, TicketsMessageListener ticketsMessageListener,
                                TicketsButtonListener ticketsButtonListener, DiscordApi api) {
        this.ticketService = ticketService;
        this.ticketsLogicMessage = ticketsLogicMessage;
        this.ticketsMessageListener = ticketsMessageListener;
        this.ticketsButtonListener = ticketsButtonListener;
        this.api = api;
    }
    
    // Ajout des listeners
    @PostConstruct
    public void init(){
        // Ajout du listener sur tous les salons enregistrés en base
        ticketService.findAllChannel().parallelStream().collect(Collectors.groupingBy(TTicketChannelEntity::getServer))
                .forEach((serverId, listTicketChannel) -> {
                    final Server server = api.getServerById(serverId).orElse(null);
                    if(server == null){ // Serveur pas trouvé (inexistant ou erreur API ?)
                        System.out.println("Le serveur ID:"+serverId+" n'a pas pu être trouvé, existe-t-il encore ?");
                    } else {
                        listTicketChannel.forEach(TC -> {
                            final ServerTextChannel textChannel = server.getTextChannelById(TC.getChannel()).orElse(null);
                            if(textChannel == null) { // Salon pas trouvé (supprimé ou erreur API ?)
                                System.out.println("Le salon "+TC.getChannel()+" du serveur "+server.getName() +" ("+serverId+") n'a pas été trouvé.");
                                // TODO Supprimer ?
                            } else {
                                addListeners(textChannel);
                            }
                        });
                    }
                });
    }
    
    public void addListeners(ServerTextChannel textChannel) {
        textChannel.addButtonClickListener(ticketsButtonListener);
        textChannel.addMessageCreateListener(ticketsMessageListener);
    }
    
    public void removeListeners(ServerTextChannel textChannel) {
        textChannel.removeListener(ButtonClickListener.class, ticketsButtonListener);
        textChannel.removeListener(MessageCreateListener.class, ticketsMessageListener);
    }
    
    public String addChannel(ServerTextChannel textChannel) {
        // Limite de 5 tickets par serveur non premium
        if (ticketService.findAllServerChannel(textChannel.getServer().getId()).size() >= 5 && !PremiumServers.isServerPremium(textChannel.getServer())) {
            return "You've reach the limit of 5 tickets per server. Premium servers are exempt of this limit.";
        } else {
            if (ticketService.findChannel(textChannel).isPresent())
                return "This channel already have a ticket.";
            else {
                final TTicketChannelEntity ticketChannel = new TTicketChannelEntity(textChannel.getServer().getId(), textChannel.getId(),
                        null, new MessageJsonTicket().init(), GRANT_TIME, GRANT_LEVEL);
                ticketService.saveChannel(ticketChannel);
                ticketsLogicMessage.resendTicket(textChannel);
                addListeners(textChannel);
                return "Ticket successfully added to the channel.";
            }
        }
    }
    
    public String removeChannel(ServerTextChannel textChannel) {
        final TTicketChannelEntity ticketChannel = ticketService.findChannel(textChannel).orElse(null);
        if(ticketChannel == null){
            return "There is no ticket on this channel.";
        } else {
            // On retire les listeners du salon
            removeListeners(textChannel);
            // On supprime le ticket
            ticketsLogicMessage.deleteTicket(textChannel, ticketChannel);
            // On supprime de la BDD le ticket
            ticketService.removeChannel(ticketChannel);
            return "Ticket on the channel has been deleted.";
        }
    }
    
    public String setGrantTime(ServerTextChannel textChannel, Long time) {
        final TTicketChannelEntity ticketChannel = ticketService.findChannel(textChannel).orElse(null);
        if(ticketChannel == null){
            return "There is no ticket on this channel.";
        } else {
            if(time == null){
                time = GRANT_TIME;
            } 
            if(time > 120 && !PremiumServers.isServerPremium(textChannel.getServer())) {
                return "Grant time on ticket is limited to 120 seconds for non-premium servers.";
            }
            if(time < 15) {
                return "Minimal grant time is 15 seconds.";
            }
            ticketChannel.setGrantTime(time);
            ticketService.saveChannel(ticketChannel);
            return "Ticket's grant time successfully set to " + time + " seconds.";
        }
    }
    
    public String setGrantLevel(ServerTextChannel textChannel, Long level) {
        final TTicketChannelEntity ticketChannel = ticketService.findChannel(textChannel).orElse(null);
        if(ticketChannel == null){
            return "There is no ticket on this channel.";
        } else {
            if(level == null){
                ticketChannel.setGrantLevel(GRANT_LEVEL);
            } else {
                if(level > 1 && !PremiumServers.isServerPremium(textChannel.getServer())) {
                    return "Required level on ticket is limited to 1 for non-premium servers.";
                }
                ticketChannel.setGrantLevel(level);
            }
            ticketService.saveChannel(ticketChannel);
            return "Ticket's required level successfully set to " + (level == null ? "default." : level + ".");
        }
    }
}
