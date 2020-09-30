package fr.cactus_industries.tools;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;

public class Permissions {
    
    public static boolean isAdmin(MessageAuthor user){
        return user.isServerAdmin() || user.isBotOwner();
    }
    
    public static boolean isOwner(MessageAuthor user){
        return user.isBotOwner();
    }
    
    
}
