package fr.cactus_industries.tisseurs;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class BumperListener implements MessageCreateListener {
    
    private static SimpleDateFormat SDF = new SimpleDateFormat("MM/yy");
    
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageAuthor().isBotUser()){
            log.info("C'est un bot");
            // Est-ce que c'est Tissrobot ?
            if(event.getMessageAuthor().getId() == 302050872383242240L){
                log.info("C'est Tissrobot");
                List<Embed> embeds = event.getMessage().getEmbeds();
                if(embeds.size() > 0) {
                    log.info("Y'a un embed");
                    if(embeds.get(0).getDescription().isPresent()) {
                        log.info("Y'a une desc");
                        String str = embeds.get(0).getDescription().get();
                        Pattern pattern = Pattern.compile("<@!?([0-9]*)>");
                        Calendar cal = Calendar.getInstance();
                        // System.out.println(str);
                        if(str.matches("(?s)<@!?[0-9]*>.*? :thumbsup:.*?https://disboard.org/.*")){
                            log.info("Ca bump");
                            long id = Long.parseLong(str.substring(str.indexOf('@') + 1, str.indexOf('>')));
                            BumperDB.addPointsTo(id, cal, 1);
                            //TODO Ajout d'un point
                            new MessageBuilder().setEmbed(getScoreboardEmbed(event.getApi(), cal))
                                .send(event.getChannel());
                        }
                        if(str.matches("<@!?[0-9]*>.*? [0-9]{1,3} .*")){
                            log.info("Ca bump mal");
                            long id = Long.parseLong(str.substring(str.indexOf('@') + 1, str.indexOf('>')));
                            BumperDB.addPointsTo(id, cal, -0.25);
                            //TODO Retrait de point
                            new MessageBuilder().setEmbed(getScoreboardEmbed(event.getApi(), cal))
                                    .send(event.getChannel());
                        }
                        log.info("Fin");
                    }
                }
            }
        } else {
            String msg = event.getMessage().getContent();
            if(msg.toLowerCase(Locale.ROOT).startsWith("!!scoreboard")){
                Calendar cal = Calendar.getInstance();
                new MessageBuilder().setEmbed(getScoreboardEmbed(event.getApi(), cal))
                        .send(event.getChannel());
                
            }
        }
    }
    
    public static EmbedBuilder getScoreboardEmbed(DiscordApi api, Calendar cal) {
        LinkedHashMap<Long, Double> scoreboard = BumperDB.getScoreboard(cal);
        ArrayList<String> listJoueurs = new ArrayList<>(), listScores = new ArrayList<>();
        scoreboard.forEach((jid, score) -> {
            listJoueurs.add(api.getUserById(jid).join().getName());
            listScores.add("" + score);
        });
        String scores = String.join("\n", listScores);
        String joueurs = String.join("\n", listJoueurs);
        if(scores.equals(""))
            scores = "0";
        if(joueurs.equals(""))
            joueurs = "Aucun participant";
        return new EmbedBuilder().setTitle("Bump scoreboard du " + SDF.format(cal.getTime())).addInlineField("Joueur", joueurs)
                .addInlineField("Score", scores).setDescription("Mais qui donnera des gages ?!");
    }
}
