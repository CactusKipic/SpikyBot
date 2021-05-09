package fr.cactus_industries.tools.pdfreading;

import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.net.URL;
import java.util.List;
import java.util.Locale;

public class PDFMessageListener implements MessageCreateListener {
    
    
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        List<MessageAttachment> MAL = event.getMessageAttachments();
        
        if(MAL.size() > 0){
            URL url = MAL.get(0).getUrl();
            if(url.getPath().toLowerCase(Locale.ROOT).endsWith(".pdf")){
                event.addReactionsToMessage("\uD83E\uDD16");
            }
        }
        
    }
}
