package fr.cactus_industries.tools;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class Permissions {
    
    // Check if the MessageAuthor is Admin or is the bot owner
    public static boolean isAdmin(MessageAuthor user){
        return user.isServerAdmin() || user.isBotOwner();
    }
    
    // Check if the MessageAuthor is Admin or is the bot owner
    public static boolean isAdmin(User user, Server server){
        return server.isAdmin(user) || user.isBotOwner();
    }
    
    // Check if the MessageAuthor is the bot owner
    public static boolean isOwner(MessageAuthor user){
        return user.isBotOwner();
    }
    
    
}
