package fr.cactus_industries.listeners;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MessageListener implements MessageCreateListener {
    
    private final String prefix;
    
    public MessageListener(String prefix){
        this.prefix = prefix;
    }
    
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        
        if(event.getMessage().getContent().startsWith(prefix)){
        
        } else
        if(event.getMessage().getContent().matches("(?i).*spikybot est le meilleur.*")){
            event.getChannel().sendMessage("Vous avez complètement raison mon cher ! :wink:");
        }
        
    }
}
