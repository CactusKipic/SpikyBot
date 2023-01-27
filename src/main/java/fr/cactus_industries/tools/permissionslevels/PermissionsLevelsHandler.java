package fr.cactus_industries.tools.permissionslevels;

import fr.cactus_industries.DBInterface;
import fr.cactus_industries.dbInterface.GenericDBInteractions;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class PermissionsLevelsHandler {
    
    public static boolean setPermLevelOfRole(SBPermissionType type, Role role, int level){
        return setPermLevelOfRole(type, role.getServer().getId(), role.getId(), level);
    }
    
    public static boolean setPermLevelOfRole(SBPermissionType type, long serverID, long roleID, int level){
        String query = "INSERT INTO " + type.getTablePermissionLevel() + " (Server, RoleID, RankLevel) VALUES ('" + serverID + "', '" + roleID + "', '" + level
                + "') ON CONFLICT (Server, RoleID) DO UPDATE SET RankLevel=excluded.RankLevel;";
        
        return GenericDBInteractions.executeInsertUpdateDeleteStatement(query);
    }
    
    public static boolean deletePermLevelOfRole(SBPermissionType type, Role role){
        return deletePermLevelOfRole(type, role.getServer().getId(), role.getId());
    }
    
    public static boolean deletePermLevelOfRole(SBPermissionType type, long serverID, long roleID){
        String query = "DELETE FROM " + type.getTablePermissionLevel() + " WHERE Server='" + serverID + "' AND RoleID='" + roleID + "';";
        
        return GenericDBInteractions.executeInsertUpdateDeleteStatement(query);
    }
    
    public static boolean setPermLevelOnChannel(SBPermissionType type, ServerChannel chan, int level){
        return setPermLevelOnChannel(type, chan.getServer().getId(), chan.getId(), level);
    }
    
    private static boolean setPermLevelOnChannel(SBPermissionType type, long serverID, long chanID, int level){
        String query = "UPDATE TicketsChannel SET GrantLevel='" + level + "' WHERE Server='" + serverID + "' AND Channel='" + chanID + "';";
        
        return GenericDBInteractions.executeInsertUpdateDeleteStatement(query);
    }
    
    public static boolean doesUserHavePermissionsOnChannel(SBPermissionType type, Server server, ServerChannel chan, User user){
        Integer permLevel = getPermLevelOnChannel(type, server, chan);
        return permLevel == 0 || server.isAdmin(user) || permLevel <= getMaxPermLevelOfUser(type, server, user);
    }
    
    public static boolean doesUserHavePermissionsOnChannel(SBPermissionType type, ServerTextChannel chan, User user){
        Server server = chan.getServer();
        Integer permLevel = getPermLevelOnChannel(type, server, chan);
        return permLevel == 0 || server.isAdmin(user) || permLevel <= getMaxPermLevelOfUser(type, server, user);
    }
    
    private static Integer getPermLevelOnChannel(SBPermissionType type, Server server, ServerChannel chan){
        return getPermLevelOnChannel(type, server.getId(), chan.getId());
    }
    
    private static Integer getPermLevelOnChannel(SBPermissionType type, long serverID, long chanID){
        String query = "Select GrantLevel FROM " + type.getTableRequiredLevel() + " WHERE Server='" + serverID + "' AND channel='" + chanID + "';";
        
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
    
    private static Integer getMaxPermLevelOfUser(SBPermissionType type, Server server, User user) {
        return getMaxPermLevelOfUser(type, server.getId(), user.getRoles(server).stream().map(role -> "'" + role.getId() + "'").collect(Collectors.joining(", ")));
    }
    
    private static Integer getMaxPermLevelOfUser(SBPermissionType type, long serverID, String roleList) {
        String query = "Select MAX(ranklevel) AS level FROM " + type.getTablePermissionLevel() + " WHERE Server='" + serverID + "' AND RoleID IN(" + roleList + ");";
        
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

}
