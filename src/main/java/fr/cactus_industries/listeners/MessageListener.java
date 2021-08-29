package fr.cactus_industries.listeners;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.ArrayList;
import fr.cactus_industries.Main;
import fr.cactus_industries.tools.ConfigSpiky;
import fr.cactus_industries.tools.pdfreading.PDFCommandHandler;
import fr.cactus_industries.tools.tickets.TicketUpdater;
import fr.cactus_industries.tools.tickets.ChannelsTicketHandler;
import java.util.concurrent.CompletionException;
import fr.cactus_industries.tools.messagesaving.MessageJsonTool;
import java.util.NoSuchElementException;
import org.javacord.api.entity.channel.ServerTextChannel;
import fr.cactus_industries.tools.RemakeRessources;
import java.io.IOException;
import fr.cactus_industries.tools.pdfreading.PDFReading;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.Message;
import java.util.concurrent.ExecutionException;
import fr.cactus_industries.tools.Tisstober;
import java.util.regex.Pattern;
import org.javacord.api.entity.channel.TextChannel;
import fr.cactus_industries.tools.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import java.awt.Color;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import java.util.Arrays;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MessageListener implements MessageCreateListener {
    
    private final String prefix;
    private final int pl;
    
    // Initialisation for command prefix
    public MessageListener(final String prefix) {
        this.prefix = prefix;
        this.pl = prefix.length();
    }
    
    public void onMessageCreate(MessageCreateEvent event) {
        
        String message = event.getMessageContent();
        
        if (message.startsWith(this.prefix)) {
            System.out.println("It's a command");
            message = message.substring(this.pl);
            String[] args = message.split(" +", 7); // Séparation des éléments de la commande jusqu'à 7
            System.out.println(args.length + ":" + args[0]);
            Arrays.stream(args).forEach(System.out::println);
            switch (args[0]) {
                /* ------------------------- /*
                    Command to get some info about the bot
                /* ------------------------- */
                case "info":
                    int nserver;
                    nserver = event.getApi().getServers().size();
                    new MessageBuilder().setEmbed(new EmbedBuilder().setAuthor("SpikyBot", "https://github.com/CactusKipic/SpikyBot", "https://i.imgur.com/VNgl2fU.jpg")
                            .setTitle("version " + this.getClass().getPackage().getImplementationVersion()).setColor(new Color(12756342))
                            .setDescription("SpikyBot is a small bot developed by CactusKipic.")
                            .addInlineField("SpikyBot is deployed on", "**" + nserver + "** servers")
                            .addInlineField("and manage", "**" + (event.getApi().getServers().stream()
                                    .map(Server::getMemberCount).reduce(0, Integer::sum) - nserver) + "** users"))
                            .send(event.getChannel());
                    break;
                case "say":
                    if (!Permissions.isAdmin(event.getMessageAuthor())) {
                        break;
                    }
                    if (args.length > 2) {
                        System.out.println("Message: " + message);
                        TextChannel tchan = event.getApi().getTextChannelById(Long.parseLong(args[1])).get();
                        Matcher i = Pattern.compile("^.*? \\d* (.*)$").matcher(message);
                        if (i.find()) {
                            new MessageBuilder().setContent(i.group(1)).send(tchan);
                        }
                        break;
                    }
                    break;
                case "tisstober": {
                    if (!Permissions.isAdmin(event.getMessageAuthor()))
                        break;
    
                    System.out.println("Tisstober");
                    if (args.length >= 2) {
                        switch (args[1]) {
                            case "force":
                                System.out.println("Forceday");
                                Tisstober.FireTask();
                                break;
                            case "forcehere":
                                System.out.println("Forcehere");
                                Tisstober.FakeTask(event.getChannel(), true);
                                break;
                            case "testday":
                                System.out.println("Testday");
                                Tisstober.FakeTask(event.getChannel(), false);
                                break;
                            case "reload":
                                new MessageBuilder().setContent(Tisstober.ReloadYml() ? "Fichier de config correctement rechargé !" : "Erreur lors du rechargement du fichier de config :exploding_head:")
                                        .send(event.getChannel());
                                break;
                            case "putreaction":
                                TextChannel chan = event.getApi().getTextChannelById((long) Tisstober.getConfig().get("chanID")).get();
                                try {
                                    chan.getMessages(150).get().forEach(m -> {
                                        if (m.getAuthor().isBotUser())
                                            m.addReaction("\ud83d\udc4d");
                                        else
                                            m.addReaction("\ud83d\udc96");
                                        return;
                                    });
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        break;
                    }
                    break;
                }
                case "ress": {
                    if (!Permissions.isAdmin(event.getMessageAuthor())) {
                        break;
                    }
                    if (args.length >= 2) {
                        String s4 = args[1];
                        switch (s4) {
                            case "test":
                                try {
                                    Message mess = event.getChannel().getMessagesBefore(1, event.getMessage()).get().last();
                                    mess.getContent();
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "pdf":
                                System.out.println("PDF !");
                                List<MessageAttachment> MAL;
                                if (args.length > 2) {
                                    MAL = event.getChannel().getMessageById(Long.parseLong(args[2])).join().getAttachments();
                                } else {
                                    MAL = event.getMessageAttachments();
                                }
                                if (MAL.size() <= 0) {
                                    break;
                                }
                                URL fileUrl = MAL.get(0).getUrl();
                                System.out.println((MAL.get(0)).getUrl().getPath() + "\nFile: " + (MAL.get(0)).getUrl().getFile());
                                if (fileUrl.getPath().endsWith(".pdf")) {
                                    try {
                                        PDFReading.sendPDFTextTo(fileUrl.openStream(), event.getChannel());
                                        System.out.println("Doc translation sent !");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                        }
                        break;
                    }
                    break;
                }
                case "getmess": {
                    if (!Permissions.isAdmin(event.getMessageAuthor())) {
                        break;
                    }
                    System.out.println("Get message " + args.length);
                    if (args.length >= 2) {
                        long mID = Long.parseLong(args[1]);
                        System.out.println("id " + mID);
                        Message mess = null;
                        try {
                            mess = event.getChannel().getMessageById(mID).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        new MessageBuilder().setContent("```"+mess.getEmbeds().get(0).getDescription().get()+"```").send(event.getChannel());
                        break;
                    }
                    break;
                }
                case "test": {
                    if (!Permissions.isAdmin(event.getMessageAuthor())) {
                        break;
                    }
                    Server server = event.getServer().get();
    
                    User user = event.getMessageAuthor().asUser().get();
    
                    List<Role> roles = server.getRoles(user);
    
                    event.getChannel().sendMessage("NB: " + roles.size());
                    
                    /*File img = new File("./images/chaine-youtube.png");
                    new MessageBuilder().setEmbed(new EmbedBuilder().setColor(new Color(6277341)).setTitle("__**Canva**__").setDescription("> Tr\u00e8s rapide et simple d'utilisation. \u00c9norm\u00e9ment de mod\u00e8les pour tout et n'importe quoi, d'une affiche \u00e0 une lettre en passant par un calendrier, ou encore une vid\u00e9o ou un CV, il en vaut le d\u00e9tour !\n> Seul b\u00e9mol, le tout est assez limit\u00e9 par rapport \u00e0 des logiciels sp\u00e9cialis\u00e9s. Aussi il vous sera impossible de modifier le fichier en dehors de canva.https://i.imgur.com/fhvX26A.png").setUrl("https://www.canva.com/fr_fr/")).send(event.getChannel());*/
                    break;
                }
                /* ------------------------- /*
                
                    Tickets commands
                
                /* ------------------------- /*
                    Command for adding a ticket
                /* ------------------------- */
                case "addticket": {
                    if (!Permissions.isAdmin(event.getMessageAuthor()))
                        break;
                    if (args.length <= 2) {
                        event.getChannel().sendMessage("Not enough arguments, usage:\naddticket <channelID> <messageStyle> [messageID] [color, image...]");
                        break;
                    }
                    long chanID;
                    try {
                        chanID = Long.parseLong(Character.isDigit(args[1].charAt(0)) ? args[1] : args[1].substring(2, args[1].length() - 1));
                    } catch (NumberFormatException e7) {
                        event.getChannel().sendMessage("Channel ID is not valid.");
                        break;
                    }
                    ServerTextChannel addChannel;
                    try {
                        addChannel = event.getServer().get().getTextChannelById(chanID).get();
                    } catch (NoSuchElementException e8) {
                        event.getChannel().sendMessage("Channel could not be found. (Verify that the bot is able to see it)");
                        break;
                    }
                    MessageJsonTool messTool = new MessageJsonTool();
                    System.out.println("Message type is " + args[2]);
                    // TODO Refaire la création de ticket avec commande #CDégueulasse (mais c'était pratique sur le coup, faut reconnaître)
                    switch (args[2]) {
                        case "embed": {
                            if (args.length > 3) {
                                Message mess2;
                                try {
                                    mess2 = event.getChannel().getMessageById(Long.parseLong(args[3])).join();
                                }
                                catch (CompletionException | NumberFormatException e) {
                                    event.getChannel().sendMessage("Error while reading given message by ID. Is the message ID valid ?");
                                    break;
                                }
                                messTool.haveEmbed(true);
                                messTool.setEmbTitle(mess2.getContent());
                                Color color = null;
                                String imageLink = null;
                                if (args.length >= 5) {
                                    System.out.println("1 option " + args[4]);
                                    if (args[4].matches("^#?[0-9A-Fa-f]{6}$")) {
                                        messTool.setEmbColor(args[4]);
                                    }
                                    else if (args[4].matches("(?i)^https?://.*\\.(png|jpg|gif)$")) {
                                        imageLink = args[4];
                                    }
                                }
                                if (args.length >= 6) {
                                    System.out.println("2 options " + args[5]);
                                    if (args[5].matches("^#?[0-9A-Fa-f]{6}$")) {
                                        System.out.println("Couleur");
                                        messTool.setEmbColor(args[5]);
                                    }
                                    else if (args[5].matches("(?i)^https?://.*\\.(png|jpg|gif)$")) {
                                        imageLink = args[5];
                                    }
                                }
                                if (imageLink != null) {
                                    messTool.setEmbImage(imageLink);
                                }
                            }
                            else {
                                messTool.haveEmbed(true);
                                messTool.setEmbTitle("Click on the reaction below to post a message on this channel.");
                            }
                            ChannelsTicketHandler.addTicketOnChannel(addChannel, messTool);
                            break;
                        }
                        default: {
                            event.getChannel().sendMessage("Please enter a valid message type. (Embed or normal)");
                            break;
                        }
                    }
                    break;
                }
                /* ------------------------- /*
                    Commands for update (Handled in a separate class)
                /* ------------------------- */
                case "updateticket": {
                    TicketUpdater.handleCommand(event, args);
                    break;
                }
                /* ------------------------- /*
                    Command for removing a ticket
                /* ------------------------- */
                case "removeticket": {
                    if (!Permissions.isAdmin(event.getMessageAuthor())) {
                        break;
                    }
                    if (args.length <= 1) {
                        event.getChannel().sendMessage("Not enough arguments, usage:\nremoveticket <channelID>");
                        break;
                    }
                    long delChanID;
                    try {
                        delChanID = Long.parseLong(Character.isDigit(args[1].charAt(0)) ? args[1] : args[1].substring(2, args[1].length() - 1));
                    }
                    catch (NumberFormatException e9) {
                        event.getChannel().sendMessage("Channel ID is not valid.");
                        break;
                    }
                    ServerTextChannel delTextChannel;
                    try {
                        delTextChannel = event.getServer().get().getTextChannelById(delChanID).get();
                    }
                    catch (NoSuchElementException e10) {
                        event.getChannel().sendMessage("Channel could not be found. (Verify that the bot is able to see it)");
                        break;
                    }
                    if (!ChannelsTicketHandler.isChannelTicketed(delTextChannel)) {
                        event.getChannel().sendMessage("This channel has no ticket.");
                        break;
                    }
                    if (ChannelsTicketHandler.deleteTicketOnChannel(delTextChannel)) {
                        event.getChannel().sendMessage("Ticket successfully deleted from channel.");
                        break;
                    }
                    break;
                }
                /* ------------------------- /*
                
                    Fin tickets commands
                
                /* ------------------------- */
                /* ------------------------- /*
                
                    Début PDFReading commands
                
                /* ------------------------- */
                case "pdfreading": {
                    PDFCommandHandler.handleCommand(event, args);
                    break;
                }
                /* ------------------------- /*
                
                    Fin PDFReading commands
                
                /* ------------------------- */
                /* ------------------------- /*
                    Command for reloading bot's configuration
                /* ------------------------- */
                case "reloadconf": {
                    if (!Permissions.isAdmin(event.getMessageAuthor())) {
                        break;
                    }
                    if (ConfigSpiky.init()) {
                        System.out.println("Config reloaded.");
                        new MessageBuilder().setContent("Config reloaded.").send(event.getChannel());
                        break;
                    }
                    System.out.println("Error while reloading config.");
                    new MessageBuilder().setContent("Error while reloading config.").send(event.getChannel());
                    break;
                }
                /* ------------------------- /*
                    Command for shutting down the bot
                /* ------------------------- */
                case "shutdown": {
                    if (!Permissions.isOwner(event.getMessageAuthor())) {
                        break;
                    }
                    if (event.getApi().getOwnerId() == event.getMessageAuthor().getId()) {
                        Main.Disconnect(event.getApi());
                        break;
                    }
                    break;
                }
            }
        } else if (event.getMessage().getContent().matches("(?i).*spikybot est le meilleur.*")) {
            event.getChannel().sendMessage("Vous avez complètement raison mon cher ! :wink:");
        }
        
        long chID = event.getChannel().getId();
        if (chID == (Long) Tisstober.getConfig().get("chanID")) {
            event.getMessage().addReaction(event.getMessageAuthor().isBotUser() ? "\ud83d\udc4d" : "\ud83d\udc96");
        } else if (ConfigSpiky.getConfigObj("PDF.chanList", new ArrayList().getClass()).contains(chID)) {
            List<MessageAttachment> att = event.getMessageAttachments();
            if (att.size() > 0 && att.get(0).getFileName().endsWith(".pdf")) {
                event.getMessage().addReaction("\ud83e\udd16");
            }
        }
    }
}
