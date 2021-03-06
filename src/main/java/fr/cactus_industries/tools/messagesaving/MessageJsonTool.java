package fr.cactus_industries.tools.messagesaving;

import java.awt.Color;
import java.util.NoSuchElementException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class MessageJsonTool {
    
    private String content = null;
    private Boolean tts = null;
    private boolean embed = false;
    private String embTitle = null;
    private String embDesc = null;
    private String embLink = null;
    private Color embColor = null;
    private String embImage = null;
    private Long embAuthor = null;
    private String embFooter = null;
    private String embFootImg = null;
    private String embThumb = null;
    private String embTStamp = null;
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTts(Boolean tts) {
        this.tts = tts;
    }
    
    public void setEmbTitle(String embTitle) {
        this.embTitle = embTitle;
    }
    
    public void haveEmbed(boolean embed) {
        this.embed = embed;
    }
    
    public void setEmbDesc(String embDesc) {
        this.embDesc = embDesc;
    }
    
    public void setEmbLink(String embLink) {
        this.embLink = embLink;
    }
    
    public boolean setEmbColor(String hexColor) {
        if (hexColor == null) {
            this.embColor = null;
            return true;
        }
        if ((hexColor).charAt(0) != '#') {
            hexColor = "#" + hexColor;
        }
        try {
            if ((hexColor).matches("^#[0-9A-Fa-f]{6}$")) {
                this.embColor = Color.decode(hexColor);
            }
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void setEmbColor(Color embColor) {
        this.embColor = embColor;
    }
    
    public void setEmbImage(String embImage) {
        this.embImage = embImage;
    }
    
    public void setEmbAuthor(Long embAuthor) {
        this.embAuthor = embAuthor;
    }
    
    public void setEmbFooter(String embFooter) {
        this.embFooter = embFooter;
    }
    
    public void setEmbFootImg(String embFootImg) {
        this.embFootImg = embFootImg;
    }
    
    public void setEmbThumb(String embThumb) {
        this.embThumb = embThumb;
    }
    
    public void setEmbTStamp(String embTStamp) {
        this.embTStamp = embTStamp;
    }
    
    public MessageBuilder create() {
        MessageBuilder mB = new MessageBuilder();
    
        if (this.content != null)
            mB.setContent(this.content);
        if (this.tts != null)
            mB.setTts(this.tts);
    
        if (this.embed) {
            EmbedBuilder eB = new EmbedBuilder();
            if (this.embTitle != null)
                eB.setTitle(this.embTitle);
            if (this.embDesc != null)
                eB.setDescription(this.embDesc);
            if (this.embLink != null)
                eB.setUrl(this.embLink);
            if (this.embColor != null)
                eB.setColor(this.embColor);
            if (this.embImage != null)
                eB.setImage(this.embImage);
            
            if (this.embFooter != null) {
                if (this.embFootImg != null)
                    eB.setFooter(this.embFooter, this.embFootImg);
                else
                    eB.setFooter(this.embFooter);
            }
            if (this.embThumb != null)
                eB.setThumbnail(this.embThumb);
            if (this.embTStamp != null)
                eB.setTimestampToNow();
    
            mB.setEmbed(eB);
        }
        return mB;
    }
    
    public MessageBuilder create(DiscordApi api) {
        MessageBuilder mB = new MessageBuilder();
        
        if (this.content != null)
            mB.setContent(this.content);
        if (this.tts != null)
            mB.setTts(this.tts);
        
        if (this.embed) {
            EmbedBuilder eB = new EmbedBuilder();
            if (this.embTitle != null)
                eB.setTitle(this.embTitle);
            if (this.embDesc != null)
                eB.setDescription(this.embDesc);
            if (this.embLink != null)
                eB.setUrl(this.embLink);
            if (this.embColor != null)
                eB.setColor(this.embColor);
            if (this.embImage != null)
                eB.setImage(this.embImage);
            if (this.embAuthor != null)
                try {
                    User user = api.getUserById(this.embAuthor).join();
                    eB.setAuthor(user);
                }
                catch (NoSuchElementException e) {
                    System.out.println("User not found while creating an embed.");
                }
            
            if (this.embFooter != null) {
                if (this.embFootImg != null)
                    eB.setFooter(this.embFooter, this.embFootImg);
                else
                    eB.setFooter(this.embFooter);
            }
            if (this.embThumb != null)
                eB.setThumbnail(this.embThumb);
            if (this.embTStamp != null)
                eB.setTimestampToNow();
            
            mB.setEmbed(eB);
        }
        return mB;
    }
}

