package fr.cactus_industries.tools;

import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;

@Slf4j
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
            log.info("Invalid channel.");
        }
        return id;
    }
    
    // Récupération de l'ID d'un utilisateur
    public static Long getUserID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str.startsWith("<@!") ? str.substring(3, 21) : str);
        } catch (NumberFormatException e) {
            log.info("Invalid channel.");
        }
        return id;
    }
    
    // Récupération d'un rôle d'un serveur
    public static Role getRole(String str, Server server){
        Long roleID = SBToolbox.getRoleID(str);
        if (roleID == null)
            return null;
        try {
            return server.getRoleById(roleID).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    
    // Récupération de l'ID d'un rôle
    public static Long getRoleID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str.startsWith("<@&") ? str.substring(3, 21) : str);
        } catch (NumberFormatException e) {
            log.info("Invalid role.");
        }
        return id;
    }
    
    // Récupération de l'ID d'un message (simple cast en long)
    public static Long getMessageID(String str) {
        Long id = null;
        try {
            id = Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.info("Invalid message ID.");
        }
        return id;
    }
}
