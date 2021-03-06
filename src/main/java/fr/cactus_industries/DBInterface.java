package fr.cactus_industries;

import fr.cactus_industries.tools.ConfigSpiky;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.javacord.api.DiscordApi;

public class DBInterface {
    
    private static Connection conn = null;
    
    // Renvoie la connexion active en cours ou bien établie une nouvelle connexion
    public static Connection getDBConnection() {
        try {
            if (conn == null || !conn.isValid(3)) {
                DBInterface.establishConnection();
            }
            return conn;
        }
        catch (SQLException throwable) {
            throwable.printStackTrace();
            if (DBInterface.establishConnection()) {
                return conn;
            }
            System.out.println("DBInterface could not establish connection with the Database for the moment.");
            return null;
        }
    }
    
    // Établissement d'une connexion avec la base de donnée
    private static boolean establishConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:" + ConfigSpiky.getConfigString("db.type") + "://" + ConfigSpiky.getConfigString("db.addr")
                    + "/" + ConfigSpiky.getConfigString("name"),
                    ConfigSpiky.getConfigString("db.user"),
                    ConfigSpiky.getConfigString("db.pass"));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    // Fermeture de la connexion
    public static void Disconnect(DiscordApi api) {
        api.disconnect();
        try {
            conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}