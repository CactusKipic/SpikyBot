package fr.cactus_industries.tools.tickets;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

@Deprecated
public class TicketsReactionHandler implements ReactionAddListener {
    
    private static TicketsReactionHandler listener = null;
    
    public static TicketsReactionHandler getListener() {
        if (listener != null) {
            return listener;
        }
        listener = new TicketsReactionHandler();
        return listener;
    }
    
    public void onReactionAdd(ReactionAddEvent event) {
        if ((event.requestUser().join()).isBot())
            return;
        if (event.requestReaction().join().get().getEmoji().equalsEmoji("\ud83c\udf9f")) {
            Message mess = event.requestMessage().join();
            if (mess.getAuthor().isYourself() && mess.getReactionByEmoji("\ud83c\udf9f").get().containsYou()) {
                event.removeReaction().join();
                System.out.println(event.getServer().get().getRoles(event.requestUser().join()));
                TicketsPermissionManager.grantTemporaryPermission(event.getChannel().asServerTextChannel().get(), event.requestUser().join());
            }
        }
    }
}

