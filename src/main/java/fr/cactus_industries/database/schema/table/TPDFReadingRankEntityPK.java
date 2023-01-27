package fr.cactus_industries.database.schema.table;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class TPDFReadingRankEntityPK implements Serializable {
    @Id
    @Column(name = "server")
    private long server;
    @Id
    @Column(name = "role")
    private long role;
    
    public TPDFReadingRankEntityPK() {
    }
    
    public TPDFReadingRankEntityPK(long server, long role) {
        this.server = server;
        this.role = role;
    }
    
    public long getServer() {
        return server;
    }
    
    public void setServer(long server) {
        this.server = server;
    }
    
    public long getRole() {
        return role;
    }
    
    public void setRole(long role) {
        this.role = role;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TPDFReadingRankEntityPK that = (TPDFReadingRankEntityPK) o;
        return server == that.server && role == that.role;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, role);
    }
}
