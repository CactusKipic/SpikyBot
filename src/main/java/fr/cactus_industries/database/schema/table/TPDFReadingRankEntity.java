package fr.cactus_industries.database.schema.table;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "t_pdfreading_rank")
@IdClass(TPDFReadingRankEntityPK.class)
public class TPDFReadingRankEntity {
    @Id
    @Column(name = "server")
    private long server;
    @Id
    @Column(name = "role")
    private long role;
    @Column(name = "ranklevel")
    private long rankLevel;
    
    public TPDFReadingRankEntity() {
    }
    
    public TPDFReadingRankEntity(long server, long role, long rankLevel) {
        this.server = server;
        this.role = role;
        this.rankLevel = rankLevel;
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
    
    public long getRankLevel() {
        return rankLevel;
    }
    
    public void setRankLevel(long rankLevel) {
        this.rankLevel = rankLevel;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TPDFReadingRankEntity that = (TPDFReadingRankEntity) o;
        return server == that.server && role == that.role && rankLevel == that.rankLevel;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(server, role, rankLevel);
    }
}
