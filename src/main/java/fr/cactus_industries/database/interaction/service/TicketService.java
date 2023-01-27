package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.schema.table.*;
import org.javacord.api.entity.channel.ServerTextChannel;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    
    TTicketChannelEntity saveChannel(TTicketChannelEntity s);
    
    void removeChannel(TTicketChannelEntity d);
    
    TTicketRankEntity saveRank(TTicketRankEntity s);
    
    void removeRank(TTicketRankEntity d);
    
    TTicketGrantedEntity saveGranted(TTicketGrantedEntity s);
    
    void removeGranted(TTicketGrantedEntity d);
    
    Optional<TTicketChannelEntity> findChannel(ServerTextChannel textChannel);
    
    Optional<TTicketChannelEntity> findChannel(TTicketChannelEntityPK key);
    
    List<TTicketChannelEntity> findAllServerChannel(long server);
    
    List<TTicketChannelEntity> findAllServerChannelNoJson(long server);
    
    List<TTicketChannelEntity> findAllChannel();
    
    boolean canUse(ServerTextChannel textChannel, List<Long> roles);
    
    boolean canUse(long server, long channel, List<Long> roles);
    
    Optional<TTicketRankEntity> findRank(ServerTextChannel textChannel);
    
    Optional<TTicketRankEntity> findRank(TTicketRankEntityPK key);
    
    List<TTicketRankEntity> findAllServerRank(long server);
    
    List<TTicketRankEntity> findAllRank();
    
    Optional<TTicketGrantedEntity> findGranted(ServerTextChannel textChannel, long userId);
    
    Optional<TTicketGrantedEntity> findGranted(TTicketGrantedEntityPK key);
    
    List<TTicketGrantedEntity> findAllServerGranted(long server);
    
    List<TTicketGrantedEntity> findAllGranted();
    
    long countGrantedOnChannel(ServerTextChannel textChannel);
}
