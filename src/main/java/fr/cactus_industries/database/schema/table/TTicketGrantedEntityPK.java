package fr.cactus_industries.database.schema.table;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class TTicketGrantedEntityPK implements Serializable {
    @Id
    @Column(name = "server")
    private long server;
    @Id
    @Column(name = "channel")
    private long channel;
    @Id
    @Column(name = "userid")
    private long userid;
    
    public TTicketGrantedEntityPK() {
    }
    
    public TTicketGrantedEntityPK(long server, long channel, long userid) {
        this.server = server;
        this.channel = channel;
        this.userid = userid;
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
    
    public long getUserid() {
        return userid;
    }
    
    public void setUserid(long user) {
        this.userid = user;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTicketGrantedEntityPK that = (TTicketGrantedEntityPK) o;
        return server == that.server && channel == that.channel && userid == that.userid;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, channel, userid);
    }
}
