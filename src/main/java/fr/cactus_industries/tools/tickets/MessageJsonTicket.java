package fr.cactus_industries.tools.tickets;

import fr.cactus_industries.tools.messagesaving.MessageJsonTool;
import org.javacord.api.entity.message.component.ButtonStyle;

import java.awt.*;

public class MessageJsonTicket extends MessageJsonTool {
    
    public MessageJsonTicket(){
    }
    
    public MessageJsonTicket init(){
        this.setEmbed(true);
        this.setEmbTitle("Default ticket");
        this.setEmbDesc("Click on the button below to post a message here.");
        this.setEmbColor(new Color(0, 193, 64));
        this.getButtonList().add(new ButtonJson("spiky:ticket", ButtonStyle.PRIMARY, "Take a ticket", "\uD83C\uDF9F"));
        return this;
    }
    
}
