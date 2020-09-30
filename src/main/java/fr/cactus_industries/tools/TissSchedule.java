package fr.cactus_industries.tools;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.Calendar;
import java.util.TimerTask;

public class TissSchedule extends TimerTask {
    
    private DiscordApi api;
    private TextChannel tchan;
    private boolean auto;
    
    public TissSchedule(DiscordApi api, TextChannel tchan, boolean auto){
        this.api = api;
        this.tchan = tchan;
        this.auto = auto;
    }
    
    @Override
    public void run() {
        System.out.println("Message auto !!!");
        Calendar calendar = Calendar.getInstance();
        String currentDay = "" + calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("Jour: "+currentDay + "Name: "
                +Tisstober.getConfigString("header"));
        new MessageBuilder().setEmbed(new EmbedBuilder().setColor(new Color(0xF77F5C)).setTitle("**Jour "+currentDay+"**")
                .setUrl(Tisstober.getConfigString("annoncelink"))
                .setDescription(Tisstober.getConfigString("header").replace("%",
                        "**"+Tisstober.getConfigString("d"+currentDay+".name")+"**")+
                        "\n"+Tisstober.getConfigString("d"+currentDay+".desc")+
                        Tisstober.getConfigString("footer"))
                .setFooter((String) Tisstober.getConfigString("embedfooter"), (String) Tisstober.getConfigString("embedfooterimage"))
                .setThumbnail((String) Tisstober.getConfigString("imagelink"))).send(tchan);
        if(auto){
            Tisstober.ScheduleNext();
        }
    }
}
