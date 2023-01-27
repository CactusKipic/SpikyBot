package fr.cactus_industries.commands;

import fr.cactus_industries.tools.pdfreading.PDFSlashHandler;
import fr.cactus_industries.tools.tickets.TicketSlashHandler;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SlashCommandListener implements SlashCommandCreateListener {
    
    private final PDFSlashHandler pdfSlashHandler;
    private final TicketSlashHandler ticketSlashHandler;
    private final GeneralSlashHandler generalSlashHandler;
    private final DiscordApi api;
    private static SlashCommandListener instance;
    
    public SlashCommandListener(PDFSlashHandler pdfSlashHandler, TicketSlashHandler ticketSlashHandler, GeneralSlashHandler generalSlashHandler, DiscordApi api) {
        this.pdfSlashHandler = pdfSlashHandler;
        this.ticketSlashHandler = ticketSlashHandler;
        this.generalSlashHandler = generalSlashHandler;
        this.api = api;
        instance = this;
    }
    
    @PostConstruct
    public void init() {
        // Ajout du listener Slash commands au bot
        api.addSlashCommandCreateListener(instance);
    }
    
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommand = event.getSlashCommandInteraction();
        System.out.println("Slash commande : "+slashCommand.getCommandName());
        switch (slashCommand.getCommandName()) {
            case "ticket":
                System.out.println("Command ticket !");
                ticketSlashHandler.handleCommand(slashCommand);
                break;
            case "pdfreading":
                pdfSlashHandler.handleCommand(slashCommand);
                break;
            case "spiky":
                generalSlashHandler.handleCommand(slashCommand);
                break;
            default:
                System.out.println("Slash command was not recognized (that's weird).");
                slashCommand.createImmediateResponder().setContent("What is it that is this thing ?!").respond();
        }
    }
}




