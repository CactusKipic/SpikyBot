package fr.cactus_industries.tools.pdfreading;

import fr.cactus_industries.tools.PremiumServers;
import fr.cactus_industries.tools.SBToolbox;
import fr.cactus_industries.tools.permissionslevels.PermissionsLevelsHandler;
import fr.cactus_industries.tools.permissionslevels.SBPermissionType;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.net.URL;
import java.util.*;

public class PDFCommandHandler {
    
    private static PDFMessageListener messageListener = new PDFMessageListener();
    private static PDFReactionListener reactionListener = new PDFReactionListener();
    
    public static void handleCommand(MessageCreateEvent event, String[] args) {
        if (args.length < 3) {
            event.getChannel().sendMessage("PDFReading usage:\npdfreading <add, remove, addreaction> <channel> [nb days]\nupdateticket <grantlevel, rolelevel> <channel, role> [level, level/remove]");
            return;
        }
        Server server = event.getServer().get();
        ServerTextChannel channel;
        switch (args[1].toLowerCase()) {
            case "add": {
                if (args.length < 4) {
                    event.getChannel().sendMessage("Command usage:\npdfreading add <channel>");
                    return;
                }
                channel = SBToolbox.getChannel(args[2], server);
                if (channel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                if (PDFDB.addPDFReadingToChannel(channel)) {
                    channel.addReactionAddListener(reactionListener);
                    channel.addMessageCreateListener(messageListener);
                } else {
                    event.getChannel().sendMessage("An unexpected error occurred. Try again later or warn the bot owner.");
                    return;
                }
                break;
            }
            case "remove": {
                if (args.length < 4) {
                    event.getChannel().sendMessage("Command usage:\npdfreading remove <channel>");
                    return;
                }
                channel = SBToolbox.getChannel(args[2], server);
                if (channel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                if (PDFDB.deletePDFReadingToChannel(channel)) {
                    channel.removeListener(PDFReactionListener.class, reactionListener);
                    channel.removeListener(PDFMessageListener.class, messageListener);
                } else {
                    event.getChannel().sendMessage("An unexpected error occurred. Try again later or warn the bot owner.");
                    return;
                }
                break;
            }
            case "addreaction":{
                if(args.length < 5){
                    event.getChannel().sendMessage("Command usage:\npdfreading addreaction <channel> <nb days>");
                    return;
                }
                channel = SBToolbox.getChannel(args[2], server);
                if(channel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                int nb;
                try {
                    nb = Integer.parseInt(args[3]);
                } catch (NumberFormatException e){
                    event.getChannel().sendMessage(args[3] + " is not a valid number.");
                    return;
                }
                if(nb < 0) {
                    event.getChannel().sendMessage("SpikyBot cannot look into the future (Or I'd already be rich !).");
                    return;
                }
                if(nb == 0){
                    if(!PremiumServers.isServerPremium(server)){
                        event.getChannel().sendMessage("Only premium servers can add reaction under PDF for all anterior messages.");
                        return;
                    }
                    channel.getMessagesAsStream().forEach(PDFCommandHandler::addReactionOnStream);
                    return;
                }
                
                if(nb > 30)
                    if (!PremiumServers.isServerPremium(server)) {
                        event.getChannel().sendMessage("Number of days for adding reactions on PDF is limited to 30 for non-Premium servers.");
                        return;
                    }
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -nb);
                long dateFrom = cal.getTimeInMillis();
                channel.getMessagesAfterAsStream(dateFrom).forEach(PDFCommandHandler::addReactionOnStream);
                break;
            }
            case "grantlevel":{
                if(args.length < 5){
                    event.getChannel().sendMessage("Command usage:\npdfreading grantlevel <channel> <level>");
                    return;
                }
                channel = SBToolbox.getChannel(args[2], server);
                if(channel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                int level;
                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException e){
                    event.getChannel().sendMessage(args[3] + " is not a valid number.");
                    return;
                }
                if(level < 0){
                    event.getChannel().sendMessage("Level can't be negative.");
                    return;
                }
                if (level > 1 && !PremiumServers.isServerPremium(event.getServer().get())) {
                    event.getChannel().sendMessage("Only premium servers can setup grant level over 1.");
                    return;
                }
                PDFDB.setGrantLevelOnChannel(channel, level);
                break;
            }
            case "rolelevel":{
                if (args.length < 5) {
                    event.getChannel().sendMessage("Command usage:\npdfreading rolelevel <role> <level/remove>");
                    break;
                }
                Role role = SBToolbox.getRole(args[2], event.getServer().get());
                if (role == null) {
                    event.getChannel().sendMessage("Invalid role.");
                    return;
                }
                if(args[3].toLowerCase(Locale.ROOT).equals("remove")){
                    if(PermissionsLevelsHandler.deletePermLevelOfRole(SBPermissionType.PDFReading, role)){
                        event.getChannel().sendMessage("Role level removed successfully !");
                    } else {
                        event.getChannel().sendMessage("Unexpected error, could not delete role level.");
                    }
                } else {
                    int level;
                    try {
                        level = Integer.parseInt(args[3]);
                        if (level < 0) {
                            event.getChannel().sendMessage("Level can't be negative.");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage("Invalid level number.");
                        return;
                    }
                    if (level > 1 && !PremiumServers.isServerPremium(event.getServer().get())) {
                        event.getChannel().sendMessage("Only premium servers can setup role level over 1.");
                        return;
                    }
                    if (PermissionsLevelsHandler.setPermLevelOfRole(SBPermissionType.PDFReading, role, level)) {
                        event.getChannel().sendMessage("Role level updated successfully !");
                    } else {
                        event.getChannel().sendMessage("Unexpected error, could not update role level.");
                    }
                }
                break;
            }
            default:
                event.getChannel().sendMessage("PDFReading usage:\npdfreading <add, remove, addreaction> <channel> [nb days]\nupdateticket <grantlevel, rolelevel> <channel, role> [level, level/remove]");
            break;
        }
        
    }
    
    public static void init(DiscordApi api){
        PDFDB.getAllPDFReadingOnChannel().forEach((key, val) -> {
            Server server = api.getServerById(key).get();
            val.forEach(c ->{
                ServerTextChannel channel = server.getTextChannelById(c).get();
                channel.addReactionAddListener(reactionListener);
                channel.addMessageCreateListener(messageListener);
            });
        });
        
        
    }
    
    private static void addReactionOnStream(Message m) {
        List<MessageAttachment> MAL = m.getAttachments();
        if (MAL.size() > 0) {
            URL url = MAL.get(0).getUrl();
            if (url.getPath().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                m.addReaction("\uD83E\uDD16");
            }
        }
    }
}
