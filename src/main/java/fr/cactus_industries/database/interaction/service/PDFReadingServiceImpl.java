package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.interaction.repository.PDFReadingChannelRepository;
import fr.cactus_industries.database.interaction.repository.PDFReadingRankRepository;
import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntity;
import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntityPK;
import fr.cactus_industries.database.schema.table.TPDFReadingRankEntity;
import fr.cactus_industries.database.schema.table.TPDFReadingRankEntityPK;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PDFReadingServiceImpl implements PDFReadingService{
    @Autowired
    private PDFReadingRankRepository repositoryRank;
    @Autowired
    private PDFReadingChannelRepository repositoryChannel;
    
    @Override
    public TPDFReadingChannelEntity saveChannel(TPDFReadingChannelEntity s) {
        return repositoryChannel.save(s);
    }
    
    @Override
    public TPDFReadingRankEntity saveRank(TPDFReadingRankEntity s) {
        return repositoryRank.save(s);
    }
    
    @Override
    public boolean canUse(ServerTextChannel textChannel, List<Long> roles) {
        return this.canUse(textChannel.getServer().getId(), textChannel.getId(), roles);
    }
    
    @Override
    public boolean canUse(long server, long channel, List<Long> roles) {
        return repositoryRank.canUse(server, channel, roles);
    }
    
    @Override
    public Optional<TPDFReadingChannelEntity> findChannel(ServerTextChannel textChannel) {
        return repositoryChannel.findById(new TPDFReadingChannelEntityPK(textChannel.getServer().getId(), textChannel.getId()));
    }
    
    @Override
    public List<TPDFReadingChannelEntity> findAllServerChannel(long server) {
        return repositoryChannel.findAllServer(server);
    }
    
    @Override
    public List<TPDFReadingChannelEntity> findAllChannel() {
        return repositoryChannel.findAll();
    }
    
    @Override
    public void deleteChannel(TPDFReadingChannelEntity entity) {
        repositoryChannel.delete(entity);
    }
    
    @Override
    public Optional<TPDFReadingRankEntity> findRank(long server, long role) {
        return repositoryRank.findById(new TPDFReadingRankEntityPK(server, role));
    }
    
    @Override
    public List<TPDFReadingRankEntity> findAllServerRank(long server) {
        return repositoryRank.findAllServer(server);
    }
    
    @Override
    public List<TPDFReadingRankEntity> findAllRank() {
        return repositoryRank.findAll();
    }
    
    @Override
    public void deleteRank(TPDFReadingRankEntity entity) {
        repositoryRank.delete(entity);
    }
}
