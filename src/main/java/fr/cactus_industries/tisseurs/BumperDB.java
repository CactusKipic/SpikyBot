package fr.cactus_industries.tisseurs;

import fr.cactus_industries.DBInterface;
import fr.cactus_industries.dbInterface.GenericDBInteractions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BumperDB {
    
    static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    
    public static boolean addPointsTo(long ID, Calendar date, double points){
        date.set(Calendar.DATE, 1);
        
        return addPointsTo(ID, SDF.format(date.getTime()), points);
    }
    
    public static boolean addPointsTo(long ID, String date, double points){
        String query = "UPDATE BumpScore SET score=score+" + points + "WHERE id=" + ID + " AND mois='" + date + "';";
        if(GenericDBInteractions.executeInsertUpdateDeleteStatement(query))
            return true;
        
        query = "INSERT INTO BumpScore (id, mois, score) VALUES ('" + ID + "','" + date + "','" + points + "');";
        return GenericDBInteractions.executeInsertUpdateDeleteStatement(query);
    }
    
    public static LinkedHashMap<Long, Double> getScoreboard(Calendar date){
        date.set(Calendar.DATE, 1);
        
        return getScoreboard(SDF.format(date.getTime()));
    }
    
    public static LinkedHashMap<Long, Double> getScoreboard(String date){
        String query = "SELECT id, score FROM BumpScore WHERE mois='" + date + "' ORDER BY score DESC;";
    
        Connection conn = DBInterface.getDBConnection();
        
        LinkedHashMap<Long, Double> scoreboard = new LinkedHashMap<>();
        
        if(conn != null)
            // CONNEXION
            try (Statement stmt = conn.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    scoreboard.put(rs.getLong("id"), rs.getDouble("score"));
                }
                return scoreboard;
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
}
