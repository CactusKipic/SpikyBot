package fr.cactus_industries.tools.messagesaving;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import fr.cactus_industries.tools.CustomColor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

@Getter
@Slf4j
public class MessageJsonTool implements Serializable {
    
    private String content = null;
    private Boolean tts = null;
    private boolean embed = false;
    // EMBED
    private String embTitle = null;
    private String embDesc = null;
    private String embLink = null;
    private CustomColor embColor = null;
    private String embImage = null;
    private Long embAuthor = null;
    private String embFooter = null;
    private String embFootImg = null;
    private String embThumb = null;
    private Boolean embTStamp = null;
    // Boutons
    private ArrayList<ButtonJson> buttonList = new ArrayList<>();
    
    public MessageJsonTool() {
    }
    
    public MessageJsonTool(String content, Boolean tts, boolean embed, String embTitle, String embDesc, String embLink,
                           CustomColor embColor, String embImage, Long embAuthor, String embFooter, String embFootImg,
                           String embThumb, Boolean embTStamp, ArrayList<ButtonJson> buttonList) {
        this.content = content;
        this.tts = tts;
        this.embed = embed;
        this.embTitle = embTitle;
        this.embDesc = embDesc;
        this.embLink = embLink;
        this.embColor = embColor;
        this.embImage = embImage;
        this.embAuthor = embAuthor;
        this.embFooter = embFooter;
        this.embFootImg = embFootImg;
        this.embThumb = embThumb;
        this.embTStamp = embTStamp;
        this.buttonList = buttonList;
    }
    
    public void setButtonList(ArrayList<ButtonJson> buttonList) {
        this.buttonList = buttonList;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTts(Boolean tts) {
        this.tts = tts;
    }
    
    public void setEmbTitle(String embTitle) {
        this.embTitle = embTitle;
    }
    
    public boolean setEmbed() {
        return this.embed;
    }
    
    public void setEmbed(boolean embed) {
        this.embed = embed;
        if(!this.embed){
            this.embTitle = null;
            this.embDesc = null;
            this.embLink = null;
            this.embColor = null;
            this.embImage = null;
            this.embAuthor = null;
            this.embFooter = null;
            this.embFootImg = null;
            this.embThumb = null;
            this.embTStamp = null;
        } else {
            this.embTitle = "Default embed title";
        }
    }
    
    public int globalEmbedLength() {
        return (this.embTitle == null ? 0 : this.embTitle.length()) + (this.embDesc == null ? 0 : this.embDesc.length()) + (this.embFooter == null ? 0: this.embFooter.length());
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
                this.embColor = CustomColor.fromColor(Color.decode(hexColor));
            }
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void setEmbColor(CustomColor embColor) {
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
    
    public void setEmbTStamp(Boolean embTStamp) {
        this.embTStamp = embTStamp;
    }
    
    public MessageBuilder create(DiscordApi api) {
        MessageBuilder mB = new MessageBuilder();
        
        if (this.content != null)
            mB.setContent(this.content);
        if (this.tts != null)
            mB.setTts(this.tts);
        
        
        // Création de l'embed (si existant)
        if (this.embed) {
            EmbedBuilder eB = new EmbedBuilder();
            if (this.embTitle != null)
                eB.setTitle(this.embTitle);
            if (this.embDesc != null)
                eB.setDescription(this.embDesc);
            if (this.embLink != null)
                eB.setUrl(this.embLink);
            if (this.embColor != null)
                eB.setColor(this.embColor.toColor());
            if (this.embImage != null)
                eB.setImage(this.embImage);
            if (this.embAuthor != null)
                try {
                    User user = api.getUserById(this.embAuthor).join();
                    eB.setAuthor(user);
                }
                catch (NoSuchElementException e) {
                    log.info("User not found while creating an embed.");
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
        if(buttonList.size()>0)
            mB.addComponents(ActionRow.of(
                    buttonList.stream().map(b -> b.create(api)).collect(Collectors.toList())));
        
        return mB;
    }
    
    public static class ButtonJson implements Serializable {
        
        private String id;
        private ButtonStyle style;
        private String label;
        private String emoji;
    
        public ButtonJson(String id, ButtonStyle style, String label, String emoji) {
            this.id = id;
            this.style = style;
            this.label = label;
            this.emoji = emoji;
        }
    
        public Button create(DiscordApi api){
            if(emoji == null)
                return Button.create(id, style, label);
            if(emoji.matches("^\\d{17,19}$")){
                KnownCustomEmoji emoji = api.getCustomEmojiById(this.emoji).orElse(null);
                return emoji == null ? Button.create(id, style, label, "\uD83C\uDF9F") : Button.create(id, style, label, emoji);
            }
            return Button.create(id, style, label, emoji);
        }
    
        public String getId() {
            return id;
        }
    
        public void setId(String id) {
            this.id = id;
        }
    
        public ButtonStyle getStyle() {
            return style;
        }
    
        public void setStyle(ButtonStyle style) {
            this.style = style;
        }
    
        public String getLabel() {
            return label;
        }
    
        public void setLabel(String label) {
            this.label = label;
        }
    
        public String getEmoji() {
            return emoji;
        }
        
        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }
    }
}

