package fr.cactus_industries.tools.ressources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageBuilder;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class RemakeRessources {
    
    private Map<String, Object> config = null;
    
    public static MessageBuilder parseMessage(String message) {
        Matcher matcher = Pattern.compile("(?i)^(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*\\.[a-zA-Z][a-zA-Z]+(?:/[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*)?)$|^(.*)$", 8).matcher(message);
        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> text = new ArrayList<>();
        matcher.results().forEach(matchResult -> {
            String httpres = matchResult.group(1);
            String txtres = matchResult.group(2);
            if (httpres != null) {
                links.add(httpres);
            }
            if (txtres != null) {
                text.add(txtres);
            }
            log.info("Match");
            log.info("Http: " + httpres + "\nTxt: " + txtres);
        });
        Ressource ress = new Ressource(links, text);
        return ress.makeEmbed();
    }
    
    public <T> T getConfigValue(String path, Class<T> type) {
        String[] list = path.split("\\.");
        Object o = null;
        Map map = this.config;
        for (String s : list) {
            o = map.get(s);
            if (o == null) {
                return null;
            }
            if (!(o instanceof Map)) continue;
            map = (Map) o;
        }
        if (type.isInstance(o)) {
            return (T) o;
        }
        return null;
    }
    
    private void loadConfig() {
        File f = new File("./ressources.yml");
        if (!f.exists()) {
            log.info("Config file for ressources not found !");
            return;
        }
        try {
            this.config = new Yaml().load(new FileInputStream(f));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info("Could not load ressources config.");
        }
    }
}
