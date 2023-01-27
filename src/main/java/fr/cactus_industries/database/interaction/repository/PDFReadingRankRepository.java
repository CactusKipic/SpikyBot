package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntity;
import fr.cactus_industries.database.schema.table.TPDFReadingRankEntity;
import fr.cactus_industries.database.schema.table.TPDFReadingRankEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PDFReadingRankRepository extends JpaRepository<TPDFReadingRankEntity, TPDFReadingRankEntityPK> {
    
    @Query(value = "SELECT CASE WHEN ch.grantLevel=0 " +
            "THEN true ELSE COALESCE(((SELECT MAX(rank.rankLevel) FROM t_pdfreading_rank AS rank WHERE rank.server=:server AND rank.role IN :listRoles)>=ch.grantLevel), false) " +
            "END FROM t_pdfreading_channel AS ch WHERE ch.server=:server AND ch.channel=:channel", nativeQuery = true)
    boolean canUse(@Param("server") long server, @Param("channel") long channel,
                   @Param("listRoles") List<Long> roles);
    
    @Query("SELECT tbl FROM TPDFReadingRankEntity AS tbl WHERE tbl.server=:server")
    List<TPDFReadingRankEntity> findAllServer(@Param("server") long server);
}
