package fr.cactus_industries.tools.pdfreading;

import fr.cactus_industries.database.interaction.service.PDFReadingService;
import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntity;
import fr.cactus_industries.tools.Permissions;
import fr.cactus_industries.tools.PremiumServers;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PDFSlashHandler {
    
    private final PDFReadingService pdfReadingService;
    private final PDFReactionListener pdfReactionListener;
    private final PDFMessageListener pdfMessageListener;
    private final DiscordApi api;
    
    private final HashMap<Long, Long> addReactionTimer = new HashMap<>(); // ServerID / Time
    
    public PDFSlashHandler(PDFReadingService pdfReadingService, PDFReactionListener pdfReactionListener,
                           PDFMessageListener pdfMessageListener, DiscordApi api) {
        this.pdfReadingService = pdfReadingService;
        this.pdfReactionListener = pdfReactionListener;
        this.pdfMessageListener = pdfMessageListener;
        this.api = api;
    }
    
    @PostConstruct
    public void init(){
        /*PDFDB.getAllPDFReadingOnChannel().forEach((key, val) -> {
            Server server = api.getServerById(key).get();
            val.forEach(c ->{
                ServerTextChannel channel = server.getTextChannelById(c).get();
                channel.addReactionAddListener(pdfReactionListener);
                channel.addMessageCreateListener(pdfMessageListener);
            });
        });*/
        pdfReadingService.findAllChannel().stream().collect(Collectors.groupingBy(TPDFReadingChannelEntity::getServer)).forEach((serverId, channels) -> {
            Server server = api.getServerById(serverId).orElse(null);
            if(server != null) {
                channels.forEach(channel -> {
                    server.getTextChannelById(channel.getChannel()).ifPresentOrElse(textChannel -> {
                        textChannel.addReactionAddListener(pdfReactionListener);
                        textChannel.addMessageCreateListener(pdfMessageListener);
                    }, // Si le salon n'est pas trouvé, on le supprime de la BDD
                    () -> {
                        System.out.println("Suppression du salon "+channel.getChannel()+" sur le serveur "+server.getName() + " ("+serverId+").");
                        pdfReadingService.deleteChannel(channel);
                    });
                });
            } else {
                System.out.println("Could not find server with ID "+serverId+" for PDFReading initialisation.");
            }
        });
    }
    
    public void handleCommand(SlashCommandInteraction command) {
        Optional<Server> optServer = command.getServer();
        InteractionImmediateResponseBuilder responder = command.createImmediateResponder();
        if (optServer.isEmpty()) {
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
        
        // On regarde si l'option indique un salon, si oui on le récupère (évitant de le récupérer à chaque fois plus tard)
        if (options.get(0).getChannelValue().isPresent()) {
            ServerChannel channel = options.get(0).getChannelValue().get();
            if (!channel.getType().isTextChannelType()) {
                responder.setContent("The given channel is not a text channel.").respond();
                return;
            }
            ServerTextChannel textChannel = channel.asServerTextChannel().get();
            String name = baseCommand.getName();
            System.out.println(name);
            switch (baseCommand.getName()){
                case "add": {
                    //if(PDFDB.isChannelOnPDFReading(textChannel)){
                    if(pdfReadingService.findChannel(textChannel).isPresent()){
                        responder.setContent("This channel is already on PDF reading.");
                    } else {
                        //if (!PremiumServers.isServerPremium(textChannel.getServer()) && PDFDB.getNumberOfChannelOnPDFReading(textChannel.getServer()) > 5) {
                        if (!PremiumServers.isServerPremium(textChannel.getServer()) && pdfReadingService.findAllServerChannel(textChannel.getServer().getId()).size() > 5) {
                            responder.setContent("You've reach the limit of 5 PDF reading per server. Premium servers are exempt of this limit.");
                        } else {
                            //if (PDFDB.addPDFReadingToChannel(textChannel)) {
                            if (pdfReadingService.saveChannel(new TPDFReadingChannelEntity(textChannel)) != null) { // Ne peut pas être nul donc...
                                responder.setContent("PDFReading successfully added to the channel.");
                            } else {
                                responder.setContent("An unexpected error occurred. Try again later or warn the bot owner.");
                                return;
                            }
                        }
                    }
                    break;
                }
                case "remove": {
                    //if(!PDFDB.isChannelOnPDFReading(textChannel)){
                    final Optional<TPDFReadingChannelEntity> optPDFChannel = pdfReadingService.findChannel(textChannel);
                    if(optPDFChannel.isEmpty()){
                        responder.setContent("PDF reading is not enabled on this channel. And I can't remove something that don't exist :woman_shrugging:");
                    } else {
                        pdfReadingService.deleteChannel(optPDFChannel.get());
                        responder.setContent("PDFReading successfully removed from the channel.");
                    }
                    break;
                }
                case "addreaction": {
                    //if(!PDFDB.isChannelOnPDFReading(textChannel)){
                    if(pdfReadingService.findChannel(textChannel).isEmpty()){
                        responder.setContent("This channel has no PDFReading on it.");
                        break;
                    }
                    Long nb = baseCommand.getOptionLongValueByIndex(1).orElse(null);
                    
                    if(nb != null && nb < 0) {
                        responder.setContent("SpikyBot cannot look into the future (Or I'd already be rich ! :money_mouth:).");
                        break;
                    }
                    
                    if(!command.getUser().isBotOwner()){
                        long serverID = server.getId();
                        long time = new Date().getTime();
                        if(addReactionTimer.containsKey(serverID) && addReactionTimer.get(serverID) > time){
                            responder.setContent("You can add reaction on old messages only once every four hours per server.");
                            break;
                        }
                        addReactionTimer.put(serverID, time + 14_400_000);
                    }
                    
                    if(nb == null || nb == 0){
                        if(!PremiumServers.isServerPremium(server)){
                            responder.setContent("Only premium servers can add reaction under PDF for all anterior messages.");
                            break;
                        }
                        responder.setContent("Starting to add reaction...\nThis may take a while, a message will be sent on this channel when complete.").respond();
                        textChannel.getMessagesAsStream().forEach(PDFSlashHandler::addReactionOnStream);
                        command.getChannel().get().sendMessage("Reactions successfully added on "+textChannel.getMentionTag()+".");
                        return;
                    }
                    
                    if(nb > 30) {
                        if (!PremiumServers.isServerPremium(server)) {
                            responder.setContent("Number of days for adding reactions on PDF is limited to 30 for non-Premium servers.");
                            break;
                        }
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, (int) -nb);
                    long dateFrom = cal.getTimeInMillis();
                    responder.setContent("Starting to add reaction...\nThis may take a while, a message will be sent on this channel when complete.").respond();
                    textChannel.getMessagesAfterAsStream(dateFrom).forEach(PDFSlashHandler::addReactionOnStream);
                    textChannel.sendMessage("Reactions successfully added on "+textChannel.getMentionTag()+" from the last "+nb+" days.");
                    return;
                }
                case "grantlevel": {
                    //if(!PDFDB.isChannelOnPDFReading(textChannel)){
                    final TPDFReadingChannelEntity pdfReadingChannel = pdfReadingService.findChannel(textChannel).orElse(null);
                    if(pdfReadingChannel == null){
                        responder.setContent("There is no PDF reading on this channel.");
                    } else {
                        Long level = baseCommand.getOptionLongValueByIndex(1).orElse(null);
                        if(level == null){
                            /*if (!PermissionsLevelsHandler.setPermLevelOnChannel(SBPermissionType.PDFReading, textChannel, 0)) {
                                responder.setContent("Error while updating the grant time. Contact the developer if this error persist.");
                                break;
                            }*/
                            pdfReadingChannel.setGrantLevel(0);
                        } else {
                            if(!(level > 1 && !PremiumServers.isServerPremium(server))) {
                                /*if (!PermissionsLevelsHandler.setPermLevelOnChannel(SBPermissionType.PDFReading, textChannel, Math.toIntExact(level))) {
                                    responder.setContent("Error while updating the grant level. Contact the developer if this error persist.");
                                    break;
                                }*/
                                pdfReadingChannel.setGrantLevel(level);
                            } else {
                                responder.setContent("Grant level on PDF reading is limited to 1 for non-premium servers.");
                                break;
                            }
                        }
                        pdfReadingService.saveChannel(pdfReadingChannel);
                        responder.setContent("PDF reading's grant level successfully set to " + (level == null || level == 0 ? "default." : level+"."));
                    }
                    break;
                }
                default:
                    responder.setContent("Unkown command. (That's weird)");
                    break;
            }
        }
        responder.respond();
    }
    
    private static void addReactionOnStream(Message m) {
        List<MessageAttachment> MAL = m.getAttachments();
        if (MAL.size() > 0) {
            URL url = MAL.get(0).getUrl();
            if (url.getPath().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                m.addReaction("\uD83E\uDD16");
            }
        }
    }
}
