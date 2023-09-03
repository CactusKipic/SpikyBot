package fr.cactus_industries.commands.register;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;
import java.util.Collections;

public class TicketCommand implements SpikyCommandInterface {
    @Override
    public SlashCommandBuilder getCommand() {
        return SlashCommand.withRequiredPermissions("ticket", "Add, delete or modify a ticket on a channel.",
                Arrays.asList(
                        // ADD
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Add a ticket on a channel.", Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to add a ticket.", true)
                        )),
                        // REMOVE
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Remove a ticket on a ticketed channel.", Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel from which you want to remove the ticket.", true)
                        )),
                        // MESSAGE
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "message", "Set the message of the ticket.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Message of the ticket or ID of a message in this channel (empty to remove).", false)
                        )),
                        // EMBED
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "embed", "EMBED: Set an embed on the ticket.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "set", "Add or remove the embed (removing delete content).", true)
                        )),
                        // TITLE
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "title", "EMBED: Set the title of the embed.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Title to set or ID of a message in this channel (nothing to remove).", false)
                        )),
                        // DESCRIPTION
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "description", "EMBED: Set the description of the embed.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "text", "Description to set or ID of a message in this channel (nothing to remove).", false)
                        )),
                        // COLOR
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "color", "EMBED: Set the color of the embed.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "color", "Color to set in hex (nothing to remove).", false)
                        )),
                        // IMAGE
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "image", "EMBED: Set the image of the embed.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "imagelink", "Link of the image to set (nothing to remove).", false)
                        )),
                        // BUTTON LABEL
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "buttonlabel", "BUTTON: Set the label of the button.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "label", "Label to set on the button (nothing to remove).", false)
                        )),
                        // BUTTON EMOJI
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "buttonemoji", "BUTTON: Set the emoji of the button.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "emoji", "Unicode emoji or custom emoji the bot can access to set on the button (nothing to remove).", false)
                        )),
                        // RESEND
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "resend", "Resend a ticket on a channel.", Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to resend a ticket.", true)
                        )),
                        // GRANT TIME
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "granttime", "PERM: Set the time of grant for a ticket.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.LONG, "seconds", "Time in seconds.", true)
                        )),
                        // GRANT LEVEL
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "grantlevel", "PERM: Set the permission level required for taking the ticket.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the ticket.", true),
                                SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Level equals or superior to 0 (0 means no requirement).", true)
                        )),
                        // ROLE LEVEL
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "rolelevel", "PERM: Set the level of grant for a role on tickets.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, "ROLE", "Role to set or update.", true),
                                SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Grant level for the role (0 to remove).", true)
                        )),
                        // LIST
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "list", "STATS: Get the list of channels with tickets and their settings.")
                ),
                PermissionType.ADMINISTRATOR);
    }
}
