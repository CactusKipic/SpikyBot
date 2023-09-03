package fr.cactus_industries.database.schema.table;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class TTicketChannelEntityPK implements Serializable {
    @Id
    @Column(name = "server")
    private long server;
    @Id
    @Column(name = "channel")
    private long channel;
    
    public TTicketChannelEntityPK() {
    }
    
    public TTicketChannelEntityPK(long server, long channel) {
        this.server = server;
        this.channel = channel;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTicketChannelEntityPK that = (TTicketChannelEntityPK) o;
        return server == that.server && channel == that.channel;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, channel);
    }
}
