package fr.cactus_industries;

import fr.cactus_industries.tools.ConfigSpiky;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BotConnexion {
    
    private final DiscordApi api;
    
    public BotConnexion() {
        // Initialisation config locale
        if (!ConfigSpiky.init()) {
            log.error("Could not load config.yml file (from resource and/or local).");
        }
        // Récupération du token depuis la config locale
        String token = ConfigSpiky.getConfigString("bot-token");
        if (token.isEmpty()) {
            log.error("Bot token is empty, add a correct token to start the bot in the config.yml.");
        }
        log.info("Connexion...");
        api = new DiscordApiBuilder().setToken(token).setAllIntentsExcept(Intent.GUILD_PRESENCES).login().join();
        log.info("Application ID : [{}]", api.getClientId());
    }
    
    @Bean
    public DiscordApi getApi() {
        return api;
    }
}
