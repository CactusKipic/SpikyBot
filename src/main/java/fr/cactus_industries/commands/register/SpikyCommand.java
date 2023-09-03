package fr.cactus_industries.commands.register;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;

public class SpikyCommand implements SpikyCommandInterface {
    
    @Override
    public SlashCommandBuilder getCommand() {
        return SlashCommand.withRequiredPermissions("spiky", "Spiky generic and admin commands.",
                Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "info", "Get basics info about SpikyBot."
                        ),
                        SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "admin", "Administrations commands (you won't need this one).", Arrays.asList(
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "command", "The command.", true),
                                SlashCommandOption.create(SlashCommandOptionType.STRING, "options", "The options.", false)
                        ))
                ),
                PermissionType.ADMINISTRATOR)
                .setEnabledInDms(false);
    }
}
