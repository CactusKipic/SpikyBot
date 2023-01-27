package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.interaction.repository.TicketChannelRepository;
import fr.cactus_industries.database.interaction.repository.TicketGrantedRepository;
import fr.cactus_industries.database.interaction.repository.TicketRankRepository;
import fr.cactus_industries.database.schema.table.*;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {
    
    private final TicketChannelRepository repositoryChannel;
    private final TicketRankRepository repositoryRank;
    private final TicketGrantedRepository repositoryGranted;
    
    public TicketServiceImpl(TicketChannelRepository repositoryChannel, TicketRankRepository repositoryRank, TicketGrantedRepository repositoryGranted) {
        this.repositoryChannel = repositoryChannel;
        this.repositoryRank = repositoryRank;
        this.repositoryGranted = repositoryGranted;
    }
    
    @Override
    public TTicketChannelEntity saveChannel(TTicketChannelEntity s) {
        return repositoryChannel.save(s);
    }
    
    @Override
    public void removeChannel(TTicketChannelEntity d) {
        repositoryChannel.delete(d);
    }
    
    @Override
    public TTicketRankEntity saveRank(TTicketRankEntity s) {
        return repositoryRank.save(s);
    }
    
    @Override
    public void removeRank(TTicketRankEntity d) {
        repositoryRank.delete(d);
    }
    
    @Override
    public TTicketGrantedEntity saveGranted(TTicketGrantedEntity s) {
        return repositoryGranted.save(s);
    }
    
    @Override
    public void removeGranted(TTicketGrantedEntity d) {
        repositoryGranted.delete(d);
    }
    
    @Override
    public Optional<TTicketChannelEntity> findChannel(ServerTextChannel textChannel) {
        return findChannel(new TTicketChannelEntityPK(textChannel.getServer().getId(), textChannel.getId()));
    }
    
    @Override
    public Optional<TTicketChannelEntity> findChannel(TTicketChannelEntityPK key) {
        return repositoryChannel.findById(key);
    }
    
    @Override
    public List<TTicketChannelEntity> findAllServerChannel(long server) {
        return repositoryChannel.findAllServer(server);
    }
    
    @Override
    public List<TTicketChannelEntity> findAllServerChannelNoJson(long server) {
        return repositoryChannel.findAllServerNoJson(server);
    }
    
    @Override
    public List<TTicketChannelEntity> findAllChannel() {
        return repositoryChannel.findAllOrdered();
    }
    
    @Override
    public boolean canUse(ServerTextChannel textChannel, List<Long> roles) {
        return canUse(textChannel.getServer().getId(), textChannel.getId(), roles);
    }
    
    @Override
    public boolean canUse(long server, long channel, List<Long> roles) {
        return repositoryRank.canUse(server, channel, roles);
    }
    
    @Override
    public Optional<TTicketRankEntity> findRank(ServerTextChannel textChannel) {
        return findRank(new TTicketRankEntityPK(textChannel.getServer().getId(), textChannel.getId()));
    }
    
    @Override
    public Optional<TTicketRankEntity> findRank(TTicketRankEntityPK key) {
        return repositoryRank.findById(key);
    }
    
    @Override
    public List<TTicketRankEntity> findAllServerRank(long server) {
        return repositoryRank.findAllServer(server);
    }
    
    @Override
    public List<TTicketRankEntity> findAllRank() {
        return repositoryRank.findAll();
    }
    
    @Override
    public Optional<TTicketGrantedEntity> findGranted(ServerTextChannel textChannel, long userId) {
        return repositoryGranted.findById(new TTicketGrantedEntityPK(textChannel.getServer().getId(), textChannel.getId(), userId));
    }
    
    @Override
    public Optional<TTicketGrantedEntity> findGranted(TTicketGrantedEntityPK key) {
        return repositoryGranted.findById(key);
    }
    
    @Override
    public List<TTicketGrantedEntity> findAllServerGranted(long server) {
        return repositoryGranted.findAllByServer(server);
    }
    
    @Override
    public List<TTicketGrantedEntity> findAllGranted() {
        return repositoryGranted.findAll();
    }
    
    @Override
    public long countGrantedOnChannel(ServerTextChannel textChannel) {
        return repositoryGranted.countGrantedByChannel(textChannel.getServer().getId(), textChannel.getId());
    }
}
