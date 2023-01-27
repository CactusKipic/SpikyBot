package fr.cactus_industries.tools.tickets;

import com.vdurmont.emoji.EmojiManager;
import fr.cactus_industries.database.interaction.service.TicketService;
import fr.cactus_industries.database.schema.table.TTicketChannelEntity;
import fr.cactus_industries.tools.Permissions;
import fr.cactus_industries.tools.messagesaving.MessageJsonTool;
import fr.cactus_industries.tools.permissionslevels.PermissionsLevelsHandler;
import fr.cactus_industries.tools.permissionslevels.SBPermissionType;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TicketSlashHandler {
    
    private final TicketService ticketService;
    private final TicketsLogicDatabase ticketsLogicDatabase;
    private final TicketsLogicMessage ticketsLogicMessage;
    
    public TicketSlashHandler(TicketService ticketService, TicketsLogicDatabase ticketsLogicDatabase,
                              TicketsLogicMessage ticketsLogicMessage) {
        this.ticketService = ticketService;
        this.ticketsLogicDatabase = ticketsLogicDatabase;
        this.ticketsLogicMessage = ticketsLogicMessage;
    }
    
    public void handleCommand(SlashCommandInteraction command){
        Optional<Server> optServer = command.getServer();
        InteractionImmediateResponseBuilder responder = command.createImmediateResponder();
        if(optServer.isEmpty()){
            responder.setContent("This command has to be used in a server.").respond();
            return;
        }
        Server server = optServer.get();
        if (!Permissions.isAdmin(command.getUser(), server)) {
            responder.setContent("You don't have permission to use this command.").respond();
            return;
        }
        SlashCommandInteractionOption baseCommand = command.getOptions().get(0);
        List<SlashCommandInteractionOption> options = baseCommand.getOptions();
        String text; // Variable pour le texte envoyé avec la commande (s'il y a)
        TTicketChannelEntity ticket; // Ticket du salon
        MessageJsonTicket mess; // Variable pour le message du ticket
        // On regarde si l'option indique un salon, si oui on le récupère (évitant de le récupérer indépendamment plus tard)
        if (options.size() > 0 && options.get(0).getName().equalsIgnoreCase("channel")) {
            System.out.println("Il y a un channel");
            ServerChannel channel = options.get(0).getChannelValue().get();
            if (!channel.getType().isTextChannelType()) {
                responder.setContent("The given channel is not a text channel.").respond();
                return;
            }
            ServerTextChannel textChannel = channel.asServerTextChannel().get();
            String name = baseCommand.getName();
            System.out.println(name);
            switch (name) {
                // RESEND a ticket
                case "resend": {
                    this.ticketsLogicMessage.resendTicket(textChannel);
                    responder.setContent("The ticket was successfully resend.");
                    break;
                }
                // MESSAGE change the message of the ticket
                case "message": {
                    text = options.get(1).getStringValue().orElse(null);
                    //text = baseCommand.getSecondOptionStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. The specified channel doesn't have a ticket. Add one first.");
                    } else {
                        if(text == null) {
                            ticket.getMessageJsonTicket().setContent(null);
                        } else {
                            if(text.length()>1000){
                                responder.setContent("Your text is too long. Ticket's message are limited to 1000 characters.");
                                break;
                            } else {
                                ticket.getMessageJsonTicket().setContent(text);
                            }
                        }
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The message of the ticket has been successfully " + (text == null ? "deleted." : "updated."));
                    }
                    break;
                }
                // EMBED TITLE change title of embed
                case "title": {
                    text = options.get(1).getStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        updateEmbedField(responder, ticket.getMessageJsonTicket(), ticket.getMessageJsonTicket()::setEmbTitle,
                                "title", textChannel, text, 256, 0);
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The "+name+" of the ticket's embed has been successfully " + ((text == null) ? "deleted." : "updated."));
                    }
                    break;
                }
                // EMBED DESCRIPTION change description of embed
                case "description": {
                    text = options.get(1).getStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        updateEmbedField(responder, ticket.getMessageJsonTicket(), ticket.getMessageJsonTicket()::setEmbDesc,
                                "embed", textChannel, text, 4096, 0);
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The "+name+" of the ticket's embed has been successfully " + ((text == null) ? "deleted." : "updated."));
                    }
                    break;
                }
                // EMBED IMAGE change the image of embed
                case "image": {
                    text = options.get(1).getStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        updateEmbedField(responder, ticket.getMessageJsonTicket(), ticket.getMessageJsonTicket()::setEmbDesc,
                                "image", textChannel, text, 1024, 2);
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The "+name+" of the ticket's embed has been successfully " + ((text == null) ? "deleted." : "updated."));
                    }
                    break;
                }
                // EMBED IMAGE change the image of embed
                case "color": {
                    text = options.get(1).getStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        if (!ticket.getMessageJsonTicket().setEmbColor(text)) {
                            responder.setContent("This is not a valid color code. Please use hexadecimal RGB color notation.");
                            break;
                        }
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The "+name+" of the ticket's embed has been successfully " + ((text == null) ? "deleted." : "updated."));
                    }
                    break;
                }
                // EMBED IMAGE change the image of embed
                case "embed": {
                    Boolean embed = options.get(1).getBooleanValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        if(embed == null) {
                            responder.setContent("Missing 1 argument.");
                            break;
                        }
                        ticket.getMessageJsonTicket().setEmbed(embed);
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("Ticket's embed successfully " + (embed ? "enabled." : "deleted."));
                    }
                    break;
                }
                // BUTTON set the label
                case "buttonlabel":{
                    String newLabel = null;
                    text = options.get(1).getStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        MessageJsonTool.ButtonJson buttonJson = ticket.getMessageJsonTicket().getButtonList().get(0);
                        if (text == null) { // Supprimer le titre
                            if(buttonJson.getEmoji() == null){
                                responder.setContent("You can't delete the button's label if there is no emoji.").respond();
                                return;
                            }
                            buttonJson.setLabel("");
                        } else {
                            if (text.matches("^\\d{17,19}$")) { // Looks like a message ID
                                try {
                                    newLabel = textChannel.getMessageById(text).join().getContent();
                                } catch (CompletionException e) {
                                    e.printStackTrace();
                                    // Erreur on répond directement et on ferme
                                    responder.setContent("Error while getting the message from its ID.").respond();
                                    return;
                                }
                            } else {
                                newLabel = text;
                            }
                            if(newLabel.length()>80){
                                responder.setContent("Labels can't be longer than 80 characters.").respond();
                                return;
                            }
                            buttonJson.setLabel(newLabel);
                        }
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The label of the ticket's button has been successfully " + ((newLabel == null) ? "deleted." : "updated."));
                    }
                    break;
                }
                // BUTTON set the label
                case "buttonemoji":{
                    String newEmoji = null;
                    text = options.get(1).getStringValue().orElse(null);
                    ticket = ticketService.findChannel(textChannel).orElse(null);
                    if (ticket == null) {
                        responder.setContent("Error. Does this channel have a ticket ?");
                    } else {
                        MessageJsonTool.ButtonJson buttonJson = ticket.getMessageJsonTicket().getButtonList().get(0);
                        if (text == null) { // Supprimer le titre
                            if(buttonJson.getLabel().length() == 0){
                                responder.setContent("You can't delete the button's emoji if there is no label.").respond();
                                return;
                            }
                            buttonJson.setEmoji(null);
                        } else {
                            newEmoji = text;
                            // On vérifie que c'est un émoji valide
                            if(!(EmojiManager.isEmoji(newEmoji)
                                    || (newEmoji.matches("^<:.{2,32}:\\d{17,19}>$")
                                    && server.getApi().getCustomEmojiById(
                                            newEmoji = newEmoji.substring(newEmoji.lastIndexOf(':')+1,newEmoji.length()-1)).isPresent()))){
                                responder.setContent("This is not a valid emoji.").respond();
                                return;
                            }
                            buttonJson.setEmoji(newEmoji);
                        }
                        // Update du message en base
                        ticketService.saveChannel(ticket);
                        responder.setContent("The emoji of the ticket's button has been successfully " + ((newEmoji == null) ? "deleted." : "updated."));
                    }
                    break;
                }
                // ADD a ticket
                case "add": {
                    // Limite de 5 tickets par serveur non premium
                    //if (!PremiumServers.isServerPremium(server) && ChannelsTicketHandler.getNumberOfTicketedChannel(server) >= 5) {
                    responder.setContent(this.ticketsLogicDatabase.addChannel(textChannel));
                    break;
                }
                // REMOVE a ticket
                case "remove": {
                    responder.setContent(this.ticketsLogicDatabase.removeChannel(textChannel));
                    break;
                }
                // SET GRANT TIME on a ticket
                case "granttime": {
                    responder.setContent(this.ticketsLogicDatabase.setGrantTime(textChannel, baseCommand.getOptionLongValueByIndex(1).orElse(null)));
                    break;
                }
                case "grantlevel": {
                    responder.setContent(this.ticketsLogicDatabase.setGrantLevel(textChannel, baseCommand.getOptionLongValueByIndex(1).orElse(null)));
                    break;
                }
                default:
                    responder.setContent("Unkown command. (That's weird)");
                    break;
            }
            
        } else {
            switch (baseCommand.getName()){
                case"rolelevel": {
                    Role role = baseCommand.getRoleValue().orElse(null);
                    if(role == null) {
                        responder.setContent("The given role is not a valid role.");
                    } else {
                        Long level = baseCommand.getOptionLongValueByIndex(1).orElse(null);
                        if(level == null) {
                            if (!PermissionsLevelsHandler.deletePermLevelOfRole(SBPermissionType.TicketChannel, role)) {
                                responder.setContent("Error while deleting the grant level of the role. Contact the developer if this error persist.");
                            } else {
                                responder.setContent("Role's grant level successfully deleted.");
                            }
                        } else {
                            if (PermissionsLevelsHandler.setPermLevelOfRole(SBPermissionType.TicketChannel, role, Math.toIntExact(level))){
                                responder.setContent("Error while updating the grant level of the role. Contact the developer if this error persist.");
                            } else {
                                responder.setContent("Role's grant level successfully updated.");
                            }
                        }
                    }
                }
                break;
                case "list": {
                    final List<TTicketChannelEntity> listTickets = this.ticketService.findAllServerChannelNoJson(server.getId());
                    final EmbedBuilder embedBuilder = new EmbedBuilder();
                    
                    ArrayList<String> channel = new ArrayList<>();
                    ArrayList<Long> grantTime = new ArrayList<>();
                    ArrayList<Long> levelRequired = new ArrayList<>();
                    ArrayList<Long> numberStillGranted = new ArrayList<>();
                    listTickets.forEach(tck -> {
                        final ServerTextChannel textChannel = server.getTextChannelById(tck.getChannel()).orElse(null);
                        if(textChannel != null) {
                            // On ajoute le tag du salon
                            channel.add(textChannel.getMentionTag());
                            // On regarde combien de personne ont la permission d'écrire sans ticket actif
                            numberStillGranted.add(
                                    textChannel.getOverwrittenUserPermissions().entrySet().stream().filter(entry -> entry.getValue().getState(PermissionType.SEND_MESSAGES).equals(PermissionState.ALLOWED)).count()
                                    - ticketService.countGrantedOnChannel(textChannel));
                            // On ajoute le niveau nécessaire pour prendre le ticket sur le salon
                            levelRequired.add(tck.getGrantLevel());
                            // On ajoute le temps d'écriture accordé sur le salon
                            grantTime.add(tck.getGrantTime());
                        }
                    });
                    embedBuilder.setTitle("List of channels with tickets on "+server.getName());
                    embedBuilder.addInlineField("Channel", String.join("\n", channel));
                    embedBuilder.addInlineField("Grant time", grantTime.stream().map(Objects::toString).collect(Collectors.joining("\n")));
                    embedBuilder.addInlineField("Level required", levelRequired.stream().map(Objects::toString).collect(Collectors.joining("\n")));
                    embedBuilder.addInlineField("People still granted", numberStillGranted.stream().map(Objects::toString).collect(Collectors.joining("\n")));
                    responder.addEmbed(embedBuilder);
                }
                
            }
            
            responder.setContent("Commande non implémentée pour le moment.");
            System.out.println("Il n'y a pas de channel");
        }
        responder.respond();
    }
    
    // Update a field of an embed in a message
    // linkType: 0 not a link, 1 link, 2 link to an image
    private void updateEmbedField(InteractionImmediateResponseBuilder responder, MessageJsonTicket mess, Consumer<String> messJsonFunction,
                                         String name, ServerTextChannel textChannel, String content, int fieldLimit, int linkType) {
        if(!mess.setEmbed()) {
            responder.setContent("This ticket doesn't have embed. Add an embed by setting it true with /ticket embed");
            return;
        }
        
        String newValue = null;
        if (content == null) { // Supprimer le titre
            messJsonFunction.accept(null);
        } else {
            if (content.matches("^\\d{17,19}$")) { // Looks like a message ID
                try {
                    newValue = textChannel.getMessageById(content).join().getContent();
                } catch (CompletionException e) {
                    e.printStackTrace();
                    // Erreur on répond directement et on ferme
                    responder.setContent("Error while getting the message from its ID.");
                    return;
                }
            } else {
                newValue = content;
            }
            if(newValue.length() > fieldLimit){
                responder.setContent("Your text is too long. The size of the field "+ name +" is limited to "+fieldLimit+".");
                return;
            }
            if(linkType == 2 && newValue.matches("(?i)^https?://.*?\\..{2,5}/.*\\.(png|jpg|gif)$")){
                responder.setContent("The given link doesn't look like a link to an image or gif. Make sure it ends in .png, .jpg or .gif.");
                return;
            }
            if(linkType == 1 && newValue.matches("(?i)^https?://.*?\\..{2,5}")){
                responder.setContent("The given link doesn't look like an internet link.");
                return;
            }
            messJsonFunction.accept(newValue);
            if(mess.globalEmbedLength() > 6000){
                responder.setContent("Your text is too long. The global size of the text in an embed is limited to 6000.");
            }
        }
    }
    /*
    public static void forceUpdateTicket(ServerTextChannel channel) {
        Long ticketMessage = ChannelsTicketHandler.getMessageID(channel);
        try {
            channel.getMessageById(ticketMessage).join().delete().join();
        } catch (CompletionException e){
            System.out.println("Old ticket wasn't found. Probably deleted by other.");
        }
        Message nMessage = ChannelsTicketHandler.getMessage(channel).create(channel.getApi()).send(channel).join();
        ChannelsTicketHandler.updateMessageID(channel, nMessage.getId());
    }
    
    public static void updateTicket(ServerTextChannel channel) {
        Long ticketMessage = ChannelsTicketHandler.getMessageID(channel);
        
        if (ticketMessage != channel.getMessages(1).join().getNewestMessage().get().getId()) {
            channel.getMessageById(ticketMessage).join().delete().join();
            Message nMessage = ChannelsTicketHandler.getMessage(channel).create(channel.getApi()).send(channel).join();
            ChannelsTicketHandler.updateMessageID(channel, nMessage.getId());
        }
    }*/
}
