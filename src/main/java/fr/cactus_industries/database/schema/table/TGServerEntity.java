package fr.cactus_industries.database.schema.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "t_g_server", catalog = "spikybot", schema = "public")
public class TGServerEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "join_date",nullable = false)
    private Date joinDate;
    @Column(name = "leave_date")
    private Date leaveDate;
    
    public TGServerEntity() {
    }
    
    public TGServerEntity(Long id, Date joinDate) {
        this.id = id;
        this.joinDate = joinDate;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
    
    public Date getLeaveDate() {
        return leaveDate;
    }
    
    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }
}
