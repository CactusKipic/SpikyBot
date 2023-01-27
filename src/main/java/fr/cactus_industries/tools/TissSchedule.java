package fr.cactus_industries.tools;

import java.awt.Color;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import java.util.Calendar;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.DiscordApi;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class TissSchedule extends TimerTask {
    
    private DiscordApi api;
    private TextChannel tchan;
    private boolean auto;
    
    public TissSchedule(final DiscordApi api, final TextChannel tchan, final boolean auto) {
        this.api = api;
        this.tchan = tchan;
        this.auto = auto;
    }
    
    @Override
    public void run() {
        System.out.println("Message auto !!!");
        Calendar calendar = Calendar.getInstance();
        String currentDay = "" + calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("Jour: " + currentDay + "Name: " + Tisstober.getConfigString("header"));
        CompletableFuture<Message> send = new MessageBuilder().setEmbed(new EmbedBuilder().setColor(new Color(16219996))
                .setTitle("**Jour " + currentDay + "**")
                .setUrl(Tisstober.getConfigString("annoncelink"))
                .setDescription(Tisstober.getConfigString("header")
                        .replace("%", "**" + Tisstober.getConfigString("d" + currentDay + ".name") + "**")
                        + "\n" + Tisstober.getConfigString("d" + currentDay + ".desc") + Tisstober.getConfigString("footer"))
                .setFooter(Tisstober.getConfigString("embedfooter"), Tisstober.getConfigString("embedfooterimage"))
                .setThumbnail(Tisstober.getConfigString("imagelink"))).send(this.tchan);
        send.join().addReaction("\ud83d\udc4d");
        if (this.auto) {
            Tisstober.ScheduleNext();
        }
    }
}
