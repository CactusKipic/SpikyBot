package fr.cactus_industries.database.schema.table;

import fr.cactus_industries.tools.MessageJsonTicketDeserializationType;
import fr.cactus_industries.tools.tickets.MessageJsonTicket;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "t_ticket_channel")
@IdClass(TTicketChannelEntityPK.class)
public class TTicketChannelEntity {
    @Id
    @Column(name = "server", nullable = false)
    private long server;
    @Id
    @Column(name = "channel", nullable = false)
    private long channel;
    @Column(name = "messageid")
    private Long messageId;
    @Convert(converter = MessageJsonTicketDeserializationType.class)
    @Column(name = "messagejsontool", columnDefinition = "json", nullable = false)
    private MessageJsonTicket messageJsonTicket;
    @Column(name = "granttime", nullable = false)
    private long grantTime = 30;
    @Column(name = "grantlevel", nullable = false)
    private long grantLevel = 0;
    
    public TTicketChannelEntity() {
    }
    
    public TTicketChannelEntity(long server, long channel, Long messageId, MessageJsonTicket messageJsonTicket) {
        this.server = server;
        this.channel = channel;
        this.messageId = messageId;
        this.messageJsonTicket = messageJsonTicket;
    }
    
    public TTicketChannelEntity(long server, long channel, Long messageId, long grantTime, long grantLevel) {
        this.server = server;
        this.channel = channel;
        this.messageId = messageId;
        this.grantTime = grantTime;
        this.grantLevel = grantLevel;
    }
    
    public TTicketChannelEntity(long server, long channel, Long messageId, MessageJsonTicket messageJsonTicket, long grantTime, long grantLevel) {
        this.server = server;
        this.channel = channel;
        this.messageId = messageId;
        this.messageJsonTicket = messageJsonTicket;
        this.grantTime = grantTime;
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
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public MessageJsonTicket getMessageJsonTicket() {
        return messageJsonTicket;
    }
    
    public void setMessageJsonTicket(MessageJsonTicket messageJsonTool) {
        this.messageJsonTicket = messageJsonTool;
    }
    
    public long getGrantTime() {
        return grantTime;
    }
    
    public void setGrantTime(long grantTime) {
        this.grantTime = grantTime;
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
        TTicketChannelEntity that = (TTicketChannelEntity) o;
        return server == that.server && channel == that.channel && messageId == that.messageId && grantTime == that.grantTime && grantLevel == that.grantLevel && Objects.equals(messageJsonTicket, that.messageJsonTicket);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, channel, messageId, messageJsonTicket, grantTime, grantLevel);
    }
}
