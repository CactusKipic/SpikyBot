package fr.cactus_industries;

import org.javacord.api.DiscordApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInterface {

    private final Connection conn;
    
    public DBInterface(String url, String user, String pass){
        Connection conn = null;
    
        try{
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Successfully connected to the database.");
        }catch (SQLException e){
            System.out.println("[Error] Could not connect to database.");
            e.printStackTrace();
        }
        this.conn = conn;
    }
    
    public void Disconnect(DiscordApi api){
        api.disconnect();
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
