package fr.cactus_industries.tools.pdfreading;

import fr.cactus_industries.dbInterface.GenericDBInteractions;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;

import java.util.ArrayList;
import java.util.HashMap;

import static fr.cactus_industries.dbInterface.GenericDBInteractions.*;

@Deprecated
public class PDFDB {
    
    public static boolean addPDFReadingToChannel(ServerTextChannel chan){
        if (addPDFReadingToChannel(chan.getServer().getId(), chan.getId())) {
            chan.addReactionAddListener(PDFReactionListener.getInstance());
            chan.addMessageCreateListener(PDFMessageListener.getInstance());
            return true;
        }
        return false;
    }
    
    public static boolean addPDFReadingToChannel(long serverID, long chanID){
        String query = "INSERT INTO t_pdfreading_channel (Server, Channel) VALUES ('" + serverID + "','" + chanID + "');";
        
        return executeInsertUpdateDeleteStatement(query);
    }
    
    public static boolean deletePDFReadingToChannel(ServerTextChannel chan){
        if (deletePDFReadingToChannel(chan.getServer().getId(), chan.getId())) {
            chan.removeListener(PDFReactionListener.class, PDFReactionListener.getInstance());
            chan.removeListener(PDFMessageListener.class, PDFMessageListener.getInstance());
            return true;
        }
        return false;
    }
    
    public static boolean deletePDFReadingToChannel(long serverID, long chanID){
        String query = "DELETE FROM t_pdfreading_channel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        return executeInsertUpdateDeleteStatement(query);
    }
    
    public static boolean isChannelOnPDFReading(ServerTextChannel chan){
        return isChannelOnPDFReading(chan.getServer().getId(), chan.getId());
    }
    
    public static boolean isChannelOnPDFReading(long serverID, long chanID){
        String query = "SELECT Channel FROM t_pdfreading_channel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        return dataExistStatement(query);
    }
    
    public static Integer getNumberOfChannelOnPDFReading(Server server){
        return getNumberOfChannelOnPDFReading(server.getId());
    }
    
    public static Integer getNumberOfChannelOnPDFReading(long serverID){
        String query = "SELECT Channel FROM t_pdfreading_channel WHERE Server='" + serverID + "';";
        
        return dataCountStatement(query);
    }
    
    public static Boolean setGrantLevelOnChannel(ServerTextChannel chan, int level){
        return setGrantLevelOnChannel(chan.getServer().getId(), chan.getId(), level);
    }
    
    public static Boolean setGrantLevelOnChannel(long serverID, long chanID, int level){
        String query = "UPDATE t_pdfreading_channel SET GrantLevel='" + level + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
    
        return GenericDBInteractions.executeInsertUpdateDeleteStatement(query);
    }
    
    public static HashMap<Long, ArrayList<Long>> getAllPDFReadingOnChannel(){
        String query = "SELECT Server, Channel FROM t_pdfreading_channel";
    
        return executeGetChannelsFromAllServer(query);
    }

}
