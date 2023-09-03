package fr.cactus_industries.database.schema.table;

import jakarta.persistence.*;
import org.javacord.api.entity.channel.ServerTextChannel;

import java.util.Objects;

@Entity
@Table(name = "t_pdfreading_channel")
@IdClass(TPDFReadingChannelEntityPK.class)
public class TPDFReadingChannelEntity {
    @Id
    @Column(name = "server")
    private long server;
    @Id
    @Column(name = "channel")
    private long channel;
    @Column(name = "grantlevel")
    private long grantLevel;
    
    public TPDFReadingChannelEntity() {
    }
    
    public TPDFReadingChannelEntity(long server, long channel, long grantLevel) {
        this.server = server;
        this.channel = channel;
        this.grantLevel = grantLevel;
    }
    
    public TPDFReadingChannelEntity(ServerTextChannel textChannel) {
        this.server = textChannel.getServer().getId();
        this.channel = textChannel.getId();
        this.grantLevel = 0;
    }
    
    public TPDFReadingChannelEntity(ServerTextChannel textChannel, long grantLevel) {
        this.server = textChannel.getServer().getId();
        this.channel = textChannel.getId();
        this.grantLevel = grantLevel;
    }
    
    public long getServer() {
        return server;
    }
    
    public void setServer(long server) {
        this.server = server;
    }
    
    public long getChannel() {
        return channel;
    }
    
    public void setChannel(long channel) {
        this.channel = channel;
    }
    
    public long getGrantLevel() {
        return grantLevel;
    }
    
    public void setGrantLevel(long grantLevel) {
        this.grantLevel = grantLevel;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TPDFReadingChannelEntity that = (TPDFReadingChannelEntity) o;
        return server == that.server && channel == that.channel && grantLevel == that.grantLevel;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, channel, grantLevel);
    }
}
