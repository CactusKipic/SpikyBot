package fr.cactus_industries.tools.tickets;

import fr.cactus_industries.database.interaction.service.TicketService;
import fr.cactus_industries.database.schema.table.TTicketChannelEntity;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class TicketsButtonListener implements ButtonClickListener {
    
    private static Timer timer = new Timer();
    
    private final TicketService ticketService;
    private final TicketsLogicPermission ticketsLogicPermission;
    
    public TicketsButtonListener(TicketService ticketService, TicketsLogicPermission ticketsLogicPermission) {
        this.ticketService = ticketService;
        this.ticketsLogicPermission = ticketsLogicPermission;
    }
    
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();
        final User user = buttonInteraction.getUser();
        if (user.isBot())
            return;
        
        String resEmoji = "";
        if (buttonInteraction.getCustomId().equals("spiky:ticket")) {
            final ServerTextChannel textChannel = buttonInteraction.getChannel().get().asServerTextChannel().get();
            final TTicketChannelEntity ticketChannel = ticketService.findChannel(textChannel).get();
    
            switch (ticketsLogicPermission.givePermission(ticketChannel, textChannel, user)) {
                case 0: // La permission a été donnée
                    resEmoji = "✅";
                    break;
                case 1: // La permission a été refusée
                    resEmoji = "\uD83D\uDEAB";
                    break;
                case 2: // Il y a eu une erreur lors de l'affectation de la permission
                    resEmoji = "❌";
                    break;
                case 3: // L'utilisateur a déjà les permissions nécessaires
                    resEmoji = "⭕";
                    break;
            }
            /*
            if (TicketsPermissionManager.grantTemporaryPermission(buttonInteraction.getChannel().get().asServerTextChannel().get(), user)) {
                resEmoji = "✅";
            } else
                resEmoji = "❌";*/
        }
        buttonInteraction.acknowledge();
        Message message = buttonInteraction.getMessage();
        message.addReaction(resEmoji);
        try {
            timer.schedule(new RemoveReact(message, resEmoji), 2500);
        } catch (IllegalStateException e) {
            System.out.println("Le timer du bouton est mort, vive le timer du bouton.");
            timer = new Timer();
            timer.schedule(new RemoveReact(message, resEmoji), 2500);
        }
    }
    private static class RemoveReact extends TimerTask {
        
        public Message message;
        public String emoji;
        
        public RemoveReact(Message message, String emoji){
            this.message = message;
            this.emoji = emoji;
        }
        
        @Override
        public void run() {
            System.out.println("Retrait émoji ticket");
            message.removeOwnReactionByEmoji(emoji).join();
            System.out.println("Fait !");
        }
    }
}
