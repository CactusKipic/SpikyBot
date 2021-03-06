package fr.cactus_industries.tools.tickets;

import java.util.Arrays;
import java.util.HashMap;
import org.javacord.api.entity.permission.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import fr.cactus_industries.DBInterface;
import com.google.gson.Gson;
import org.javacord.api.entity.message.Message;
import fr.cactus_industries.tools.messagesaving.MessageJsonTool;
import org.javacord.api.entity.channel.ServerTextChannel;

public class ChannelsTicketHandler {
    
    public static boolean addTicketOnChannel(ServerTextChannel textChannel, MessageJsonTool ticketMessage) {
        return addTicketOnChannel(textChannel, ticketMessage, null);
    }
    
    public static boolean addTicketOnChannel(ServerTextChannel textChannel, MessageJsonTool ticketMessage, Integer time) {
        Message mess = ticketMessage.create().send(textChannel).join();
        long messageID = mess.getId();
        mess.addReaction("\ud83c\udf9f");
        if (addTicketOnChannel(textChannel.getServer().getId(), textChannel.getId(), messageID, ticketMessage, time)) {
            TicketsMessageManager.addChannel(textChannel);
            return true;
        }
        return false;
    }
    
    public static boolean addTicketOnChannel(long serverID, long chanID, long messageID, MessageJsonTool ticketMessage, Integer time) {
        String query = "INSERT INTO TicketsChannel (Server, Channel, MessageID, MessageJsonTool" + (time != null ? ", GrantTime" : "") + ") VALUES ('"
                + serverID + "', '" + chanID + "', '" + messageID + "', '" + new Gson().toJson(ticketMessage) + "'"
                + (time != null ? ", '" + time + "'" : "") + ");";
        
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
    
    public static Long getMessageID(ServerTextChannel textChannel) {
        return getMessageID(textChannel.getServer().getId(), textChannel.getId());
    }
    
    public static Long getMessageID(long serverID, long chanID) {
        String query = "SELECT MessageID from TicketsChannel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    return rs.getLong("MessageID");
                }
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static MessageJsonTool getMessage(ServerTextChannel textChannel) {
        return getMessage(textChannel.getServer().getId(), textChannel.getId());
    }
    
    public static MessageJsonTool getMessage(long serverID, long chanID) {
        String query = "SELECT MessageJsonTool from TicketsChannel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
        
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    return new Gson().fromJson(rs.getString("MessageJsonTool"), MessageJsonTool.class);
                }
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static boolean updateMessage(ServerTextChannel textChannel, MessageJsonTool ticketMessage) {
        return updateMessage(textChannel.getServer().getId(), textChannel.getId(), ticketMessage);
    }
    
    public static boolean updateMessage(long serverID, long chanID, MessageJsonTool ticketMessage) {
        String query = "UPDATE TicketsChannel SET MessageJsonTool='" + new Gson().toJson(ticketMessage) + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DE L'UPDATE
                int rs = stmt.executeUpdate(query);
                return rs != 0; // RENVOIE VRAI SI UNE DONNEE A ETE MAJ
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR LORS DE L'UPDATE
        return false;
    }
    
    public static boolean updateMessageID(ServerTextChannel textChannel, long id) {
        return updateMessageID(textChannel.getServer().getId(), textChannel.getId(), id);
    }
    
    public static boolean updateMessageID(long serverID, long chanID, long messageID) {
        String query = "UPDATE TicketsChannel SET MessageID='" + messageID + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DE L'UPDATE
                int rs = stmt.executeUpdate(query);
                return rs != 0; // RENVOIE VRAI SI UNE DONNEE A ETE MAJ
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR LORS DE L'UPDATE
        return false;
    }
    
    public static boolean isChannelTicketed(ServerTextChannel textChannel) {
        return isChannelTicketed(textChannel.getServer().getId(), textChannel.getId());
    }
    
    public static boolean isChannelTicketed(long serverID, long chanID) {
        String query = "SELECT Server FROM TicketsChannel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
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
    
    public static boolean deleteTicketOnChannel(ServerTextChannel textChannel) {
        try {
            textChannel.getMessageById(getMessageID(textChannel)).join().delete().join();
        }
        catch (Exception e) {
            System.out.println("Error, probably no ticket message found.");
        }
        TicketsMessageManager.removeChannel(textChannel);
        return deleteTicketOnChannel(textChannel.getServer().getId(), textChannel.getId());
    }
    
    public static boolean deleteTicketOnChannel(long serverID, long chanID) {
        String query = "DELETE FROM TicketsChannel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
        
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                if(stmt.executeUpdate(query) != 0) // DELETE TOKEN ROW
                    return true; // SUCCESSFUL DELETION
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR
        return false;
    }
    
    public static boolean doesUserAlreadyGranted(User user) {
        String query = "SELECT UserID from TicketsChannelGranted WHERE Server='" + user.getId() + "';";
        
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
        String query = "Select Server FROM PremiumServer WHERE Server='" + serverID + "';";
        
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
    
    public static boolean deletePremiumServer(Server server) {
        return deletePremiumServer(server.getId());
    }
    
    public static boolean deletePremiumServer(long serverID) {
        String query = "DELETE FROM PremiumServer WHERE Server='" + serverID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DU DELETE
                if(stmt.executeUpdate(query) != 0)
                    return true;
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // RIEN N'A ETE SUPPRIME
        return false;
    }
    
    public static Integer getMaxPermLevelOfUser(Server server, User user) {
        return getMaxPermLevelOfUser(server.getId(), user.getRoles(server).stream().map(role -> "'" + role.getId() + "'").collect(Collectors.joining(", ")));
    }
    
    public static Integer getMaxPermLevelOfUser(long serverID, String roleList) {
        String query = "Select MAX(ranklevel) AS level FROM TicketsRank WHERE Server='" + serverID + "' AND RoleID IN(" + roleList + ");";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    return rs.getInt("level");
                }
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static List<Integer> getGrantsOnChannel(ServerTextChannel channel) {
        return getGrantsOnChannel(channel.getServer().getId(), channel.getId());
    }
    
    public static List<Integer> getGrantsOnChannel(long serverID, long chanID) {
        String query = "Select GrantLevel, GrantTime FROM TicketsChannel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if (!rs.next())
                    return null;
                ArrayList<Integer> res = new ArrayList<>();
                res.add(rs.getInt("grantlevel"));
                res.add(rs.getInt("granttime"));
                return res;
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static boolean setGrantTimeOnChannel(ServerTextChannel channel, int time) {
        return setGrantTimeOnChannel(channel.getServer().getId(), channel.getId(), time);
    }
    
    public static boolean setGrantTimeOnChannel(long serverID, long chanID, int time) {
        String query = "UPDATE TicketsChannel SET GrantTime='" + time + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
        
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DE L'UPDATE
                int rs = stmt.executeUpdate(query);
                return rs != 0; // RENVOIE VRAI SI UNE DONNEE A ETE MAJ
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR LORS DE L'UPDATE
        return false;
    }
    
    public static boolean setPermLevelOnChannel(ServerTextChannel channel, int level) {
        return setPermLevelOnChannel(channel.getServer().getId(), channel.getId(), level);
    }
    
    public static boolean setPermLevelOnChannel(long serverID, long chanID, int level) {
        String query = "UPDATE TicketsChannel SET GrantLevel='" + level + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
        
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DE L'UPDATE
                int rs = stmt.executeUpdate(query);
                return rs != 0; // RENVOIE VRAI SI UNE DONNEE A ETE MAJ
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // ERREUR LORS DE L'UPDATE
        return false;
    }
    
    public static Integer getPermLevelOnChannel(ServerTextChannel channel) {
        return getPermLevelOnChannel(channel.getServer().getId(), channel.getId());
    }
    
    public static Integer getPermLevelOnChannel(long serverID, long chanID) {
        String query = "SELECT GrantLevel from TicketsChannel WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    return rs.getInt("GrantLevel");
                }
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static boolean setPermLevelOfRank(Role channel, int level) {
        return setPermLevelOfRank(channel.getServer().getId(), channel.getId(), level);
    }
    
    public static boolean setPermLevelOfRank(long serverID, long rankID, int level) {
        String query = "INSERT INTO TicketsRank (Server, RoleID, RankLevel) VALUES ('" + serverID + "', '" + rankID + "', '" + level
                + "') ON CONFLICT (Server, RoleID) DO UPDATE SET RankLevel=excluded.RankLevel;";
        
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
    
    public static Integer getPermLevelOfRank(Role role) {
        return getPermLevelOfRank(role.getServer().getId(), role.getId());
    }
    
    public static Integer getPermLevelOfRank(long serverID, long rankID) {
        String query = "SELECT RankLevel from TicketsRank WHERE Server='" + serverID + "' AND RoleID='" + rankID + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next()){
                    return rs.getInt("RankLevel");
                }
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static boolean userGranted(ServerTextChannel textChannel, User user){
        return userGranted(textChannel.getServer().getId(), textChannel.getId(), user.getId());
    }
    
    public static boolean userGranted(long serverID, long chanID, long userID){
        String query = "INSERT INTO TicketsChannelGranted (UserID, Server, Channel) VALUES ('" + userID + "', '" + serverID + "', '" + chanID + "');";
        
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
    
    public static boolean userNoLongerGranted(User user) {
        String query = "DELETE FROM TicketsChannelGranted WHERE UserID='" + user.getId() + "';";
        
        Connection con = DBInterface.getDBConnection();
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // EXECUTION DU DELETE
                if(stmt.executeUpdate(query) != 0)
                    return true;
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // RIEN N'A ETE SUPPRIME
        return false;
    }
    
    public static HashMap<Long, HashMap<Long, ArrayList<Long>>> getAllGrantedUsers() {
        String query = "SELECT Server, Channel, UserID from TicketsChannelGranted;";
        
        Connection con = DBInterface.getDBConnection();
        
        HashMap<Long, HashMap<Long, ArrayList<Long>>> serverChannel = new HashMap<>();
        
    
        if(con != null)
            // CONNEXION
            try (Statement stmt = con.createStatement()){
                // RECUPERATION DES DONNEES
                ResultSet rs = stmt.executeQuery(query);
                
                while (rs.next()) {
                    long server = rs.getLong("Server");
                    HashMap<Long, ArrayList<Long>> channelMap;
                    // Ajout des salons dans les serveurs
                    if (!serverChannel.containsKey(server)) { // Serveur inexistant
                        channelMap = new HashMap<>();
                        serverChannel.put(server, channelMap); // Ajout d'une map pour les salons
                    } else {
                        channelMap = serverChannel.get(server); // Récupération de la map existante
                    }
                    long channel = rs.getLong("Channel");
                    ArrayList<Long> users;
                    // Ajout des users dans les salons
                    if(!channelMap.containsKey(channel)){ // Salon inexistant
                        users = new ArrayList<>();
                        channelMap.put(channel, users); // Ajout d'une liste pour les users
                    } else {
                        users = channelMap.get(channel); // Récupération de la liste existante
                    }
                    users.add(rs.getLong("UserID")); // Ajoute de l'ID du user actuel
                }
                return serverChannel;
            
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        // PAS D'ID ASSOCIE
        return null;
    }
    
    public static HashMap<Long, ArrayList<Long>> getAllTicketedChannel() {
        String query = "SELECT Server, Channel from TicketsChannel;";
        
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
