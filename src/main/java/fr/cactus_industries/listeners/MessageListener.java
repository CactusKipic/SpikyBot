package fr.cactus_industries.listeners;

import fr.cactus_industries.Main;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

public class MessageListener implements MessageCreateListener {
    
    private final String prefix;
    private final int pl;
    
    public MessageListener(String prefix){
        this.prefix = prefix;
        pl = prefix.length();
    }
    
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String message = event.getMessageContent();
        
        if(message.startsWith(prefix)){
            System.out.println("It's a command");
            message = message.substring(pl);
            String[] args = message.split(" ", 5);
            System.out.println(args[0]);
            switch (args[0]){
                case "info":
                    int nserver = 0;
                    new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setAuthor("SpikyBot","https://github.com/CactusKipic/SpikyBot","https://i.imgur.com/VNgl2fU.jpg")
                            .setTitle("version "+getClass().getPackage().getImplementationVersion())
                            .setColor(new Color(0xC2A576)) // Couleur: #C2A576
                            .setDescription("SpikyBot is a small bot developed by CactusKipic.")
                            .addInlineField("SpikyBot is deployed on", "**"+(nserver = event.getApi().getServers().size())+"** servers")
                            .addInlineField("and manage",
                                    "**"+(event.getApi().getServers().stream().map(Server::getMemberCount).reduce(0, Integer::sum)-nserver)+"** users")
                    ).send(event.getChannel());
                    break;
                case "shutdown":
                    if(event.getApi().getOwnerId() == event.getMessageAuthor().getId())
                        Main.Disconnect(event.getApi());
                    break;
            }
        } else{
            
            if(event.getMessage().getContent().matches("(?i).*spikybot est le meilleur.*")){
                event.getChannel().sendMessage("Vous avez complètement raison mon cher ! :wink:");
            }
        }
        
    }
}
