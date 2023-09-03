package fr.cactus_industries.commands.register;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;
import java.util.Collections;

public class PDFReadingCommand implements SpikyCommandInterface {
    @Override
    public SlashCommandBuilder getCommand() {
        return SlashCommand.withRequiredPermissions("pdfreading", "Add, delete or manage PDF Reading on a channel.",
                Arrays.asList(
                        // ADD
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Add possibility to read PDF on a channel and auto-adding of reaction on PDFs.", Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to add the PDF reading.", true)
                        )),
                        // REMOVE
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Remove possibility to read PDF on a channel (doesn't remove added reaction).", Collections.singletonList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel from which you want to remove the PDF reading.", true)
                        )),
                        // ADD REACTION
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "addreaction", "Add reaction to already posted PDF.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to add reaction to the existing PDF.", true),
                                SlashCommandOption.create(SlashCommandOptionType.LONG, "NB_DAY", "Number of day from today of messages to check (0 for beginning of the channel).", false)
                        )),
                        // GRANT LEVEL
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "grantlevel", "PERM: Set the permission level required for reading a PDF.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.CHANNEL, "CHANNEL", "Channel on which you want to modify the PDF reading.", true),
                                SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Level equals or superior to 0 (0 means no requirement).", true)
                        )),
                        // ROLE LEVEL
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "rolelevel", "PERM: Set the level of grant for a role on PDF reading.", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.ROLE, "ROLE", "Role to set or update.", true),
                                SlashCommandOption.create(SlashCommandOptionType.LONG, "level", "Grant level for the role (0 to remove).", true)
                        ))
                ),
                PermissionType.ADMINISTRATOR);
    }
}
