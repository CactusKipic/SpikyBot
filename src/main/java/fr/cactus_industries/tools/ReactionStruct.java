package fr.cactus_industries.tools;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import com.google.gson.Gson;
import java.io.File;
import java.util.Hashtable;
import java.io.Serializable;

public class ReactionStruct implements Serializable {
    
    private Hashtable<Integer, Hashtable<Long, Long>> reactionMessage;
    private static ReactionStruct cela;
    
    private ReactionStruct() {
        this.reactionMessage = null;
        this.reactionMessage = new Hashtable<>();
    }
    
    public static void load() {
        final File f = new File("./ReactionStruct.json");
        if (!f.exists()) {
            ReactionStruct.cela = new ReactionStruct();
            return;
        }
        final Gson gson = new Gson();
        try {
            ReactionStruct.cela = gson.fromJson(new FileReader(f), ReactionStruct.class);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static ReactionStruct getCela() {
        if (ReactionStruct.cela == null) {
            load();
        }
        return ReactionStruct.cela;
    }
    
    public Hashtable<Long, Long> getReactionMessage(final int day) {
        this.reactionMessage.computeIfAbsent(day, k -> new Hashtable<>());
        return this.reactionMessage.get(day);
    }
    
    public static Long getMessageReaction(final long discordID, final int day) {
        return getCela().getReactionMessage(day).get(discordID);
    }
    
    public static void setReactionMessage(final long discordID, final long messageID, final int day) {
        getCela().getReactionMessage(day).put(discordID, messageID);
        save();
    }
    
    public static void save() {
        System.out.println("Saving votes...");
        final Gson gson = new Gson();
        final File f = new File("./ReactionStruct.json");
        try {
            final FileOutputStream fw = new FileOutputStream(f);
            fw.write(gson.toJson(getCela()).getBytes());
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
