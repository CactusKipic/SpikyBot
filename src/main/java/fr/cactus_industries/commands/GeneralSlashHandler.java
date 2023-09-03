package fr.cactus_industries.commands;

import fr.cactus_industries.Main;
import fr.cactus_industries.model.CommandHandler;
import fr.cactus_industries.tools.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionFollowupMessageBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GeneralSlashHandler extends CommandHandler {
    
    public static final String COMMAND_NAME = "spiky";
    
    public void init() {
        // Rien à faire ici
    }
    
    public InteractionImmediateResponseBuilder handleCommand(SlashCommandInteraction command) {
        Optional<Server> optServer = command.getServer();
        InteractionImmediateResponseBuilder responder = command.createImmediateResponder();
        if (optServer.isEmpty()) {
            return responder.setContent("This command has to be used in a server.");
        }
        Server server = optServer.get();
        SlashCommandInteractionOption baseCommand = command.getArgumentByIndex(0).get();
        List<SlashCommandInteractionOption> options = baseCommand.getOptions();
    
        switch (baseCommand.getName()) {
            /*
                COMMANDES 'PUBLIC'
             */
            case "info":
                int nserver;
                nserver = command.getApi().getServers().size();
                new MessageBuilder().setEmbed(new EmbedBuilder().setAuthor("SpikyBot", "https://github.com/CactusKipic/SpikyBot", "https://i.imgur.com/VNgl2fU.jpg")
                        .setTitle("version " + Main.class.getPackage().getImplementationVersion()).setColor(new Color(12756342))
                        .setDescription("SpikyBot is a small bot developed by CactusKipic.")
                        .addInlineField("SpikyBot is deployed on", "**" + nserver + "** servers")
                        .addInlineField("and manage", "**" + (command.getApi().getServers().stream()
                                .map(Server::getMemberCount).reduce(0, Integer::sum) - nserver) + "** users"))
                        .send(command.getChannel().get());
                responder.setContent("✓");
                break;
            /*
                COMMANDES ADMINS
             */
            default:
                if (!Permissions.isAdmin(command.getUser(), server)) {
                    return responder.setContent("You don't have permission to use this command.");
                }
                switch (baseCommand.getName()) {
                    case "":
                        
                        break;
                    case "admin":
                        if(!command.getUser().isBotOwner()){
                            return responder.setContent("You don't have permission to use this command.");
                        }
                        Optional<String> rootCommand = baseCommand.getStringValue();
                        if (rootCommand.isEmpty()) {
                            return responder.setContent("Il n'y a pas de commande. Est-ce une blague que je suis trop robot pour comprendre ?");
                        }
                        switch (rootCommand.get()) {
                            case "test":
                                responder.setContent("Je réponds rapidement...").respond();
                                try {
                                    TimeUnit.SECONDS.sleep(15);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                InteractionFollowupMessageBuilder followupMessageBuilder = command.createFollowupMessageBuilder();
                                followupMessageBuilder.setContent("Et là je répond plus tard ! Dingue, nan ?!").send();
                                log.info("Le followup est lancé.");
                                break;
                            case "shutdown":
                                Main.Disconnect(command.getApi());
                                break;
                            default:
                                responder.setContent("Suis-je trop robot pour comprendre cette blague ?");
                                break;
                        }
                        break;
                    default:
                        responder.setContent("Unkown command. (that's weird)");
                        break;
                }
                break;
        }
        
        return responder;
    }
    
    @Override
    public String canHandle() {
        return COMMAND_NAME;
    }
}
