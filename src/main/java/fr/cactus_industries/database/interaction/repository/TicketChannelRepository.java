package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TTicketChannelEntity;
import fr.cactus_industries.database.schema.table.TTicketChannelEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketChannelRepository extends JpaRepository<TTicketChannelEntity, TTicketChannelEntityPK> {
    
    @Query("SELECT tbl FROM TTicketChannelEntity AS tbl WHERE tbl.server=:server")
    List<TTicketChannelEntity> findAllServer(@Param("server") long server);
    
    @Query("SELECT new fr.cactus_industries.database.schema.table.TTicketChannelEntity(tbl.server, tbl.channel, tbl.messageId, tbl.grantTime, tbl.grantLevel) FROM TTicketChannelEntity AS tbl WHERE tbl.server=:server")
    List<TTicketChannelEntity> findAllServerNoJson(@Param("server") long server);
    
    @Query("SELECT tbl FROM TTicketChannelEntity AS tbl ORDER BY tbl.server ASC")
    List<TTicketChannelEntity> findAllOrdered();
}
