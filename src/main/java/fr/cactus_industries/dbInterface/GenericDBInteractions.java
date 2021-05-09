package fr.cactus_industries.dbInterface;

import fr.cactus_industries.DBInterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class GenericDBInteractions {
    
    public static boolean executeInsertUpdateDeleteStatement(String query){
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DE L'UPDATE
                return stmt.executeUpdate(query) != 0; // RENVOIE VRAI SI UNE DONNEE A ETE MAJ
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR LORS DE L'UPDATE
        return false;
    }
    
    public static boolean dataExistStatement(String query){
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){ // RENVOIE VRAI POUR UNE DONNEE TROUVÉE (ou plus)
                    return true;
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR
        return false;
    }
    
    public static HashMap<Long, ArrayList<Long>> executeGetChannelsFromAllServer(String query){
        Connection con = DBInterface.getDBConnection();
    
        HashMap<Long, ArrayList<Long>> serverChannel = new HashMap<>();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
            
                while (rs.next()) {
                    long server = rs.getLong("Server");
                    if (!serverChannel.containsKey(server)) {
                        ArrayList<Long> channelList = new ArrayList<>();
                        channelList.add(rs.getLong("Channel"));
                        serverChannel.put(server, channelList);
                    } else {
                        serverChannel.get(server).add(rs.getLong("Channel"));
                    }
                }
                return serverChannel;
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
}
