package fr.cactus_industries.tools.pdfreading;

import fr.cactus_industries.tools.permissionslevels.PermissionsLevelsHandler;
import fr.cactus_industries.tools.permissionslevels.SBPermissionType;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PDFReactionListener implements ReactionAddListener {
    
    private final static HashMap<Long, Long> cooldownMap = new HashMap<>(); // ID User | Time
    public static int clearCounter = 0;
    
    @Override
    public void onReactionAdd(ReactionAddEvent event) {
        User user = event.requestUser().join();
        if(user.isBot())
            return;
        
        if(event.requestReaction().join().get().getEmoji().equalsEmoji("\uD83E\uDD16")){
            event.removeReaction();
            
            long date = new Date().getTime();
            if(cooldownMap.containsKey(event.getUserId()) && cooldownMap.get(event.getUserId()) >= date){
                return; // Cooldown for user not finished
            } else {
                List<MessageAttachment> MAL = event.requestMessage().join().getAttachments();
                if(MAL.size() > 0){
                    if(clearCounter < 20){ // Tous les 20 réactions robots sur un message avec une pièce jointe
                        clearCounter++;
                    } else {
                        clearCounter = 0;
                        cooldownMap.forEach((key, value) -> {
                            if (value <= date)
                                cooldownMap.remove(key);
                        });
                    }
                    
                    URL PDFUrl = MAL.get(0).getUrl();
                    if(PDFUrl.getPath().toLowerCase(Locale.ROOT).endsWith(".pdf")){
                        if(PermissionsLevelsHandler.doesUserHavePermissionsOnChannel(SBPermissionType.PDFReading, event.getServerTextChannel().get(), user)) {
                            cooldownMap.put(event.getUserId(), date + 30_000);
                            try {
                                PDFReading.sendPDFTextTo(PDFUrl.openStream(), user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            System.out.println("User doesn't have permission to read PDF.");
                    }
                }
            }
            
        }
        
    }
}
