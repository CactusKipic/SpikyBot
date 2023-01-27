package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TTicketGrantedEntity;
import fr.cactus_industries.database.schema.table.TTicketGrantedEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketGrantedRepository extends JpaRepository<TTicketGrantedEntity, TTicketGrantedEntityPK> {
    
    @Query("SELECT tbl FROM TTicketGrantedEntity AS tbl ORDER BY tbl.server, tbl.channel")
    List<TTicketGrantedEntity> findAllOrdered();
    
    @Query("SELECT tbl FROM TTicketGrantedEntity AS tbl WHERE tbl.server =:#{#server}")
    List<TTicketGrantedEntity> findAllByServer(@Param("server")long server);
    
    @Query("SELECT COUNT(tbl) FROM TTicketGrantedEntity AS tbl WHERE tbl.server = :#{#server} AND tbl.channel = :#{#channel}")
    long countGrantedByChannel(@Param("server") long server, @Param("channel") long channel);
}
