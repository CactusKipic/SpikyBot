package fr.cactus_industries.tools.actionreaction;

import fr.cactus_industries.tools.SBToolbox;
import fr.cactus_industries.tools.messagesaving.MessageJsonTool;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

@Slf4j
public class ActionReactionHandler {
    public static void handleCommand(MessageCreateEvent event, String[] args) {
        if (args.length < 3) {
            event.getChannel().sendMessage("Updateticket usage:\nupdateticket <mess, type, title, desc, color, image, resend> <channel> [value, messageID, empty to delete option]\nupdateticket <granttime, grantlevel, rolelevel> <channel/role> [time value, level]");
            return;
        }
        log.info(args[1]);
        ServerTextChannel textChannel;
        Long chanID;
        MessageJsonTool msg;
        int level;
        Server server = event.getServer().get();
        switch (args[1].toLowerCase()) {
            case "resend":
                textChannel = SBToolbox.getChannel(args[2], server);
                if (textChannel == null) {
                    event.getChannel().sendMessage("Invalid channel. (Can the bot see it ? Is the ID valid ?)");
                    return;
                }
                // TODO Resend
                event.getChannel().sendMessage("Ticket message updated.");
                break;
        }
    }
}
