package fr.cactus_industries.tools.pdfreading;

import fr.cactus_industries.database.interaction.service.PDFReadingService;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PDFReactionListener implements ReactionAddListener {
    
    private final PDFReadingService pdfReadingService;
    
    private static final HashMap<Long, Long> cooldownMap = new HashMap<>(); // ID User | Time
    public static int clearCounter = 0;
    private static PDFReactionListener listener = null;
    
    public PDFReactionListener(PDFReadingService pdfReadingService) {
        this.pdfReadingService = pdfReadingService;
        listener = this;
    }
    
    public static PDFReactionListener getInstance() {
        return listener;
    }
    
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
                        //if(PermissionsLevelsHandler.doesUserHavePermissionsOnChannel(SBPermissionType.PDFReading, event.getServerTextChannel().get(), user)) {
                        if(pdfReadingService.canUse(event.getServerTextChannel().get(),
                                user.getRoles(event.getServer().get()).stream().map(Role::getId).collect(Collectors.toList()))){
                            cooldownMap.put(event.getUserId(), date + 30_000);
                            try {
                                PDFReading.sendPDFTextTo(PDFUrl.openStream(), user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            log.info("User doesn't have permission to read PDF.");
                    }
                }
            }
            
        }
        
    }
}
