package fr.cactus_industries.tools;

import org.javacord.api.entity.message.MessageAuthor;

public class Permissions {
    
    // Check if the MessageAuthor is Admin or is the bot owner
    public static boolean isAdmin(MessageAuthor user){
        return user.isServerAdmin() || user.isBotOwner();
    }
    
    // Check if the MessageAuthor is the bot owner
    public static boolean isOwner(MessageAuthor user){
        return user.isBotOwner();
    }
    
    
}