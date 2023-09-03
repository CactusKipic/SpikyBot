package fr.cactus_industries.listeners;

import java.net.URL;
import java.util.List;
import fr.cactus_industries.tools.ReactionStruct;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;
import fr.cactus_industries.tools.Tisstober;
import java.io.IOException;
import fr.cactus_industries.tools.pdfreading.PDFReading;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.Message;
import java.util.Date;
import fr.cactus_industries.tools.ConfigSpiky;
import java.util.ArrayList;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import java.util.HashMap;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.springframework.stereotype.Service;

@Slf4j
public class ReactionManager {
    @Service
    public static class ReactionAdded implements ReactionAddListener {
        
        private static final HashMap<Long, Long> PDFCooldown = new HashMap<>();
        private static int PDFCooldownClean = 0;
        
        public void onReactionAdd(final ReactionAddEvent event) {
            
            final long chID = event.getChannel().getId();
            
            if (!event.requestUser().join().isBot()) {
                if (ConfigSpiky.getConfigObj("PDF.chanList", ArrayList.class).contains(chID)) {
                    if (event.requestReaction().join().get().getEmoji().equalsEmoji("\ud83e\udd16")) {
                        event.removeReaction();
                        final long date = new Date().getTime();
                        if (ReactionAdded.PDFCooldown.containsKey(event.getUserId()) && ReactionAdded.PDFCooldown.get(event.getUserId()) > date) {
                            return;
                        }
                        ++ReactionAdded.PDFCooldownClean;
                        if (ReactionAdded.PDFCooldownClean >= 20) {
                            ReactionAdded.PDFCooldown.entrySet().forEach(entry -> {
                                if (entry.getValue() < date) {
                                    ReactionAdded.PDFCooldown.remove(entry.getKey());
                                }
                                return;
                            });
                            ReactionAdded.PDFCooldownClean = 0;
                        }
                        ReactionAdded.PDFCooldown.put(event.getUserId(), date + 30000L);
                        final List<MessageAttachment> MAL = event.requestMessage().join().getAttachments();
                        if (MAL.size() > 0) {
                            final URL fileUrl = MAL.get(0).getUrl();
                            System.out.println((MAL.get(0)).getUrl().getPath() + "\nFile: " + (MAL.get(0)).getUrl().getFile());
                            if (fileUrl.getPath().toLowerCase().endsWith(".pdf")) {
                                try {
                                    PDFReading.sendPDFTextTo(fileUrl.openStream(), event.requestUser().join());
                                    log.info("Doc translation sent !");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                else if (chID == (Long) Tisstober.getConfig().get("chanID")) {
                    log.info("Reaction Tisstober");
                    if (!event.getUser().get().isBot() && event.requestReaction().join().get().getEmoji().equalsEmoji("\ud83d\udc96")) {
                        Message message = null;
                        try {
                            message = event.requestMessage().get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        final Calendar mesCal = GregorianCalendar.from(message.getCreationTimestamp().atZone(ZoneId.systemDefault()));
                        int day = mesCal.get(Calendar.DAY_OF_MONTH);
                        final Calendar limitCal = (Calendar)mesCal.clone();
                        final String[] str = Tisstober.getConfigString("hour").split(":");
                        limitCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str[0]));
                        limitCal.set(Calendar.MINUTE, Integer.parseInt(str[1]));
                        if (mesCal.compareTo(limitCal) <= 0) {
                            --day;
                        }
                        final long uID = event.getUser().get().getId();
                        final Long mes = ReactionStruct.getMessageReaction(uID, day);
                        if (mes != null) {
                            try {
                                final Message oldMessage = event.getChannel().getMessageById(mes).get();
                                oldMessage.getReactions().get(0).removeUser(event.getUser().get());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        ReactionStruct.setReactionMessage(uID, event.getMessageId(), day);
                    }
                }
            }
        }
    }
}
