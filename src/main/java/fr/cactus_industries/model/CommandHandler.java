package fr.cactus_industries.model;

import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import javax.annotation.PostConstruct;

public abstract class CommandHandler {
    
    @PostConstruct
    public abstract void init();
    
    public abstract InteractionImmediateResponseBuilder handleCommand(SlashCommandInteraction command);
    
    public abstract String canHandle();
}
