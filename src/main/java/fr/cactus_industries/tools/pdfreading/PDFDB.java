package fr.cactus_industries.tools.pdfreading;

import fr.cactus_industries.dbInterface.GenericDBInteractions;
import org.javacord.api.entity.channel.ServerTextChannel;

import java.util.ArrayList;
import java.util.HashMap;

import static fr.cactus_industries.dbInterface.GenericDBInteractions.*;

public class PDFDB {
    
    public static boolean addPDFReadingToChannel(ServerTextChannel chan){
        return addPDFReadingToChannel(chan.getServer().getId(), chan.getId());
    }
    
    public static boolean addPDFReadingToChannel(long serverID, long chanID){
        String query = "INSERT INTO PDFReadingChannels (Server, Channel) VALUES ('" + serverID + "','" + chanID + "');";
        
        return executeInsertUpdateDeleteStatement(query);
    }
    
    public static boolean deletePDFReadingToChannel(ServerTextChannel chan){
        return deletePDFReadingToChannel(chan.getServer().getId(), chan.getId());
    }
    
    public static boolean deletePDFReadingToChannel(long serverID, long chanID){
        String query = "DELETE FROM PDFReadingChannels WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        return executeInsertUpdateDeleteStatement(query);
    }
    
    public static Boolean setGrantLevelOnChannel(ServerTextChannel chan, int level){
        return setGrantLevelOnChannel(chan.getServer().getId(), chan.getId(), level);
    }
    
    public static Boolean setGrantLevelOnChannel(long serverID, long chanID, int level){
        String query = "UPDATE TicketsChannel SET GrantLevel='" + level + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
    
        return GenericDBInteractions.executeInsertUpdateDeleteStatement(query);
    }
    
    public static HashMap<Long, ArrayList<Long>> getAllPDFReadingOnChannel(){
        String query = "SELECT Server, Channel FROM PDFReadingChannels";
    
        return executeGetChannelsFromAllServer(query);
    }

}
