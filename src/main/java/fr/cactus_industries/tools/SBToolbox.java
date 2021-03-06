package fr.cactus_industries.tools;

import java.util.NoSuchElementException;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

public class SBToolbox {
    
    // Récupération d'un salon donné en String pour le serveur passé
    public static ServerTextChannel getChannel(String str, Server server) {
        Long chanID = SBToolbox.getChannelID(str);
        if (chanID == null)
            return null;
        try {
            return server.getTextChannelById(chanID).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    
    // Récupération de l'ID d'un salon
    public static Long getChannelID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str.startsWith("<#") ? str.substring(2, 20) : str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid channel.");
        }
        return id;
    }
    
    // Récupération de l'ID d'un utilisateur
    public static Long getUserID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str.startsWith("<@!") ? str.substring(3, 21) : str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid channel.");
        }
        return id;
    }
    
    // Récupération de l'ID d'un rôle
    public static Long getRoleID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str.startsWith("<@&") ? str.substring(3, 21) : str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid role.");
        }
        return id;
    }
    
    // Récupération de l'ID d'un message (simple cast en long)
    public static Long getMessageID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid message ID.");
        }
        return id;
    }
}
