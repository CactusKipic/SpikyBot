package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TTicketRankEntity;
import fr.cactus_industries.database.schema.table.TTicketRankEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRankRepository extends JpaRepository<TTicketRankEntity, TTicketRankEntityPK> {
    
    @Query(value = "SELECT CASE WHEN ch.grantLevel=0 " +
            "THEN true ELSE COALESCE(((SELECT MAX(rank.rankLevel) FROM t_ticket_rank AS rank WHERE rank.server=:server AND rank.role IN :listRoles)>=ch.grantLevel), false) " +
            "END FROM t_ticket_channel AS ch WHERE ch.server=:server AND ch.channel=:channel", nativeQuery = true)
    boolean canUse(@Param("server") long server, @Param("channel") long channel,
                   @Param("listRoles") List<Long> roles);
    
    @Query("SELECT tbl FROM TTicketRankEntity AS tbl WHERE tbl.server=:server")
    List<TTicketRankEntity> findAllServer(@Param("server") long server);
}
