package fr.cactus_industries.database.schema.table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "premiumserver")
public class TGPremiumServer {
    @Id
    @Column(name = "server")
    private long server;
    @Column(name = "endsub")
    private Date endSub;
    
    public TGPremiumServer() {
    }
    
    public TGPremiumServer(long server, Date endSub) {
        this.server = server;
        this.endSub = endSub;
    }
    
    public long getServer() {
        return server;
    }
    
    public void setServer(long server) {
        this.server = server;
    }
    
    public Date getEndSub() {
        return endSub;
    }
    
    public void setEndSub(Date endSub) {
        this.endSub = endSub;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TGPremiumServer that = (TGPremiumServer) o;
        return server == that.server && endSub.equals(that.endSub);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, endSub);
    }
}
