package fr.cactus_industries.database.schema.table;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "t_ticket_granted")
@IdClass(TTicketGrantedEntityPK.class)
public class TTicketGrantedEntity {
    @Id
    @Column(name = "server")
    private long server;
    @Id
    @Column(name = "channel")
    private long channel;
    @Id
    @Column(name = "userid")
    private long userid;
    @Column(name = "endgrant", nullable = false)
    private Date endGrant;
    
    public TTicketGrantedEntity() {
    }
    
    public TTicketGrantedEntity(long server, long channel, long userid, Date endGrant) {
        this.server = server;
        this.channel = channel;
        this.userid = userid;
        this.endGrant = endGrant;
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
    
    public Date getEndGrant() {
        return endGrant;
    }
    
    public void setEndGrant(Date endGrant) {
        this.endGrant = endGrant;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTicketGrantedEntity that = (TTicketGrantedEntity) o;
        return server == that.server && channel == that.channel && userid == that.userid && endGrant.equals(that.endGrant);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, channel, userid, endGrant);
    }
}
