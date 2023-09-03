package fr.cactus_industries.tools.ressources;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
public class Ressource {
    
    private String Title = null;
    private String Link = null;
    private ArrayList<String> LinkList = new ArrayList<>();
    private ArrayList<String> TextList = new ArrayList<>();
    
    public Ressource(String content) {
        Matcher matcher = Pattern.compile("(?i)^Titre *?: *?(.*)$|^(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*\\.[a-zA-Z][a-zA-Z]+(?:/[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*)?)$|^(.*)$", Pattern.MULTILINE).matcher(content);
        matcher.results().forEach(matchResult -> {
            String title = matchResult.group(1);
            String httpres = matchResult.group(2);
            String txtres = matchResult.group(3);
            if (txtres != null) {
                this.TextList.add(txtres);
            } else if (httpres != null) {
                if (this.Link == null) {
                    this.Link = httpres;
                } else {
                    this.TextList.add(httpres);
                }
            } else if (title != null && this.Title == null) {
                this.Title = title;
            }
            log.info("Match");
            log.info("Http: " + httpres + "\nTxt: " + txtres + "\nTitre: " + title);
        });
        if (this.Link != null) {
            try {
                URL url = new URL(this.LinkList.get(0));
                Document doc = Jsoup.connect(this.LinkList.get(0)).get();
                this.Title = doc.title();
            }
            catch (MalformedURLException e) {
                matcher = Pattern.compile("https?://(?:www\\.)?(.*?)(?:/.*)?", Pattern.CASE_INSENSITIVE).matcher(this.Link);
                this.Title = matcher.results().findFirst().get().group(1);
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Ressource(List<String> links, List<String> text) {
        this.LinkList.addAll(links);
        this.TextList.addAll(text);
        if (!this.LinkList.isEmpty()) {
            try {
                URL url = new URL(this.LinkList.get(0));
                Document doc = Jsoup.connect(this.LinkList.get(0)).get();
                this.Title = doc.title();
            }
            catch (MalformedURLException e) {
                Matcher matcher = Pattern.compile("https?://(?:www\\.)?(.*?)(?:/.*)?", Pattern.CASE_INSENSITIVE).matcher(this.LinkList.get(0));
                this.Title = matcher.results().findFirst().get().group(1);
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public MessageBuilder makeEmbed() {
        MessageBuilder MB = new MessageBuilder();
        EmbedBuilder EB = new EmbedBuilder().setTitle("__**" + this.Title + "**__").setUrl(this.LinkList.get(0));
        String Description = this.TextList.stream().map(line -> "> " + line).reduce((str1, str2) -> str1 + "\n" + str2).get();
        log.info("Desc\n" + Description + "\n");
        if (this.LinkList.size() > 1) {
            Description = Description + "\n";
        }
        for (int i = 1; i < this.LinkList.size(); ++i) {
            Description = Description + "\n" + this.LinkList.get(i);
        }
        EB.setDescription(Description);
        return MB.setEmbed(EB);
    }
}
