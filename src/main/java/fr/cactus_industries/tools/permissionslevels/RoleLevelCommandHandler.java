package fr.cactus_industries.tools.permissionslevels;

import fr.cactus_industries.tools.PremiumServers;
import fr.cactus_industries.tools.SBToolbox;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.postgresql.shaded.com.ongres.scram.client.ScramClient;

import java.util.Locale;

public class RoleLevelCommandHandler {
    
    
    public static void handleCommand(MessageCreateEvent event, String[] args) {
        if (args.length < 3) {
            event.getChannel().sendMessage("RoleLevel usage:\nrolelevel <set, remove> <role> [level]");
            return;
        }
        Role role;
        switch (args[2].toLowerCase(Locale.ROOT)){
            case "set":
                if (args.length < 5) {
                    event.getChannel().sendMessage("Command usage:\nrolelevel set <role> <level>");
                    return;
                }
                role = SBToolbox.getRole(args[2], event.getServer().get());
                if (role == null) {
                    event.getChannel().sendMessage("Invalid role.");
                    return;
                }
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
                if (PermissionsLevelsHandler.setPermLevelOfRole(SBPermissionType.TicketChannel, role, level)) {
                    event.getChannel().sendMessage("Role level updated successfully !");
                } else {
                    event.getChannel().sendMessage("Unexpected error, could not update role level.");
                }
                break;
            case "remove":
                if (args.length < 4) {
                    event.getChannel().sendMessage("Command usage:\nrolelevel remove <role>");
                    return;
                }
                role = SBToolbox.getRole(args[2], event.getServer().get());
                if (role == null) {
                    event.getChannel().sendMessage("Invalid role.");
                    return;
                }
                if(PermissionsLevelsHandler.deletePermLevelOfRole(SBPermissionType.TicketChannel, role)){
                    event.getChannel().sendMessage("Role level removed successfully !");
                } else {
                    event.getChannel().sendMessage("Unexpected error, could not delete role level.");
                }
                break;
            default:
                event.getChannel().sendMessage("RoleLevel usage:\nrolelevel <set, remove> <role> [level]");
                break;
        }
    }
}
