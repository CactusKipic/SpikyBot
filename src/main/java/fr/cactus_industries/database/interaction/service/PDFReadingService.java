package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntity;
import fr.cactus_industries.database.schema.table.TPDFReadingRankEntity;
import org.javacord.api.entity.channel.ServerTextChannel;

import java.util.List;
import java.util.Optional;

public interface PDFReadingService {
    
    TPDFReadingChannelEntity saveChannel(TPDFReadingChannelEntity s);
    
    TPDFReadingRankEntity saveRank(TPDFReadingRankEntity s);
    
    boolean canUse(ServerTextChannel textChannel, List<Long> roles);
    
    boolean canUse(long server, long channel, List<Long> roles);
    
    Optional<TPDFReadingChannelEntity> findChannel(ServerTextChannel textChannel);
    
    List<TPDFReadingChannelEntity> findAllServerChannel(long server);
    
    List<TPDFReadingChannelEntity> findAllChannel();
    
    void deleteChannel(TPDFReadingChannelEntity entity);
    
    Optional<TPDFReadingRankEntity> findRank(long server, long channel);
    
    List<TPDFReadingRankEntity> findAllServerRank(long server);
    
    List<TPDFReadingRankEntity> findAllRank();
    
    void deleteRank(TPDFReadingRankEntity entity);
}
