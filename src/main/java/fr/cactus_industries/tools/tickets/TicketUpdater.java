package fr.cactus_industries.tools.tickets;

import fr.cactus_industries.tools.PremiumServers;
import fr.cactus_industries.tools.messagesaving.MessageJsonTool;

import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import fr.cactus_industries.tools.permissionslevels.PermissionsLevelsHandler;
import fr.cactus_industries.tools.permissionslevels.SBPermissionType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.channel.ServerTextChannel;
import fr.cactus_industries.tools.SBToolbox;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

public class TicketUpdater {
    
    public static void handleCommand(MessageCreateEvent event, String[] args) {
        if (args.length < 3) {
            event.getChannel().sendMessage("Updateticket usage:\nupdateticket <mess, type, title, desc, color, image, resend> <channel> [value, messageID, empty to delete option]\nupdateticket <granttime, grantlevel, rolelevel> <channel/role> [time value, level/remove]");
            return;
        }
        System.out.println(args[1]);
        ServerTextChannel textChannel;
        Long chanID;
        MessageJsonTool msg;
        int level;
        switch (args[1].toLowerCase()) {
            case "resend":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                forceUpdateTicket(textChannel);
                event.getChannel().sendMessage("Ticket message updated.");
                break;
            case "granttime":
                if (args.length < 4) {
                    break;
                }
                chanID = SBToolbox.getChannelID(args[2]);
                if (chanID == null) {
                    event.getChannel().sendMessage("Invalid channel (Is the bot able to see this channel ?)");
                    return;
                }
                int time;
                try {
                    time = Integer.parseInt(args[3]);
                    if (time < 0) {
                        event.getChannel().sendMessage("Time can't be negative, madlad.");
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    event.getChannel().sendMessage("Invalid time number.");
                    return;
                }
                if (time > 30 && !PremiumServers.isServerPremium(event.getServer().get())) {
                    event.getChannel().sendMessage("Only premium servers can setup grant time over 30 seconds.");
                    return;
                }
                if (ChannelsTicketHandler.setGrantTimeOnChannel(event.getServer().get().getTextChannelById(chanID).get(), time)) {
                    event.getChannel().sendMessage("Grant time updated successfully !");
                } else {
                    event.getChannel().sendMessage("Error, could not update grant time. (Is channel ID valid ?)");
                }
                break;
            case "grantlevel":
                if (args.length < 4) {
                    break;
                }
                chanID = SBToolbox.getChannelID(args[2]);
                if (chanID == null) {
                    event.getChannel().sendMessage("Invalid channel (Is the bot able to see this channel ?)");
                    return;
                }
                try {
                    level = Integer.parseInt(args[3]);
                    if (level < 0) {
                        event.getChannel().sendMessage("Level can't be negative.");
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    event.getChannel().sendMessage("Invalid level number.");
                    return;
                }
                if (level > 1 && !PremiumServers.isServerPremium(event.getServer().get())) {
                    event.getChannel().sendMessage("Only premium servers can setup grant level over 1.");
                    return;
                }
                Server server = event.getServer().get();
                if (PermissionsLevelsHandler.setPermLevelOnChannel(SBPermissionType.TicketChannel, server, server.getTextChannelById(chanID).get(), level)) {
                    event.getChannel().sendMessage("Grant level updated successfully !");
                } else {
                    event.getChannel().sendMessage("Error, could not update grant level. (Is channel ID valid ?)");
                }
                break;
            case "rolelevel":
                if (args.length < 4) {
                    event.getChannel().sendMessage("Command usage:\nupdateticket rolelevel <role> <level/remove>");
                    break;
                }
                Role role = SBToolbox.getRole(args[2], event.getServer().get());
                if (role == null) {
                    event.getChannel().sendMessage("Invalid role.");
                    return;
                }
                if(args[3].toLowerCase(Locale.ROOT).equals("remove")){
                    if(PermissionsLevelsHandler.deletePermLevelOfRole(SBPermissionType.TicketChannel, role)){
                        event.getChannel().sendMessage("Role level removed successfully !");
                    } else {
                        event.getChannel().sendMessage("Unexpected error, could not delete role level.");
                    }
                } else {
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
                    if (PermissionsLevelsHandler.setPermLevelOfRole(SBPermissionType.TicketChannel, role, level)) {
                        event.getChannel().sendMessage("Role level updated successfully !");
                    } else {
                        event.getChannel().sendMessage("Unexpected error, could not update role level.");
                    }
                }
                break;
            case "title":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                String newTitle = null;
                if (args.length >= 4 && Character.isDigit(args[3].charAt(0))) {
                    Long messID = SBToolbox.getMessageID(args[3]);
                    if (messID != null) {
                        newTitle = event.getChannel().getMessageById(messID).join().getContent();
                    } else {
                        AtomicInteger i = new AtomicInteger();
                        newTitle = Arrays.stream(args).filter(item -> i.getAndIncrement() < 4).collect(Collectors.joining(" "));
                    }
                }
                msg = ChannelsTicketHandler.getMessage(textChannel);
                if (msg == null) {
                    event.getChannel().sendMessage("Error. Does this channel have a ticket ?");
                    return;
                }
                msg.setEmbTitle(newTitle);
                if (!ChannelsTicketHandler.updateMessage(textChannel, msg)) {
                    event.getChannel().sendMessage("Unexpected error while updating ticket's embed tilte.");
                    return;
                }
                event.getChannel().sendMessage("Ticket's embed title successfully " + ((newTitle == null) ? "deleted." : "updated."));
                break;
            case "desc":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                String newDesc = null;
                if (args.length >= 4 && Character.isDigit(args[3].charAt(0))) {
                    Long messID = SBToolbox.getMessageID(args[3]);
                    if (messID != null) {
                        newDesc = event.getChannel().getMessageById(messID).join().getContent();
                    } else {
                        AtomicInteger i = new AtomicInteger();
                        newDesc = Arrays.stream(args).filter(item -> i.getAndIncrement() < 4).collect(Collectors.joining(" "));
                    }
                }
                msg = ChannelsTicketHandler.getMessage(textChannel);
                if (msg == null) {
                    event.getChannel().sendMessage("Error. Does this channel have a ticket ?");
                    return;
                }
                msg.setEmbDesc(newDesc);
                if (!ChannelsTicketHandler.updateMessage(textChannel, msg)) {
                    event.getChannel().sendMessage("Unexpected error while updating ticket's embed description.");
                    return;
                }
                event.getChannel().sendMessage("Ticket's embed description successfully " + ((newDesc == null) ? "deleted." : "updated."));
                break;
            case "mess":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                String newMess = null;
                if (args.length >= 4 && Character.isDigit(args[3].charAt(0))) {
                    Long messID = SBToolbox.getMessageID(args[3]);
                    if (messID != null) {
                        newMess = event.getChannel().getMessageById(messID).join().getContent();
                    }
                    else {
                        AtomicInteger i = new AtomicInteger();
                        newMess = Arrays.stream(args).filter(item -> i.getAndIncrement() < 4).collect(Collectors.joining(" "));
                    }
                }
                msg = ChannelsTicketHandler.getMessage(textChannel);
                if (msg == null) {
                    event.getChannel().sendMessage("Error. Does this channel have a ticket ?");
                    return;
                }
                msg.setContent(newMess);
                if (!ChannelsTicketHandler.updateMessage(textChannel, msg)) {
                    event.getChannel().sendMessage("Unexpected error while updating ticket's message.");
                    return;
                }
                event.getChannel().sendMessage("Ticket's message successfully " + ((newMess == null) ? "deleted." : "updated."));
            case "color":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                String newColor = null;
                if (args.length >= 4) {
                    newColor = args[3];
                }
                msg = ChannelsTicketHandler.getMessage(textChannel);
                if (msg == null) {
                    event.getChannel().sendMessage("Error. Does this channel have a ticket ?");
                    return;
                }
                msg.setEmbColor(newColor);
                if (!ChannelsTicketHandler.updateMessage(textChannel, msg)) {
                    event.getChannel().sendMessage("Unexpected error while updating ticket's embed color.");
                    return;
                }
                event.getChannel().sendMessage("Ticket's embed color successfully " + ((newColor == null) ? "deleted." : "updated."));
            case "type":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                Boolean embed = null;
                if (args.length >= 4) {
                    if (args[3].equalsIgnoreCase("embed"))
                        embed = true;
                    else if (args[3].equalsIgnoreCase("mess"))
                        embed = false;
                } else
                    embed = false;
                
                if (embed == null) {
                    event.getChannel().sendMessage("Valid types are: Embed or Mess");
                    return;
                }
                msg = ChannelsTicketHandler.getMessage(textChannel);
                if (msg == null) {
                    event.getChannel().sendMessage("Error. Does this channel have a ticket ?");
                    return;
                }
                msg.haveEmbed(embed);
                if (!ChannelsTicketHandler.updateMessage(textChannel, msg)) {
                    event.getChannel().sendMessage("Unexpected error while updating ticket's type.");
                    return;
                }
                event.getChannel().sendMessage("Ticket's embed successfully " + (embed ? "enabled" : "disabled"));
            case "image":
                textChannel = SBToolbox.getChannel(args[2], event.getServer().get());
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                String newImgLink = null;
                if (args.length >= 4) {
                    if (!args[3].matches("(?i)^https?://.*\\.(png|jpg|gif)$")) {
                        event.getChannel().sendMessage("The link provided is malformed or isn't an image (.png, .jpg or .gif).");
                        return;
                    }
                    newImgLink = args[3];
                }
                msg = ChannelsTicketHandler.getMessage(textChannel);
                if (msg == null) {
                    event.getChannel().sendMessage("Error. Does this channel have a ticket ?");
                    return;
                }
                msg.setEmbImage(newImgLink);
                if (!ChannelsTicketHandler.updateMessage(textChannel, msg)) {
                    event.getChannel().sendMessage("Unexpected error while updating ticket's embed image.");
                    return;
                }
                event.getChannel().sendMessage("Ticket's embed image successfully " + ((newImgLink == null) ? "deleted." : "updated."));
            default:
                event.getChannel().sendMessage("Updateticket usage:\nupdateticket <mess, type, title, desc, color, image, resend> <channel> [value, messageID, empty to delete option]\nupdateticket <granttime, grantlevel> <channel> [time value, level/remove]");
                break;
        }
        
    }
    
    public static void forceUpdateTicket(ServerTextChannel channel) {
        Long ticketMessage = ChannelsTicketHandler.getMessageID(channel);
        try {
            channel.getMessageById(ticketMessage).join().delete().join();
        } catch (CompletionException e){
            System.out.println("Old ticket wasn't found. Probably deleted by other.");
        }
        Message nMessage = ChannelsTicketHandler.getMessage(channel).create().send(channel).join();
        nMessage.addReaction("\ud83c\udf9f");
        ChannelsTicketHandler.updateMessageID(channel, nMessage.getId());
    }
    
    public static void updateTicket(ServerTextChannel channel) {
        Long ticketMessage = ChannelsTicketHandler.getMessageID(channel);
        
        if (ticketMessage != channel.getMessages(1).join().getNewestMessage().get().getId()) {
            channel.getMessageById(ticketMessage).join().delete().join();
            Message nMessage = ChannelsTicketHandler.getMessage(channel).create().send(channel).join();
            nMessage.addReaction("\ud83c\udf9f");
            ChannelsTicketHandler.updateMessageID(channel, nMessage.getId());
        }
    }
}
