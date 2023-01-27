package fr.cactus_industries.tools.tickets;

import fr.cactus_industries.database.interaction.service.TicketService;
import fr.cactus_industries.database.schema.table.TTicketChannelEntity;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TicketsLogicMessage {
    
    private final TicketService ticketService;
    private final DiscordApi api;
    
    public TicketsLogicMessage(TicketService ticketService, DiscordApi api) {
        this.ticketService = ticketService;
        this.api = api;
    }
    
    // Renvoie un ticket et supprime l'ancien si le dernier message n'est pas le ticket actuel
    public void updateTicket(ServerTextChannel textChannel) {
        final TTicketChannelEntity ticketChannel = ticketService.findChannel(textChannel).orElse(null);
        if(ticketChannel == null) {
            System.out.println("The ticket channel was not found while resending ticket...");
            return;
        }
        final Message message = textChannel.getMessages(1).join().getNewestMessage().orElse(null);
        // S'il n'y a pas de précédent message (improbable) ou que le dernier message du salon n'est pas celui du ticket, on met à jour
        if(message == null || message.getId() != ticketChannel.getMessageId()){
            deleteOldAndSendNewTicket(textChannel, ticketChannel);
        }
    }
    
    // Renvoie un ticket et supprime l'ancien
    public void resendTicket(ServerTextChannel textChannel) {
        final TTicketChannelEntity ticketChannel = ticketService.findChannel(textChannel).orElse(null);
        if(ticketChannel == null) {
            System.out.println("The ticket channel was not found while resending ticket...");
            return;
        }
        deleteOldAndSendNewTicket(textChannel, ticketChannel);
    }
    // Envoi du nouveau message de ticket avec attente de complétion
    private void deleteOldAndSendNewTicket(ServerTextChannel textChannel, TTicketChannelEntity ticketChannel) {
        try {
            textChannel.getMessageById(ticketChannel.getMessageId()).get().delete();
        } catch (InterruptedException e) {
            System.out.println("Erreur lors de la récupération de l'actuel message de ticket... (s:"+textChannel.getServer().getName()+"|"+textChannel.getServer().getId()
                    +" c:"+textChannel.getId()+")");
            e.printStackTrace();
            return;
        } catch (NullPointerException | ExecutionException e) {
            System.out.println("Le message de ticket actuel n'a pas pu être trouvé.");
            e.printStackTrace();
        }
        ticketChannel.setMessageId(ticketChannel.getMessageJsonTicket().create(this.api).send(textChannel).join().getId());
        this.ticketService.saveChannel(ticketChannel);
    }
    
    public void deleteTicket(ServerTextChannel textChannel, TTicketChannelEntity ticketChannel) {
        try {
            textChannel.getMessageById(ticketChannel.getMessageId()).get().delete();
        } catch (InterruptedException e) {
            System.out.println("Erreur lors de la récupération de l'actuel message de ticket lors de sa suppression... (s:"+textChannel.getServer().getName()+"|"+textChannel.getServer().getId()
                    +" c:"+textChannel.getId()+")");
            e.printStackTrace();
        } catch (NullPointerException | ExecutionException e) {
            System.out.println("Le message de ticket actuel n'a pas pu être trouvé pour le supprimé.");
            e.printStackTrace();
        }
    }
}
