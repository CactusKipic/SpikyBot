package fr.cactus_industries.tools;

import fr.cactus_industries.DBInterface;
import fr.cactus_industries.dbInterface.GenericDBInteractions;
import org.javacord.api.entity.server.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PremiumServers {
    
    public static boolean addPremiumServer(Server server, String date) {
        return addPremiumServer(server.getId(), date);
    }
    
    public static boolean addPremiumServer(long serverID, String date) {
        String query = "INSERT INTO PremiumServer (Server, EndSub) VALUES ('" + serverID + "', '" + date + "');";
        
        Connection con = DBInterface.getDBConnection();
        
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DE L'INSERT
                if(stmt.executeUpdate(query) != 0)
                    return true;
                
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // RIEN N'A ETE INSÉRÉ
        return false;
    }
    
    public static boolean isServerPremium(Server server) {
        return isServerPremium(server.getId());
    }
    
    public static boolean isServerPremium(long serverID) {
        String query = "SELECT Server FROM PremiumServer WHERE Server='" + serverID + "';";
        
        return GenericDBInteractions.dataExistStatement(query);
    }
}
