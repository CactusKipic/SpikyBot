package fr.cactus_industries.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class Commands {
    
    public static List<ApplicationCommand> commands = new ArrayList<>();
    public static long ownerID = 0L;
    
    public static void addCommands(DiscordApi api) {
        System.out.println("Ajout des commandes...");
        try {
            commands = api.bulkOverwriteGlobalApplicationCommands(
                    Arrays.asList(SlashCommand.with("ticket", "Add, delete or modify a ticket on a channel.",
                            Arrays.asList(
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Add a ticket on a channel.", Collections.singletonList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to add a ticket.", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Remove a ticket on a ticketed channel.", Collections.singletonList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel from which you want to remove the ticket.", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "message", "Set the message of the ticket.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Message of the ticket or ID of a message in this channel (empty to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "embed", "EMBED: Set an embed on the ticket.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "set", "Add or remove the embed (removing delete content).", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "title", "EMBED: Set the title of the embed.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Title to set or ID of a message in this channel (nothing to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "description", "EMBED: Set the description of the embed.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Description to set or ID of a message in this channel (nothing to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "color", "EMBED: Set the color of the embed.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "color", "Color to set in hex (nothing to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "image", "EMBED: Set the image of the embed.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "imagelink", "Link of the image to set (nothing to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "buttonlabel", "BUTTON: Set the label of the button.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "label", "Label to set on the button (nothing to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "buttonemoji", "BUTTON: Set the emoji of the button.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "emoji", "Unicode emoji or custom emoji the bot can access to set on the button (nothing to remove).", false)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "resend", "Resend a ticket on a channel.", Collections.singletonList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to resend a ticket.", true)
                                    )),
                                    // GRANT/TIME/LEVEL
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "granttime", "PERM: Set the time of grant for a ticket.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.LONG, "seconds", "Time in seconds.", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "grantlevel", "PERM: Set the permission level required for taking the ticket.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Level equals or superior to 0 (0 means no requirement).", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "rolelevel", "PERM: Set the level of grant for a role on tickets.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.ROLE, "ROLE", "Role to set or update.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Grant level for the role (0 to remove).", true)
                                    )),
                                    // List of tickets
                                    SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "list", "STATS: Get the list of channels with tickets and their settings.")
                            )).setDefaultPermission(true)
                            , SlashCommand.with("pdfreading", "Add, delete or manage PDF Reading on a channel.",
                            Arrays.asList(
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Add possibility to read PDF on a channel and auto-adding of reaction on PDFs.", Collections.singletonList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to add the PDF reading.", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Remove possibility to read PDF on a channel (doesn't remove added reaction).", Collections.singletonList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel from which you want to remove the PDF reading.", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "addreaction", "Add reaction to already posted PDF.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to add reaction to the existing PDF.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.LONG, "NB_DAY", "Number of day from today of messages to check (0 for beginning of the channel).", false)
                                    )),
                                    // GRANT/LEVEL
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "grantlevel", "PERM: Set the permission level required for reading a PDF.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the PDF reading.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Level equals or superior to 0 (0 means no requirement).", true)
                                    )),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "rolelevel", "PERM: Set the level of grant for a role on PDF reading.", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.ROLE, "ROLE", "Role to set or update.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Grant level for the role (0 to remove).", true)
                                    ))
                            )).setDefaultPermission(true)
                            , SlashCommand.with("spiky", "Spiky generic and admin commands.",
                            Arrays.asList(
                                    SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "info", "Get basics info about SpikyBot."
                                    ),
                                    SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "admin", "Administrations commands (you won't need this one).", Arrays.asList(
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "command", "The command.", true),
                                            SlashCommandOption.create(SlashCommandOptionType.STRING, "options", "The options.", false)
                                    ))
                            )).setDefaultPermission(true)
                    )).join();
            
            commands.forEach(s -> System.out.println(s.getName()));
            
            /* Pour test
            api.getServerById(739254091154456628L).get()
            * */
            /* - - - - - - - - - -
            *
            * Permission des commandes
            *
            - - - - - - - - - - */
            ownerID = api.getOwnerId();
            api.getServers().forEach(Commands::setPermissionOnServer);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Echec de l'ajout des commandes.");
            return;
        }
        System.out.println("Succès de l'ajout des commandes !");
    }
    
    public static boolean setPermissionOnServer(Server server) {
        try {
            System.out.println("Récupération des rôles de "+server.getName());
            List<ApplicationCommandPermissions> adminRoles = server.getRoles().stream().filter(r -> r.getAllowedPermissions().contains(PermissionType.ADMINISTRATOR)).map(
                    r -> {
                        System.out.println(r.getName());
                        return ApplicationCommandPermissions.create(r.getId(), ApplicationCommandPermissionType.ROLE, true);
                    }).collect(Collectors.toList());
            List<ApplicationCommandPermissions> owner = Arrays.asList(ApplicationCommandPermissions.create(ownerID, ApplicationCommandPermissionType.USER, true),
                    ApplicationCommandPermissions.create(server.getOwnerId(), ApplicationCommandPermissionType.USER, true));
            // -----
            return setPermissionOnCommands(server, Arrays.asList(adminRoles, owner));
        } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Echec de l'ajout des permissions sur les serveurs.");
        return false;
        }
    }
    
    public static boolean updatePermissionOfRole(Role role, boolean allow) {
        return setPermissionOnCommands(role.getServer(), Collections.singletonList(Collections.singletonList(
                ApplicationCommandPermissions.create(role.getId(), ApplicationCommandPermissionType.ROLE, allow))));
    }
    
    public static boolean updatePermissionOfUser(Server server, User user, boolean allow) {
        return setPermissionOnCommands(server, Collections.singletonList(Collections.singletonList(
                ApplicationCommandPermissions.create(user.getId(), ApplicationCommandPermissionType.USER, allow))));
    }
    
    private static boolean setPermissionOnCommands(Server server, List<List<ApplicationCommandPermissions>> listPermissions) {
        try {
            listPermissions.forEach(p -> {
                System.out.println("Réglage des permissions pour "+server.getName());
                try {
                    // TICKET
                    new ApplicationCommandPermissionsUpdater(server).setPermissions(p).update(getCommandByName("ticket").getId()).join();
                } catch(CompletionException e) {
                    e.printStackTrace();
                    System.out.println("Erreur ajout perm Ticket.");
                }
                try {
                    // PDF READING
                    new ApplicationCommandPermissionsUpdater(server).setPermissions(p).update(getCommandByName("pdfreading").getId()).join();
                } catch(CompletionException e) {
                    e.printStackTrace();
                    System.out.println("Erreur ajout perm PDF.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Echec lors de l'ajout des permissions sur commandes");
            return false;
        }
        return true;
    }
    
    public static ApplicationCommand getCommandByName(String name) {
        for (ApplicationCommand command : commands) {
            if(command.getName().equals(name))
                return command;
        }
        return null;
    }
}
