package fr.cactus_industries.commands;

import fr.cactus_industries.commands.register.SpikyCommandInterface;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.ApplicationCommand;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
//@Service
public class SlashCommandCreator {

    private final Set<ApplicationCommand> commands;
    
    public SlashCommandCreator(DiscordApi api, List<SpikyCommandInterface> spikyCommands) {
        log.info("[Startup] Registering commands...");
        commands = api.bulkOverwriteGlobalApplicationCommands(spikyCommands.stream().map(SpikyCommandInterface::getCommand)
                .collect(Collectors.toSet())).join();
        log.info("Commands registered !");
    }
}
