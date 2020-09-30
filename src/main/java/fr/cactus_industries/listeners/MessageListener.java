package fr.cactus_industries.listeners;

import fr.cactus_industries.Main;
import fr.cactus_industries.tools.Permissions;
import fr.cactus_industries.tools.Tisstober;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.io.File;

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
                case "tisstober":
                    if(!Permissions.isAdmin(event.getMessageAuthor()))
                        break;
                    System.out.println("Tisstober");
                    if(!(args.length<2)){
                        switch (args[1]){
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
                                new MessageBuilder().setContent(Tisstober.ReloadYml()?
                                        "Fichier de config correctement rechargé !"
                                        :"Erreur lors du rechargement du fichier de config :exploding_head:").send(event.getChannel());
                                ;
                                break;
                        }
                    }
                    break;
                case "test":
                    if(!Permissions.isAdmin(event.getMessageAuthor()))
                        break;
                    File img = new File("./images/chaine-youtube.png");
                    
                    new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setColor(new Color(0x5fc8dd))
                            .setTitle("__**Canva**__")
                            .setDescription("Très rapide et simple d'utilisation. Énormément de modèles pour tout et n'importe quoi, d'une affiche à une lettre en passant par un calendrier, ou encore une vidéo ou un CV, il en vaut le détour !\n" +
                                    "Seul bémol, le tout est assez limité par rapport à des logiciels spécialisés. Aussi il vous sera impossible de modifier le fichier en dehors de canva.")
                            .setUrl("https://www.canva.com/fr_fr/"))
                            .send(event.getChannel());
                    
                    break;
                case "shutdown":
                    if(!Permissions.isOwner(event.getMessageAuthor()))
                        break;
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
